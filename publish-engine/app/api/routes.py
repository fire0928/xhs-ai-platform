"""
发布引擎API路由
"""
from fastapi import APIRouter, HTTPException, Request
from pydantic import BaseModel
from loguru import logger

router = APIRouter()


class PublishTaskRequest(BaseModel):
    queue_id: int
    user_id: int
    content_id: int
    xh_account_id: int | None = None
    is_retry: bool = False


class PublishStatusUpdate(BaseModel):
    queue_id: int
    status: int  # 0-待发布, 1-发布中, 2-已发布, 3-发布失败
    result: str = ""


@router.post("/publish")
async def publish_now(request: Request, task: PublishTaskRequest):
    """手动触发发布任务"""
    try:
        publisher = request.app.state.publisher
        result = await publisher.publish_single(
            queue_id=task.queue_id,
            user_id=task.user_id,
            content_id=task.content_id,
            xh_account_id=task.xh_account_id
        )
        return {"status": "ok", "result": result}
    except Exception as e:
        logger.error(f"发布任务失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/browsers")
async def get_browser_status(request: Request):
    """获取浏览器池状态"""
    pool = request.app.state.browser_pool
    return {
        "active_count": len(pool.active_browsers),
        "max_browsers": pool.max_browsers,
        "browsers": [
            {
                "account_id": bid,
                "status": "online" if info.get("ready") else "busy"
            }
            for bid, info in pool.browsers.items()
        ]
    }


@router.post("/browser/{account_id}/close")
async def close_browser(request: Request, account_id: int):
    """关闭指定浏览器实例"""
    await request.app.state.browser_pool.close_browser(account_id)
    return {"status": "ok"}


@router.get("/stats")
async def get_engine_stats(request: Request):
    """获取引擎统计"""
    pool = request.app.state.browser_pool
    return {
        "total_tasks": pool.total_tasks,
        "success_count": pool.success_count,
        "fail_count": pool.fail_count,
        "success_rate": (
            pool.success_count / max(pool.total_tasks, 1) * 100
        )
    }
