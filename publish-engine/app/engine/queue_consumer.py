"""
Redis Stream 发布任务消费者
持续监听 publish:queue:stream，收到任务后调用 XhsPublisher 执行发布
"""
import asyncio
import json
import os

import redis.asyncio as redis
from loguru import logger

from app.engine.publisher import XhsPublisher

STREAM_KEY = "publish:queue:stream"
CONSUMER_GROUP = "publish_engine_group"
CONSUMER_NAME = f"engine_{os.environ.get('HOSTNAME', 'local')}"
MAX_RETRY = 2


class QueueConsumer:
    """发布任务消费者：从 Redis Stream 拉取任务并执行"""

    def __init__(self, redis_host: str, redis_port: int,
                 redis_password: str, publisher: XhsPublisher):
        self.redis_host = redis_host
        self.redis_port = redis_port
        self.redis_password = redis_password
        self.publisher = publisher
        self.running = False
        self.redis: redis.Redis | None = None

    async def _connect(self) -> redis.Redis:
        """建立 Redis 连接并确保消费组存在"""
        if self.redis is not None:
            return self.redis

        self.redis = redis.Redis(
            host=self.redis_host,
            port=self.redis_port,
            password=self.redis_password or None,
            decode_responses=True,
        )
        await self.redis.ping()

        # 创建消费组（忽略已存在错误）
        try:
            await self.redis.xgroup_create(STREAM_KEY, CONSUMER_GROUP, id="0", mkstream=True)
            logger.info(f"创建消费组: {CONSUMER_GROUP}")
        except redis.ResponseError as e:
            if "BUSYGROUP" not in str(e):
                raise

        return self.redis

    async def run(self):
        """主循环：持续监听 Stream"""
        self.running = True
        logger.info(f"QueueConsumer 启动: stream={STREAM_KEY}, consumer={CONSUMER_NAME}")

        while self.running:
            try:
                r = await self._connect()

                # 从消费组读取新消息，超时 5000ms 后自动开启下一轮
                messages = await r.xreadgroup(
                    groupname=CONSUMER_GROUP,
                    consumername=CONSUMER_NAME,
                    streams={STREAM_KEY: ">"},
                    count=1,
                    block=5000,
                )

                if messages is None:
                    continue

                for stream_name, entries in messages:
                    for msg_id, fields in entries:
                        await self._handle_message(msg_id, fields)

            except asyncio.CancelledError:
                logger.info("QueueConsumer 被取消")
                break
            except Exception as e:
                logger.error(f"消费异常，3秒后重试: {e}")
                await asyncio.sleep(3)

        # 清理
        if self.redis:
            await self.redis.close()
            self.redis = None
        logger.info("QueueConsumer 已停止")

    async def _handle_message(self, msg_id: str, fields: dict):
        """处理单条消息"""
        try:
            queue_id = int(fields.get("queueId", "0"))
            user_id = int(fields.get("userId", "0"))
            content_id = int(fields.get("contentId", "0"))
            xh_account_id = int(fields.get("xhAccountId", "0") or "0")

            if queue_id == 0 or content_id == 0:
                logger.warning(f"无效消息: {msg_id}, fields={fields}")
                await self._ack(msg_id)
                return

            logger.info(
                f"收到发布任务: queue_id={queue_id}, content_id={content_id}, "
                f"user_id={user_id}, xh_account_id={xh_account_id}"
            )

            result = await self.publisher.publish_single(
                queue_id=queue_id,
                user_id=user_id,
                content_id=content_id,
                xh_account_id=xh_account_id if xh_account_id > 0 else None,
            )

            logger.info(
                f"发布完成: queue_id={queue_id}, success={result.get('success')}, "
                f"message={result.get('message', result.get('error', ''))}"
            )

        except Exception as e:
            logger.error(f"处理任务异常: {msg_id}, {e}")
        finally:
            await self._ack(msg_id)

    async def _ack(self, msg_id: str):
        """确认消息并删除"""
        try:
            if self.redis:
                await self.redis.xack(STREAM_KEY, CONSUMER_GROUP, msg_id)
                await self.redis.xdel(STREAM_KEY, msg_id)
        except Exception:
            pass

    async def stop(self):
        """停止消费"""
        self.running = False
