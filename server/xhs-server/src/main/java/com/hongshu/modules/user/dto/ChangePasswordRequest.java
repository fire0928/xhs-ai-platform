package com.hongshu.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {
    @NotBlank private String oldPassword;
    @NotBlank @Size(min = 6, max = 16) private String newPassword;

    public String getOldPassword() { return oldPassword; } public void setOldPassword(String p) { this.oldPassword = p; }
    public String getNewPassword() { return newPassword; } public void setNewPassword(String p) { this.newPassword = p; }
}
