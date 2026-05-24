package com.hongshu.modules.account.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("xiaohongshu_account")
public class XiaohongshuAccount {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String xhAccount;
    private String xhPassword;
    private String xhNickname;
    private String xhAvatar;
    private LocalDateTime bindTime;
    private Integer isDefault;     // 0-否, 1-是
    private Integer status;        // 0-失效, 1-正常
    private String cookieJson;
    private String remark;
    private Integer sessionStatus; // 0-离线, 1-在线, 2-过期
    private LocalDateTime lastActiveTime;
    @TableLogic
    private Integer deleted;

    // === Getters and Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getXhAccount() { return xhAccount; }
    public void setXhAccount(String xhAccount) { this.xhAccount = xhAccount; }
    public String getXhPassword() { return xhPassword; }
    public void setXhPassword(String xhPassword) { this.xhPassword = xhPassword; }
    public String getXhNickname() { return xhNickname; }
    public void setXhNickname(String xhNickname) { this.xhNickname = xhNickname; }
    public String getXhAvatar() { return xhAvatar; }
    public void setXhAvatar(String xhAvatar) { this.xhAvatar = xhAvatar; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getBindTime() { return bindTime; }
    public void setBindTime(LocalDateTime bindTime) { this.bindTime = bindTime; }
    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCookieJson() { return cookieJson; }
    public void setCookieJson(String cookieJson) { this.cookieJson = cookieJson; }
    public Integer getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(Integer sessionStatus) { this.sessionStatus = sessionStatus; }
    public LocalDateTime getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(LocalDateTime lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
