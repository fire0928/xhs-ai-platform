package com.hongshu.modules.user.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
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
}

@Data
class LoginRequest {
    @NotBlank private String phone;
    @NotBlank private String password;
}

@Data
class ResetPasswordRequest {
    @NotBlank private String phone;
    @NotBlank private String code;
    @NotBlank @Size(min = 6, max = 16) private String newPassword;
}

@Data
class ChangePasswordRequest {
    @NotBlank private String oldPassword;
    @NotBlank @Size(min = 6, max = 16) private String newPassword;
}

@Data
class UpdateUserRequest {
    private String nickname;
    private String email;
    private Long defaultAgentId;
}
