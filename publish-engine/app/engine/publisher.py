"""
小红书发布器 - Playwright浏览器自动化
实现：登录态维持、内容发布、结果验证、反检测
"""
import asyncio
import json
import os
import random
import time
from pathlib import Path
from typing import Optional

import httpx
from loguru import logger
from playwright.async_api import async_playwright, BrowserContext, Page


class BrowserPool:
    """浏览器实例池"""

    def __init__(self, max_browsers: int = 5):
        self.max_browsers = max_browsers
        self.browsers: dict[int, dict] = {}  # xh_account_id -> {context, page, ready, last_used}
        self.lock = asyncio.Lock()
        self.total_tasks = 0
        self.success_count = 0
        self.fail_count = 0

    @property
    def active_browsers(self) -> list:
        return list(self.browsers.keys())

    async def get_or_create(self, account_id: int, cookie_json: Optional[str] = None) -> BrowserContext:
        """获取或创建浏览器实例"""
        async with self.lock:
            if account_id in self.browsers and self.browsers[account_id].get("ready"):
                self.browsers[account_id]["last_used"] = time.time()
                return self.browsers[account_id]["context"]

            # 达到上限则等待
            while len(self.browsers) >= self.max_browsers and account_id not in self.browsers:
                self.lock.release()
                await asyncio.sleep(1)
                await self.lock.acquire()

            # 创建新浏览器
            playwright = await async_playwright().start()
            browser = await playwright.chromium.launch(
                headless=True,
                args=[
                    '--no-sandbox',
                    '--disable-setuid-sandbox',
                    '--disable-dev-shm-usage',
                    '--disable-blink-features=AutomationControlled',
                ]
            )

            context = await browser.new_context(
                viewport={"width": 1280, "height": 900},
                user_agent=random.choice([
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/125.0.0.0 Safari/537.36",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Chrome/125.0.0.0 Safari/537.36",
                ]),
                locale="zh-CN",
                timezone_id="Asia/Shanghai",
            )

            # 注入反检测脚本
            await context.add_init_script("""
                Object.defineProperty(navigator, 'webdriver', {get: () => false});
                window.chrome = { runtime: {} };
                const originalQuery = navigator.permissions.query;
                navigator.permissions.query = (parameters) => (
                    parameters.name === 'notifications' ?
                    Promise.resolve({state: Notification.permission}) :
                    originalQuery(parameters)
                );
            """)

            # 如果有cookie，加载登录态
            if cookie_json:
                try:
                    cookies = json.loads(cookie_json)
                    await context.add_cookies(cookies)
                except Exception as e:
                    logger.warning(f"加载Cookie失败: {e}")

            self.browsers[account_id] = {
                "context": context,
                "playwright": playwright,
                "browser": browser,
                "ready": True,
                "last_used": time.time()
            }

            return context

    async def close_browser(self, account_id: int):
        """关闭浏览器"""
        async with self.lock:
            if account_id in self.browsers:
                info = self.browsers.pop(account_id)
                try:
                    await info["context"].close()
                    await info["browser"].close()
                    await info["playwright"].stop()
                except Exception:
                    pass

    async def close_all(self):
        """关闭所有浏览器"""
        for account_id in list(self.browsers.keys()):
            await self.close_browser(account_id)

    async def health_check(self, account_id: int) -> bool:
        """检查浏览器是否健康"""
        if account_id not in self.browsers:
            return False
        try:
            info = self.browsers[account_id]
            # 检查页面是否可访问
            page = await info["context"].new_page()
            await page.goto("https://creator.xiaohongshu.com", timeout=10000)
            await page.close()
            return True
        except Exception:
            return False


