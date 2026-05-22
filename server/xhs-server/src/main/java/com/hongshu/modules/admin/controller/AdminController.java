package com.hongshu.modules.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hongshu.common.model.ApiResponse;
import com.hongshu.common.model.PageResult;
import com.hongshu.modules.admin.service.AdminService;
import com.hongshu.modules.admin.entity.OperationLog;
import com.hongshu.modules.user.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ===== 用户管理 =====
    @GetMapping("/users")
    public ApiResponse<PageResult<?>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String terminal) {
        IPage<?> result = adminService.pageUsers(page, pageSize, keyword, status, terminal);
        return ApiResponse.ok(PageResult.of(result.getTotal(), page, pageSize, result.getRecords()));
    }

    @PutMapping("/users/{userId}/toggle-status")
    public ApiResponse<?> toggleUserStatus(@PathVariable Long userId,
                                           @RequestParam Integer status) {
        adminService.toggleUserStatus(userId, status);
        return ApiResponse.ok();
    }

    @PutMapping("/users/{userId}/reset-password")
    public ApiResponse<?> resetUserPassword(@PathVariable Long userId,
                                            @RequestParam String newPassword) {
        adminService.resetUserPassword(userId, newPassword);
        return ApiResponse.ok();
    }

    // ===== 系统概览 =====
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverview() {
        return ApiResponse.ok(adminService.getSystemOverview());
    }

    // ===== 操作日志 =====
    @GetMapping("/logs")
    public ApiResponse<List<OperationLog>> getRecentLogs(
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.ok(adminService.getRecentLogs(limit));
    }

    // ===== 全量内容管理 =====
    @GetMapping("/contents")
    public ApiResponse<PageResult<?>> getAllContents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String terminal) {
        IPage<?> result = adminService.pageAllContents(page, pageSize, auditStatus, keyword, terminal);
        return ApiResponse.ok(PageResult.of(result.getTotal(), page, pageSize, result.getRecords()));
    }

    @DeleteMapping("/contents/{contentId}")
    public ApiResponse<?> deleteContent(@PathVariable Long contentId) {
        adminService.deleteContent(contentId);
        return ApiResponse.ok();
    }

    // ===== AI调用监控 =====
    @GetMapping("/ai-call-logs")
    public ApiResponse<PageResult<?>> getAiCallLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long apiId,
            @RequestParam(required = false) String terminal) {
        IPage<?> result = adminService.pageCallLogs(page, pageSize, apiId, terminal);
        return ApiResponse.ok(PageResult.of(result.getTotal(), page, pageSize, result.getRecords()));
    }

    // ===== 数据分析 =====
    @GetMapping("/analytics/summary")
    public ApiResponse<Map<String, Object>> getAnalyticsSummary(
            @RequestParam(required = false) Integer days) {
        return ApiResponse.ok(adminService.getAnalyticsSummary(days != null ? days : 7));
    }

    @GetMapping("/analytics/user/{userId}")
    public ApiResponse<Map<String, Object>> getUserAnalytics(@PathVariable Long userId) {
        return ApiResponse.ok(adminService.getUserAnalytics(userId));
    }
}
