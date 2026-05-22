package com.hongshu.modules.user.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank private String phone;
    @NotBlank private String password;

    public String getPhone() { return phone; } public void setPhone(String p) { this.phone = p; }
    public String getPassword() { return password; } public void setPassword(String p) { this.password = p; }
}
