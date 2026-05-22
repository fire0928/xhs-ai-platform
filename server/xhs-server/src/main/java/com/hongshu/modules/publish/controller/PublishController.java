package com.hongshu.modules.publish.controller;

import com.hongshu.common.model.ApiResponse;
import com.hongshu.modules.publish.entity.PublishQueue;
import com.hongshu.modules.publish.service.PublishService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/publish")
public class PublishController {

    private final PublishService publishService;

    public PublishController(PublishService publishService) {
        this.publishService = publishService;
    }

    /**
     * 添加到发布队列
     */
    @PostMapping("/queue/add")
    public ApiResponse<PublishQueue> addToQueue(Authentication auth,
                                                 @RequestBody Map<String, Object> body) {
        Long userId = (Long) auth.getPrincipal();
        Long contentId = Long.valueOf(body.get("contentId").toString());
        Long xhAccountId = body.containsKey("xhAccountId") && body.get("xhAccountId") != null
                ? Long.valueOf(body.get("xhAccountId").toString()) : null;
        LocalDateTime publishTime = body.containsKey("publishTime")
                ? LocalDateTime.parse(body.get("publishTime").toString())
                : LocalDateTime.now();

        return ApiResponse.ok(publishService.addToQueue(userId, contentId, xhAccountId, publishTime));
    }

    /**
     * 立即发布
     */
    @PostMapping("/publish-now")
    public ApiResponse<PublishQueue> publishNow(Authentication auth,
                                                 @RequestBody Map<String, Object> body) {
        Long userId = (Long) auth.getPrincipal();
        Long contentId = Long.valueOf(body.get("contentId").toString());
        Long xhAccountId = body.containsKey("xhAccountId") && body.get("xhAccountId") != null
                ? Long.valueOf(body.get("xhAccountId").toString()) : null;
        return ApiResponse.ok(publishService.publishNow(userId, contentId, xhAccountId));
    }

    /**
     * 获取发布队列
     */
    @GetMapping("/queue")
    public ApiResponse<List<PublishQueue>> getQueues(Authentication auth,
                                                      @RequestParam(required = false) Integer status) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok(publishService.getUserQueues(userId, status));
    }

    /**
     * 修改队列项
     */
    @PutMapping("/queue/{queueId}")
    public ApiResponse<?> updateQueueItem(
            @PathVariable Long queueId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishTime,
            @RequestParam(required = false) Long xhAccountId,
            @RequestParam(required = false) Integer sortOrder) {
        publishService.updateQueueItem(queueId, publishTime, xhAccountId, sortOrder);
        return ApiResponse.ok();
    }

    /**
     * 从队列移除
     */
    @DeleteMapping("/queue/{queueId}")
    public ApiResponse<?> removeFromQueue(@PathVariable Long queueId) {
        publishService.removeFromQueue(queueId);
        return ApiResponse.ok();
    }

    /**
     * 队列统计
     */
    @GetMapping("/queue/stats")
    public ApiResponse<Map<String, Object>> getQueueStats(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok(publishService.getQueueStats(userId));
    }
}
