package com.hongshu.modules.user.dto;

public class UpdateUserRequest {
    private String nickname;
    private String email;
    private Long defaultAgentId;

    public String getNickname() { return nickname; } public void setNickname(String n) { this.nickname = n; }
    public String getEmail() { return email; } public void setEmail(String e) { this.email = e; }
    public Long getDefaultAgentId() { return defaultAgentId; } public void setDefaultAgentId(Long id) { this.defaultAgentId = id; }
}
