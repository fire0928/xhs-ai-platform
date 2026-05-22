"""
小红书发布引擎 - FastAPI服务
负责通过Playwright浏览器自动化完成小红书内容发布
"""
import asyncio
import os
import sys
from contextlib import asynccontextmanager

from fastapi import FastAPI
from loguru import logger

from app.api.routes import router as api_router
from app.engine.publisher import XhsPublisher, BrowserPool
from app.engine.queue_consumer import QueueConsumer

# 配置日志
LOG_DIR = os.environ.get("LOG_DIR", "logs")
os.makedirs(LOG_DIR, exist_ok=True)
logger.add(os.path.join(LOG_DIR, "publish_engine.log"), rotation="10MB", retention="7 days")
logger.add(sys.stderr, level="INFO")


@asynccontextmanager
async def lifespan(app: FastAPI):
    """应用生命周期管理"""
    logger.info("=== 发布引擎启动 ===")

    # 初始化浏览器池
    redis_host = os.environ.get("REDIS_HOST", "localhost")
    redis_port = int(os.environ.get("REDIS_PORT", "6379"))
    redis_pass = os.environ.get("REDIS_PASS", "")

    app.state.browser_pool = BrowserPool(max_browsers=5)
    app.state.publisher = XhsPublisher(app.state.browser_pool)

    # 启动Redis队列消费者
    app.state.queue_consumer = QueueConsumer(
        redis_host=redis_host,
        redis_port=redis_port,
        redis_password=redis_pass,
        publisher=app.state.publisher
    )
    app.state.consumer_task = asyncio.create_task(app.state.queue_consumer.run())

    logger.info("发布引擎就绪，等待发布任务...")
    yield

    # 清理
    logger.info("=== 发布引擎关闭 ===")
    if hasattr(app.state, 'consumer_task'):
        app.state.consumer_task.cancel()
    await app.state.browser_pool.close_all()


app = FastAPI(
    title="小红书发布引擎",
    description="Playwright驱动的自动发布微服务",
    version="1.0.0",
    lifespan=lifespan
)

app.include_router(api_router, prefix="/api/publish-engine")


@app.get("/health")
async def health_check():
    """健康检查"""
    return {
        "status": "ok",
        "service": "xhs-publish-engine",
        "browsers_active": len(app.state.browser_pool.active_browsers)
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8001, reload=True)
