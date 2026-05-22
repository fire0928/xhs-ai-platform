package com.hongshu.modules.user.controller;

import com.hongshu.common.model.ApiResponse;
import com.hongshu.modules.user.service.UserService;
import com.hongshu.modules.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/send-code")
    public ApiResponse<?> sendCode(@RequestParam String phone) {
        userService.sendVerifyCode(phone);
        return ApiResponse.ok("验证码已发送");
    }

    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ApiResponse.ok(userService.register(req));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResponse.ok(userService.login(req));
    }

    @PostMapping("/refresh-token")
    public ApiResponse<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        return ApiResponse.ok(userService.refreshToken(refreshToken));
    }

    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        userService.resetPassword(req);
        return ApiResponse.ok("密码重置成功");
    }

    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(Authentication auth,
                                         @Valid @RequestBody ChangePasswordRequest req) {
        Long userId = (Long) auth.getPrincipal();
        userService.changePassword(userId, req);
        return ApiResponse.ok("密码修改成功");
    }

    @GetMapping("/info")
    public ApiResponse<?> getUserInfo(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok(userService.getUserInfo(userId));
    }

    @PutMapping("/info")
    public ApiResponse<?> updateUserInfo(Authentication auth,
                                         @RequestBody UpdateUserRequest req) {
        Long userId = (Long) auth.getPrincipal();
        userService.updateUserInfo(userId, req);
        return ApiResponse.ok("更新成功");
    }
}
