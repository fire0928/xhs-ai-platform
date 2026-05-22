package com.hongshu.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String password;
    private String nickname;
    private String email;
    private LocalDateTime registerTime;
    private LocalDateTime lastLoginTime;
    private Integer status;        // 0-禁用, 1-正常
    private Long defaultAgentId;
    private String registerTerminal;
    private Integer memberLevel;   // 0-免费, 1-标准, 2-专业
    private LocalDateTime memberExpireTime;
    @TableLogic
    private Integer deleted;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; } public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public LocalDateTime getRegisterTime() { return registerTime; } public void setRegisterTime(LocalDateTime t) { this.registerTime = t; }
    public LocalDateTime getLastLoginTime() { return lastLoginTime; } public void setLastLoginTime(LocalDateTime t) { this.lastLoginTime = t; }
    public Integer getStatus() { return status; } public void setStatus(Integer status) { this.status = status; }
    public Long getDefaultAgentId() { return defaultAgentId; } public void setDefaultAgentId(Long id) { this.defaultAgentId = id; }
    public String getRegisterTerminal() { return registerTerminal; } public void setRegisterTerminal(String t) { this.registerTerminal = t; }
    public Integer getMemberLevel() { return memberLevel; } public void setMemberLevel(Integer l) { this.memberLevel = l; }
    public LocalDateTime getMemberExpireTime() { return memberExpireTime; } public void setMemberExpireTime(LocalDateTime t) { this.memberExpireTime = t; }
    public Integer getDeleted() { return deleted; } public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
