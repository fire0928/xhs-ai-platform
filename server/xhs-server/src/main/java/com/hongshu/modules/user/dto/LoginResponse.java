package com.hongshu.modules.user.dto;

public class LoginResponse {
    private String token;
    private String refreshToken;
    private UserDTO user;

    public String getToken() { return token; } public void setToken(String t) { this.token = t; }
    public String getRefreshToken() { return refreshToken; } public void setRefreshToken(String t) { this.refreshToken = t; }
    public UserDTO getUser() { return user; } public void setUser(UserDTO u) { this.user = u; }
}
