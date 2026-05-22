package com.hongshu.modules.user.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class ResetPasswordRequest {
    @NotBlank private String phone;
    @NotBlank private String code;
    @NotBlank @Size(min = 6, max = 16) private String newPassword;
}
