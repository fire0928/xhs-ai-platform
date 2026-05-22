package com.hongshu.modules.account.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.exception.BusinessException;
import com.hongshu.common.util.CryptoUtil;
import com.hongshu.modules.account.entity.XiaohongshuAccount;
import com.hongshu.modules.account.entity.XhAccountData;
import com.hongshu.modules.account.mapper.XiaohongshuAccountMapper;
import com.hongshu.modules.account.mapper.XhAccountDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService extends ServiceImpl<XiaohongshuAccountMapper, XiaohongshuAccount> {

    private final XhAccountDataMapper xhAccountDataMapper;
    private final CryptoUtil cryptoUtil;

    private static final int MAX_BIND_COUNT = 3;

    /**
     * 获取用户绑定的账号列表
     */
    public List<XiaohongshuAccount> getUserAccounts(Long userId) {
        return list(new LambdaQueryWrapper<XiaohongshuAccount>()
                .eq(XiaohongshuAccount::getUserId, userId)
                .orderByDesc(XiaohongshuAccount::getIsDefault)
                .orderByDesc(XiaohongshuAccount::getBindTime));
    }

    /**
     * 绑定小红书账号
     */
    @Transactional
    public XiaohongshuAccount bindAccount(Long userId, String account, String password) {
        // 检查绑定数量
        long count = count(new LambdaQueryWrapper<XiaohongshuAccount>()
                .eq(XiaohongshuAccount::getUserId, userId));
        if (count >= MAX_BIND_COUNT) {
            throw new BusinessException(400, "最多绑定" + MAX_BIND_COUNT + "个小红书账号");
        }

        // 检查是否已绑定
        String encryptedAccount = cryptoUtil.aesEncrypt(account);
        XiaohongshuAccount existing = getOne(new LambdaQueryWrapper<XiaohongshuAccount>()
                .eq(XiaohongshuAccount::getUserId, userId)
                .eq(XiaohongshuAccount::getXhAccount, encryptedAccount));
        if (existing != null) {
            throw new BusinessException(400, "该账号已绑定");
        }

        XiaohongshuAccount entity = new XiaohongshuAccount();
        entity.setUserId(userId);
        entity.setXhAccount(encryptedAccount);
        entity.setXhPassword(password != null ? cryptoUtil.aesEncrypt(password) : null);
        entity.setBindTime(LocalDateTime.now());
        entity.setIsDefault(count == 0 ? 1 : 0);
        entity.setStatus(1);
        entity.setSessionStatus(0);
        save(entity);

        // 脱敏返回
        entity.setXhAccount(account);
        entity.setXhPassword(null);
        return entity;
    }

    /**
     * 解绑账号
     */
    public void unbindAccount(Long userId, Long accountId) {
        XiaohongshuAccount account = getById(accountId);
        if (account == null || !account.getUserId().equals(userId)) {
            throw new BusinessException(404, "账号不存在");
        }
        removeById(accountId);
    }

    /**
     * 获取账号某日数据
     */
    public XhAccountData getAccountDayData(Long accountId, LocalDate date) {
        if (date == null) date = LocalDate.now();
        LambdaQueryWrapper<XhAccountData> wrapper = new LambdaQueryWrapper<XhAccountData>()
                .eq(XhAccountData::getXhAccountId, accountId)
                .eq(XhAccountData::getRecordDate, date);
        return xhAccountDataMapper.selectOne(wrapper);
    }

    /**
     * 获取账号历史数据
     */
    public List<XhAccountData> getAccountHistoryData(Long accountId, LocalDate startDate, LocalDate endDate) {
        return xhAccountDataMapper.selectList(new LambdaQueryWrapper<XhAccountData>()
                .eq(XhAccountData::getXhAccountId, accountId)
                .ge(startDate != null, XhAccountData::getRecordDate, startDate)
                .le(endDate != null, XhAccountData::getRecordDate, endDate)
                .orderByAsc(XhAccountData::getRecordDate));
    }

    /**
     * 更新账号Cookie(发布引擎用)
     */
    public void updateCookie(Long accountId, String cookieJson, Integer sessionStatus) {
        XiaohongshuAccount account = getById(accountId);
        if (account == null) return;
        if (StrUtil.isNotBlank(cookieJson)) {
            account.setCookieJson(cryptoUtil.aesEncrypt(cookieJson));
        }
        if (sessionStatus != null) {
            account.setSessionStatus(sessionStatus);
        }
        account.setLastActiveTime(LocalDateTime.now());
        updateById(account);
    }

    /**
     * 获取Cookie原文(发布引擎用)
     */
    public String getCookiePlainText(Long accountId) {
        XiaohongshuAccount account = getById(accountId);
        if (account == null || StrUtil.isBlank(account.getCookieJson())) return null;
        return cryptoUtil.aesDecrypt(account.getCookieJson());
    }
}
