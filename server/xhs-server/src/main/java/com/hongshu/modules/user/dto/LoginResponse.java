package com.hongshu.modules.user.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String refreshToken;
    private UserDTO user;
}