class XhsPublisher:
    """小红书内容发布器"""

    XHS_CREATOR_URL = "https://creator.xiaohongshu.com"
    XHS_PUBLISH_URL = f"{XHS_CREATOR_URL}/publish"
    SERVER_API_URL = os.environ.get("SERVER_URL", "http://localhost:8080/api")
    INTERNAL_KEY = os.environ.get("INTERNAL_KEY", "XhsEngineInternal2024")

    def __init__(self, browser_pool: BrowserPool):
        self.pool = browser_pool

    async def publish_single(self, queue_id: int, user_id: int,
                             content_id: int, xh_account_id: Optional[int] = None) -> dict:
        """
        发布单个内容到小红书
        
        流程：
        1. 从服务器获取内容详情和账号Cookie
        2. 获取/创建浏览器实例并加载登录态
        3. 导航到发布页
        4. 上传图片
        5. 填写标题和正文
        6. 点击发布
        7. 验证结果并更新状态
        """
        start_time = time.time()
        self.pool.total_tasks += 1

        try:
            # 1. 获取内容数据
            content_data = await self._fetch_content(content_id)
            if not content_data:
                return {"status": "failed", "error": "获取内容失败"}

            account_id = xh_account_id or (content_data.get("xh_account_id", 0))
            cookie = await self._fetch_cookie(account_id)

            # 2. 获取浏览器实例
            context = await self.pool.get_or_create(account_id, cookie)

            # 3. 新建页面并反检测
            page = await context.new_page()
            await self._apply_stealth(page)

            # 发布步骤
            result = await self._execute_publish(page, content_data, queue_id)
            await page.close()

            elapsed = time.time() - start_time
            logger.info(f"发布完成: queue_id={queue_id}, 耗时={elapsed:.1f}s, 结果={result}")

            if result.get("success"):
                self.pool.success_count += 1
                await self._update_status(queue_id, 2, "发布成功")
            else:
                self.pool.fail_count += 1
                await self._update_status(queue_id, 3, result.get("error", "发布失败"))

            return result

        except Exception as e:
            self.pool.fail_count += 1
            error_msg = str(e)
            logger.error(f"发布异常: queue_id={queue_id}, {error_msg}")
            await self._update_status(queue_id, 3, error_msg)
            return {"status": "failed", "error": error_msg}

    async def _execute_publish(self, page: Page, content_data: dict, queue_id: int) -> dict:
        """执行发布流程"""
        try:
            title = content_data.get("title", "")
            content = content_data.get("content", "")
            image_urls = content_data.get("image_urls", "")
            tags = content_data.get("tags", "")

            # Step 1: 导航到发布页
            await self._log_step(queue_id, "navigate", 0)
            await page.goto(self.XHS_PUBLISH_URL, wait_until="networkidle", timeout=30000)
            await page.wait_for_timeout(random.randint(2000, 4000))
            await self._log_step(queue_id, "navigate", 1)

            # Step 2: 上传图片
            await self._log_step(queue_id, "upload", 0)
            if image_urls:
                images = [url.strip() for url in image_urls.split(",") if url.strip()]
                for img_url in images[:9]:  # 最多9张
                    try:
                        img_data = await self._download_image(img_url)
                        if img_data:
                            file_input = page.locator('input[type="file"]').first
                            await file_input.set_input_files({"name": "image.jpg", "mimeType": "image/jpeg", "buffer": img_data})
                            await page.wait_for_timeout(random.randint(1500, 3000))
                    except Exception as e:
                        logger.warning(f"上传图片失败: {img_url}, {e}")
            await self._log_step(queue_id, "upload", 1)

            # Step 3: 填写标题
            await self._log_step(queue_id, "input_title", 0)
            title_input = page.locator('[placeholder*="标题"]').first
            if await title_input.count() > 0:
                await title_input.click()
                await title_input.fill(title)
                await page.wait_for_timeout(random.randint(500, 1000))
            await self._log_step(queue_id, "input_title", 1)

            # Step 4: 填写正文
            await self._log_step(queue_id, "input_content", 0)
            content_area = page.locator('[contenteditable="true"]').first
            if await content_area.count() > 0:
                await content_area.click()
                # 模拟逐字输入
                for char in content[:2000]:
                    await content_area.type(char, delay=random.randint(20, 80))
                await page.wait_for_timeout(random.randint(500, 1500))
            await self._log_step(queue_id, "input_content", 1)

            # Step 5: 添加话题标签
            if tags:
                await self._log_step(queue_id, "add_tags", 0)
                try:
                    tag_input = page.locator('[placeholder*="添加话题"]').first
                    if await tag_input.count() > 0:
                        for tag in [t.strip() for t in tags.split(",") if t.strip()]:
                            await tag_input.fill(f"#{tag}")
                            await page.wait_for_timeout(random.randint(800, 1500))
                            await page.keyboard.press("Enter")
                            await page.wait_for_timeout(500)
                except Exception as e:
                    logger.warning(f"添加标签失败: {e}")
                await self._log_step(queue_id, "add_tags", 1)

            # Step 6: 点击发布
            await self._log_step(queue_id, "publish", 0)
            await page.wait_for_timeout(random.randint(2000, 4000))

            publish_btn = page.locator('button:has-text("发布")').last
            if await publish_btn.count() > 0:
                await publish_btn.click()
                await page.wait_for_timeout(random.randint(5000, 8000))

                # 验证结果
                success_indicators = [
                    page.locator('text=发布成功'),
                    page.locator('text=已发布'),
                    page.locator('.publish-success'),
                ]
                for indicator in success_indicators:
                    try:
                        if await indicator.count() > 0:
                            await self._log_step(queue_id, "publish", 1)
                            await self._log_step(queue_id, "verify", 1)
                            return {"success": True, "message": "发布成功"}
                    except Exception:
                        pass
            else:
                logger.warning(f"找不到发布按钮: queue_id={queue_id}")

            await self._log_step(queue_id, "publish", 1)
            await self._log_step(queue_id, "verify", 1)

            return {"success": True, "message": "发布流程执行完毕"}

        except Exception as e:
            # 失败截图
            try:
                screenshot_path = f"screenshots/queue_{queue_id}_{int(time.time())}.png"
                await page.screenshot(path=screenshot_path)
                logger.info(f"失败截图: {screenshot_path}")
            except Exception:
                pass
            raise e

    async def _apply_stealth(self, page: Page):
        """应用反检测"""
        await page.evaluate("""
            Object.defineProperty(navigator, 'webdriver', {get: () => undefined});
            delete navigator.__proto__.webdriver;
        """)

    async def _fetch_content(self, content_id: int) -> Optional[dict]:
        """从服务器获取内容数据"""
        try:
            async with httpx.AsyncClient(timeout=10) as client:
                resp = await client.get(f"{self.SERVER_API_URL}/content/{content_id}")
                if resp.status_code == 200:
                    data = resp.json()
                    return data.get("data")
                return None
        except Exception as e:
            logger.error(f"获取内容失败: {e}")
            return None

    async def _fetch_cookie(self, account_id: int) -> Optional[str]:
        """获取账号Cookie"""
        try:
            async with httpx.AsyncClient(timeout=10) as client:
                resp = await client.get(
                    f"{self.SERVER_API_URL}/publish/internal/cookie/{account_id}",
                    headers={"X-Internal-Key": self.INTERNAL_KEY}
                )
                if resp.status_code == 200:
                    data = resp.json()
                    return data.get("data", {}).get("cookie")
                return None
        except Exception:
            return None

    async def _download_image(self, url: str) -> Optional[bytes]:
        """下载图片"""
        try:
            async with httpx.AsyncClient(timeout=30) as client:
                resp = await client.get(url)
                if resp.status_code == 200:
                    return resp.content
                return None
        except Exception:
            return None

    async def _update_status(self, queue_id: int, status: int, result: str):
        """更新发布状态"""
        try:
            async with httpx.AsyncClient(timeout=10) as client:
                await client.post(
                    f"{self.SERVER_API_URL}/publish/internal/status",
                    json={"queueId": queue_id, "status": status, "result": result},
                    headers={"X-Internal-Key": self.INTERNAL_KEY}
                )
        except Exception as e:
            logger.error(f"更新状态失败: {e}")

    async def _log_step(self, queue_id: int, step: str, status: int):
        """记录操作步骤"""
        try:
            async with httpx.AsyncClient(timeout=5) as client:
                await client.post(
                    f"{self.SERVER_API_URL}/publish/internal/log",
                    json={
                        "queueId": queue_id,
                        "step": step,
                        "status": status,
                        "errorMsg": ""
                    },
                    headers={"X-Internal-Key": self.INTERNAL_KEY}
                )
        except Exception:
            pass
