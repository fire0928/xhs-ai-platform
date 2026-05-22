package com.hongshu.modules.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.exception.BusinessException;
import com.hongshu.common.util.CryptoUtil;
import com.hongshu.modules.user.entity.User;
import com.hongshu.modules.user.mapper.UserMapper;
import com.hongshu.modules.user.dto.*;
import com.hongshu.modules.content.entity.AiContent;
import com.hongshu.modules.content.mapper.AiContentMapper;
import com.hongshu.modules.ai.entity.ApiCallLog;
import com.hongshu.modules.ai.mapper.ApiCallLogMapper;
import com.hongshu.modules.publish.entity.PublishQueue;
import com.hongshu.modules.publish.mapper.PublishQueueMapper;
import com.hongshu.modules.account.mapper.XiaohongshuAccountMapper;
import com.hongshu.modules.admin.entity.OperationLog;
import com.hongshu.modules.admin.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {

    private final UserMapper userMapper;
    private final AiContentMapper contentMapper;
    private final ApiCallLogMapper callLogMapper;
    private final PublishQueueMapper publishQueueMapper;
    private final XiaohongshuAccountMapper accountMapper;
    private final OperationLogMapper operationLogMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CryptoUtil cryptoUtil;

    public AdminService(UserMapper userMapper, AiContentMapper contentMapper,
                        ApiCallLogMapper callLogMapper, PublishQueueMapper publishQueueMapper,
                        XiaohongshuAccountMapper accountMapper,
                        OperationLogMapper operationLogMapper,
                        RedisTemplate<String, Object> redisTemplate,
                        CryptoUtil cryptoUtil) {
        this.userMapper = userMapper;
        this.contentMapper = contentMapper;
        this.callLogMapper = callLogMapper;
        this.publishQueueMapper = publishQueueMapper;
        this.accountMapper = accountMapper;
        this.operationLogMapper = operationLogMapper;
        this.redisTemplate = redisTemplate;
        this.cryptoUtil = cryptoUtil;
    }

    public IPage<?> pageUsers(int page, int pageSize, String keyword, Integer status, String terminal) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null) wrapper.like(User::getPhone, keyword);
        if (status != null) wrapper.eq(User::getStatus, status);
        if (terminal != null) wrapper.eq(User::getRegisterTerminal, terminal);
        wrapper.orderByDesc(User::getRegisterTime);
        return userMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    public void toggleUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Transactional
    public void resetUserPassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setPassword(cryptoUtil.encodePassword(newPassword));
        userMapper.updateById(user);
    }

    public Map<String, Object> getSystemOverview() {
        long totalUsers = userMapper.selectCount(null);
        long activeUsers = userMapper.countActiveUsers();
        long dau = userMapper.countDau();
        long totalContents = contentMapper.selectCount(null);
        long aiTotal = callLogMapper.selectCount(null);
        long pendingPublishes = publishQueueMapper.selectCount(
                new LambdaQueryWrapper<PublishQueue>().eq(PublishQueue::getPublishStatus, 0));
        long publishedToday = publishQueueMapper.selectCount(
                new LambdaQueryWrapper<PublishQueue>()
                        .eq(PublishQueue::getPublishStatus, 2)
                        .ge(PublishQueue::getActualPublishTime, LocalDate.now().atStartOfDay()));

        // 服务健康检测
        List<Map<String, Object>> services = new ArrayList<>();
        services.add(checkService("API 服务", () -> true));
        services.add(checkService("数据库", () -> { userMapper.selectCount(null); return true; }));
        services.add(checkService("Redis", () -> { redisTemplate.opsForValue().get("_h"); return true; }));
        services.add(checkService("发布引擎", () -> true));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalUsers", totalUsers);
        result.put("activeUsers", activeUsers);
        result.put("dau", dau);
        result.put("totalContents", totalContents);
        result.put("aiTotal", aiTotal);
        result.put("pendingPublishes", pendingPublishes);
        result.put("publishedToday", publishedToday);
        result.put("systemHealth", services.stream().allMatch(s -> "up".equals(s.get("status"))) ? "正常" : "异常");
        result.put("services", services);
        return result;
    }

    private Map<String, Object> checkService(String name, java.util.concurrent.Callable<Boolean> checker) {
        Map<String, Object> svc = new LinkedHashMap<>();
        svc.put("name", name);
        try {
            long t0 = System.currentTimeMillis();
            checker.call();
            long latency = System.currentTimeMillis() - t0;
            svc.put("status", "up");
            svc.put("latency", latency + "ms");
        } catch (Exception e) {
            svc.put("status", "down");
            svc.put("latency", "不可用");
        }
        return svc;
    }

    public IPage<AiContent> pageAllContents(int page, int pageSize, Integer auditStatus,
                                             String keyword, String terminal) {
        LambdaQueryWrapper<AiContent> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) wrapper.eq(AiContent::getAuditStatus, auditStatus);
        if (keyword != null) wrapper.like(AiContent::getTitle, keyword);
        if (terminal != null) wrapper.eq(AiContent::getCreateTerminal, terminal);
        wrapper.orderByDesc(AiContent::getCreateTime);
        return contentMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    public void deleteContent(Long contentId) {
        AiContent content = contentMapper.selectById(contentId);
        if (content != null) {
            content.setAuditStatus(4);
            contentMapper.updateById(content);
        }
    }

    public IPage<ApiCallLog> pageCallLogs(int page, int pageSize, Long apiId, String terminal) {
        LambdaQueryWrapper<ApiCallLog> wrapper = new LambdaQueryWrapper<>();
        if (apiId != null) wrapper.eq(ApiCallLog::getApiId, apiId);
        if (terminal != null) wrapper.eq(ApiCallLog::getTerminal, terminal);
        wrapper.orderByDesc(ApiCallLog::getCreateTime);
        return callLogMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    public List<OperationLog> getRecentLogs(int limit) {
        return operationLogMapper.selectList(
                new LambdaQueryWrapper<OperationLog>()
                        .orderByDesc(OperationLog::getCreateTime)
                        .last("LIMIT " + Math.min(limit, 50)));
    }

    public Map<String, Object> getAnalyticsSummary(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        long newContents = contentMapper.selectCount(
                new LambdaQueryWrapper<AiContent>().ge(AiContent::getCreateTime, since));
        long newUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>().ge(User::getRegisterTime, since));
        long publishedCount = publishQueueMapper.selectCount(
                new LambdaQueryWrapper<PublishQueue>()
                        .eq(PublishQueue::getPublishStatus, 2)
                        .ge(PublishQueue::getActualPublishTime, since));

        return Map.of(
                "newContents", newContents,
                "newUsers", newUsers,
                "publishedCount", publishedCount,
                "avgContentPerUser", totalUsers() > 0 ? (double) newContents / totalUsers() : 0
        );
    }

    public Map<String, Object> getUserAnalytics(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");

        long contentCount = contentMapper.selectCount(
                new LambdaQueryWrapper<AiContent>().eq(AiContent::getUserId, userId));
        long publishedCount = publishQueueMapper.selectCount(
                new LambdaQueryWrapper<PublishQueue>()
                        .eq(PublishQueue::getUserId, userId)
                        .eq(PublishQueue::getPublishStatus, 2));

        return Map.of(
                "userId", userId,
                "totalContents", contentCount,
                "publishedContents", publishedCount,
                "accountCount", accountMapper.selectCount(
                        new LambdaQueryWrapper<com.hongshu.modules.account.entity.XiaohongshuAccount>().eq(
                                com.hongshu.modules.account.entity.XiaohongshuAccount::getUserId, userId))
        );
    }

    private long totalUsers() {
        return userMapper.selectCount(null);
    }
}
