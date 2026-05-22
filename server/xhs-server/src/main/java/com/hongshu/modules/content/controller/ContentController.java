package com.hongshu.modules.content.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hongshu.common.model.ApiResponse;
import com.hongshu.common.model.PageResult;
import com.hongshu.modules.content.entity.AiContent;
import com.hongshu.modules.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    // ===== CRUD =====
    @PostMapping
    public ApiResponse<AiContent> createContent(Authentication auth, @RequestBody AiContent content) {
        content.setUserId((Long) auth.getPrincipal());
        return ApiResponse.ok(contentService.createContent(content));
    }

    @GetMapping("/{id}")
    public ApiResponse<AiContent> getContent(@PathVariable Long id) {
        return ApiResponse.ok(contentService.getContentDetail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<AiContent> updateContent(@PathVariable Long id, @RequestBody AiContent update) {
        return ApiResponse.ok(contentService.updateContent(id, update));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return ApiResponse.ok();
    }

    // ===== 审核 =====
    @PostMapping("/{id}/submit-audit")
    public ApiResponse<?> submitAudit(@PathVariable Long id) {
        contentService.submitForAudit(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<?> approve(@PathVariable Long id) {
        contentService.approveContent(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<?> reject(@PathVariable Long id, @RequestParam(required = false) String reason) {
        contentService.rejectContent(id, reason);
        return ApiResponse.ok();
    }

    @PostMapping("/batch/approve")
    public ApiResponse<?> batchApprove(@RequestBody List<Long> ids) {
        contentService.batchApprove(ids);
        return ApiResponse.ok();
    }

    @PostMapping("/batch/delete")
    public ApiResponse<?> batchDelete(@RequestBody List<Long> ids) {
        contentService.batchDelete(ids);
        return ApiResponse.ok();
    }

    // ===== 查询 =====
    @GetMapping("/my")
    public ApiResponse<PageResult<AiContent>> getMyContents(
            Authentication auth,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) String keyword) {
        Long userId = (Long) auth.getPrincipal();
        IPage<AiContent> result = contentService.pageUserContents(userId, page, pageSize, auditStatus, keyword);
        return ApiResponse.ok(PageResult.of(result.getTotal(), page, pageSize, result.getRecords()));
    }

    // ===== 图片上传 =====
    @PostMapping("/upload-image")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = contentService.uploadImage(file);
        return ApiResponse.ok(Map.of("url", url));
    }
}
