package com.hongshu.modules.publish.controller;

import com.hongshu.common.model.ApiResponse;
import com.hongshu.common.util.CryptoUtil;
import com.hongshu.modules.account.entity.XiaohongshuAccount;
import com.hongshu.modules.account.mapper.XiaohongshuAccountMapper;
import com.hongshu.modules.publish.service.PublishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 发布引擎内部回调API
 * 仅供Python发布引擎调用，不走JWT认证（通过内网/密钥验证）
 */
@Slf4j
@RestController
@RequestMapping("/publish/internal")
@RequiredArgsConstructor
public class PublishInternalController {

    private final XiaohongshuAccountMapper xhAccountMapper;
    private final PublishService publishService;
    private final CryptoUtil cryptoUtil;

    private static final String INTERNAL_KEY = "XhsEngineInternal2024";

    /**
     * 验证内部调用密钥
     */
    private boolean verifyKey(@RequestHeader(value = "X-Internal-Key", required = false) String key) {
        return INTERNAL_KEY.equals(key);
    }

    /**
     * 获取账号Cookie（解密后返回给发布引擎）
     */
    @GetMapping("/cookie/{accountId}")
    public ApiResponse getCookie(@PathVariable Long accountId,
                                  @RequestHeader(value = "X-Internal-Key", required = false) String key) {
        if (!verifyKey(key)) {
            return ApiResponse.unauthorized("内部接口认证失败");
        }

        XiaohongshuAccount account = xhAccountMapper.selectById(accountId);
        if (account == null || account.getDeleted() == 1) {
            return ApiResponse.fail(404, "账号不存在");
        }

        if (account.getCookie() == null || account.getCookie().isEmpty()) {
            return ApiResponse.fail(400, "账号Cookie未配置");
        }

        try {
            String decryptedCookie = cryptoUtil.decrypt(account.getCookie());
            return ApiResponse.ok(Map.of(
                    "accountId", accountId,
                    "nickname", account.getNickname() != null ? account.getNickname() : "",
                    "cookie", decryptedCookie
            ));
        } catch (Exception e) {
            log.error("Cookie解密失败: accountId={}", accountId, e);
            return ApiResponse.fail(500, "Cookie解密失败");
        }
    }

    /**
     * 更新发布状态（发布引擎回调）
     */
    @PostMapping("/status")
    public ApiResponse updateStatus(@RequestBody StatusRequest req,
                                     @RequestHeader(value = "X-Internal-Key", required = false) String key) {
        if (!verifyKey(key)) {
            return ApiResponse.unauthorized("内部接口认证失败");
        }

        log.info("更新发布状态: queueId={}, status={}, result={}", req.queueId, req.status, req.result);
        publishService.updatePublishStatus(req.queueId, req.status, req.result);

        // 如果是发布成功，同时更新内容状态
        if (req.status == 2) {
            try {
                var queue = publishService.getById(req.queueId);
                if (queue != null) {
                    // 可以通过 ContentService 更新，这里简化处理
                    publishService.updatePublishStatus(req.queueId, 2, "发布成功");
                }
            } catch (Exception e) {
                log.error("更新内容状态失败: queueId={}", req.queueId, e);
            }
        }

        return ApiResponse.ok();
    }

    /**
     * 记录发布步骤日志（发布引擎回调）
     */
    @PostMapping("/log")
    public ApiResponse addLog(@RequestBody LogRequest req,
                               @RequestHeader(value = "X-Internal-Key", required = false) String key) {
        if (!verifyKey(key)) {
            return ApiResponse.unauthorized("内部接口认证失败");
        }

        publishService.addPublishLog(req.queueId, req.step, req.status, req.errorMsg, req.durationMs);
        return ApiResponse.ok();
    }

    public record StatusRequest(Long queueId, int status, String result) {}
    public record LogRequest(Long queueId, String step, int status, String errorMsg, int durationMs) {}
}
