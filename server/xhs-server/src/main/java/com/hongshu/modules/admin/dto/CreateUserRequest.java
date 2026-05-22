package com.hongshu.modules.admin.dto;

public class CreateUserRequest {
    private String phone;
    private String password;
    private String nickname;
    private String email;
    private Integer memberLevel;
    private String registerTerminal;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getMemberLevel() { return memberLevel; }
    public void setMemberLevel(Integer memberLevel) { this.memberLevel = memberLevel; }
    public String getRegisterTerminal() { return registerTerminal; }
    public void setRegisterTerminal(String registerTerminal) { this.registerTerminal = registerTerminal; }
}
