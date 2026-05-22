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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AdminService {

    private final UserMapper userMapper;
    private final AiContentMapper contentMapper;
    private final ApiCallLogMapper callLogMapper;
    private final PublishQueueMapper publishQueueMapper;
    private final XiaohongshuAccountMapper accountMapper;
    private final CryptoUtil cryptoUtil;

    public AdminService(UserMapper userMapper, AiContentMapper contentMapper,
                        ApiCallLogMapper callLogMapper, PublishQueueMapper publishQueueMapper,
                        XiaohongshuAccountMapper accountMapper, CryptoUtil cryptoUtil) {
        this.userMapper = userMapper;
        this.contentMapper = contentMapper;
        this.callLogMapper = callLogMapper;
        this.publishQueueMapper = publishQueueMapper;
        this.accountMapper = accountMapper;
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
        long totalContents = contentMapper.selectCount(null);
        long pendingPublishes = publishQueueMapper.selectCount(
                new LambdaQueryWrapper<PublishQueue>().eq(PublishQueue::getPublishStatus, 0));
        long publishedToday = publishQueueMapper.selectCount(
                new LambdaQueryWrapper<PublishQueue>()
                        .eq(PublishQueue::getPublishStatus, 2)
                        .ge(PublishQueue::getActualPublishTime, LocalDate.now().atStartOfDay()));

        return Map.of(
                "totalUsers", totalUsers,
                "activeUsers", activeUsers,
                "totalContents", totalContents,
                "pendingPublishes", pendingPublishes,
                "publishedToday", publishedToday,
                "systemHealth", "99.8%"
        );
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
