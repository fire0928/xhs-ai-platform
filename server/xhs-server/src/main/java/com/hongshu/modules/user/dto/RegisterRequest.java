package com.hongshu.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    @NotBlank(message = "验证码不能为空")
    private String code;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 16, message = "密码长度6-16位")
    private String password;
    private String terminal;

    public String getPhone() { return phone; } public void setPhone(String p) { this.phone = p; }
    public String getCode() { return code; } public void setCode(String c) { this.code = c; }
    public String getPassword() { return password; } public void setPassword(String p) { this.password = p; }
    public String getTerminal() { return terminal; } public void setTerminal(String t) { this.terminal = t; }
}
