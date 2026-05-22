package com.hongshu.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
    @NotBlank private String phone;
    @NotBlank private String code;
    @NotBlank @Size(min = 6, max = 16) private String newPassword;

    public String getPhone() { return phone; } public void setPhone(String p) { this.phone = p; }
    public String getCode() { return code; } public void setCode(String c) { this.code = c; }
    public String getNewPassword() { return newPassword; } public void setNewPassword(String p) { this.newPassword = p; }
}
