package com.hongshu.modules.admin.dto;

public class UpdateUserRequest {
    private String nickname;
    private String email;
    private Integer memberLevel;
    private String registerTerminal;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getMemberLevel() { return memberLevel; }
    public void setMemberLevel(Integer memberLevel) { this.memberLevel = memberLevel; }
    public String getRegisterTerminal() { return registerTerminal; }
    public void setRegisterTerminal(String registerTerminal) { this.registerTerminal = registerTerminal; }
}
