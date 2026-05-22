package com.hongshu.modules.publish.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.exception.BusinessException;
import com.hongshu.modules.account.entity.XiaohongshuAccount;
import com.hongshu.modules.content.entity.AiContent;
import com.hongshu.modules.publish.entity.PublishQueue;
import com.hongshu.modules.publish.entity.PublishLog;
import com.hongshu.modules.publish.mapper.PublishQueueMapper;
import com.hongshu.modules.publish.mapper.PublishLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PublishService extends ServiceImpl<PublishQueueMapper, PublishQueue> {

    private static final Logger logger = LoggerFactory.getLogger(PublishService.class);

    private final PublishLogMapper publishLogMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${publish.engine-url}")
    private String engineUrl;

    @Value("${publish.max-retry}")
    private int maxRetry;

    public PublishService(PublishLogMapper publishLogMapper, RedisTemplate<String, Object> redisTemplate) {
        this.publishLogMapper = publishLogMapper;
        this.redisTemplate = redisTemplate;
    }

    private static final String QUEUE_STREAM_KEY = "publish:queue:stream";
    private static final int MAX_RETRY_COUNT = 2;

    /**
     * 添加内容到发布队列
     */
    @Transactional
    public PublishQueue addToQueue(Long userId, Long contentId, Long xhAccountId, LocalDateTime publishTime) {
        // 检查内容是否已审核通过
        PublishQueue existing = getOne(new LambdaQueryWrapper<PublishQueue>()
                .eq(PublishQueue::getContentId, contentId));
        if (existing != null) {
            throw new BusinessException(400, "该内容已在发布队列中");
        }

        PublishQueue queue = new PublishQueue();
        queue.setUserId(userId);
        queue.setContentId(contentId);
        queue.setXhAccountId(xhAccountId);
        queue.setPublishTime(publishTime);
        queue.setPublishStatus(0);
        queue.setRetryCount(0);
        queue.setSortOrder(getNextSortOrder(userId));
        queue.setCreateTime(LocalDateTime.now());
        save(queue);

        return queue;
    }

    /**
     * 立即发布
     */
    @Transactional
    public PublishQueue publishNow(Long userId, Long contentId, Long xhAccountId) {
        PublishQueue queue = addToQueue(userId, contentId, xhAccountId, LocalDateTime.now());
        // 推送到Redis Stream触发发布引擎
        Map<String, Object> taskMap = Map.of(
                "queueId", String.valueOf(queue.getId()),
                "userId", String.valueOf(userId),
                "contentId", String.valueOf(contentId),
                "xhAccountId", String.valueOf(xhAccountId != null ? xhAccountId : 0)
        );
        redisTemplate.opsForStream().add(QUEUE_STREAM_KEY, taskMap);
        return queue;
    }

    /**
     * 获取用户的发布队列
     */
    public List<PublishQueue> getUserQueues(Long userId, Integer status) {
        LambdaQueryWrapper<PublishQueue> wrapper = new LambdaQueryWrapper<PublishQueue>()
                .eq(PublishQueue::getUserId, userId);
        if (status != null) wrapper.eq(PublishQueue::getPublishStatus, status);
        wrapper.orderByAsc(PublishQueue::getPublishTime).orderByAsc(PublishQueue::getSortOrder);
        return list(wrapper);
    }

    /**
     * 更新队列项
     */
    @Transactional
    public void updateQueueItem(Long queueId, LocalDateTime publishTime, Long xhAccountId, Integer sortOrder) {
        PublishQueue queue = getById(queueId);
        if (queue == null || queue.getPublishStatus() != 0) {
            throw new BusinessException(400, "仅待发布状态可修改");
        }
        if (publishTime != null) queue.setPublishTime(publishTime);
        if (xhAccountId != null) queue.setXhAccountId(xhAccountId);
        if (sortOrder != null) queue.setSortOrder(sortOrder);
        updateById(queue);
    }

    /**
     * 从队列中移除
     */
    public void removeFromQueue(Long queueId) {
        PublishQueue queue = getById(queueId);
        if (queue == null || queue.getPublishStatus() == 2) {
            throw new BusinessException(400, "已发布的内容不可删除");
        }
        removeById(queueId);
    }

    /**
     * 更新发布状态（发布引擎回调）
     */
    @Transactional
    public void updatePublishStatus(Long queueId, int status, String result) {
        PublishQueue queue = getById(queueId);
        if (queue == null) return;

        queue.setPublishStatus(status);
        if (status == 2) {
            queue.setActualPublishTime(LocalDateTime.now());
        }
        if (result != null) queue.setPublishResult(result);
        updateById(queue);
    }

    /**
     * 记录发布步骤日志
     */
    public void addPublishLog(Long queueId, String step, int status, String errorMsg, int durationMs) {
        PublishLog plog = new PublishLog();
        plog.setQueueId(queueId);
        plog.setStep(step);
        plog.setStatus(status);
        plog.setErrorMsg(errorMsg);
        plog.setDurationMs(durationMs);
        plog.setCreateTime(LocalDateTime.now());
        publishLogMapper.insert(plog);
    }

    /**
     * 获取队列统计
     */
    public Map<String, Object> getQueueStats(Long userId) {
        List<PublishQueue> allQueues = getUserQueues(userId, null);
        long todayPending = allQueues.stream()
                .filter(q -> q.getPublishTime() != null
                        && q.getPublishTime().toLocalDate().equals(LocalDateTime.now().toLocalDate())
                        && q.getPublishStatus() == 0)
                .count();
        long totalSuccess = allQueues.stream().filter(q -> q.getPublishStatus() == 2).count();
        long totalFailed = allQueues.stream().filter(q -> q.getPublishStatus() == 3).count();
        double successRate = totalSuccess + totalFailed > 0
                ? (double) totalSuccess / (totalSuccess + totalFailed) * 100 : 100;

        return Map.of(
                "todayPending", todayPending,
                "weekPlan", allQueues.stream().filter(q -> q.getPublishStatus() <= 1).count(),
                "totalSuccess", totalSuccess,
                "totalFailed", totalFailed,
                "successRate", Math.round(successRate * 10) / 10.0
        );
    }

    /**
     * 定时扫描待发布任务（每分钟）
     */
    @Scheduled(cron = "0 * * * * ?")
    public void scanPendingTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<PublishQueue> pendingTasks = list(new LambdaQueryWrapper<PublishQueue>()
                .eq(PublishQueue::getPublishStatus, 0)
                .le(PublishQueue::getPublishTime, now.plusMinutes(1))
                .le(PublishQueue::getRetryCount, MAX_RETRY_COUNT));

        for (PublishQueue task : pendingTasks) {
            Map<String, Object> taskMap = Map.of(
                    "queueId", String.valueOf(task.getId()),
                    "userId", String.valueOf(task.getUserId()),
                    "contentId", String.valueOf(task.getContentId()),
                    "xhAccountId", String.valueOf(task.getXhAccountId() != null ? task.getXhAccountId() : 0)
            );
            redisTemplate.opsForStream().add(QUEUE_STREAM_KEY, taskMap);
            logger.info("推送发布任务: queueId={}, contentId={}", task.getId(), task.getContentId());
        }
    }

    private int getNextSortOrder(Long userId) {
        return (int) count(new LambdaQueryWrapper<PublishQueue>()
                .eq(PublishQueue::getUserId, userId));
    }
}
