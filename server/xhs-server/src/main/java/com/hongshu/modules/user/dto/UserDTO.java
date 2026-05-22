package com.hongshu.modules.user.dto;

import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String phone;
    private String nickname;
    private String email;
    private LocalDateTime registerTime;
    private LocalDateTime lastLoginTime;
    private Integer status;
    private Integer memberLevel;
    private LocalDateTime memberExpireTime;
    private String registerTerminal;
    private Long defaultAgentId;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getPhone() { return phone; } public void setPhone(String p) { this.phone = p; }
    public String getNickname() { return nickname; } public void setNickname(String n) { this.nickname = n; }
    public String getEmail() { return email; } public void setEmail(String e) { this.email = e; }
    public LocalDateTime getRegisterTime() { return registerTime; } public void setRegisterTime(LocalDateTime t) { this.registerTime = t; }
    public LocalDateTime getLastLoginTime() { return lastLoginTime; } public void setLastLoginTime(LocalDateTime t) { this.lastLoginTime = t; }
    public Integer getStatus() { return status; } public void setStatus(Integer s) { this.status = s; }
    public Integer getMemberLevel() { return memberLevel; } public void setMemberLevel(Integer l) { this.memberLevel = l; }
    public LocalDateTime getMemberExpireTime() { return memberExpireTime; } public void setMemberExpireTime(LocalDateTime t) { this.memberExpireTime = t; }
    public String getRegisterTerminal() { return registerTerminal; } public void setRegisterTerminal(String t) { this.registerTerminal = t; }
    public Long getDefaultAgentId() { return defaultAgentId; } public void setDefaultAgentId(Long id) { this.defaultAgentId = id; }
}
