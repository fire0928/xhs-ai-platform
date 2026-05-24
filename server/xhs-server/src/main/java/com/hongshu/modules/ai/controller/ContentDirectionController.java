package com.hongshu.modules.ai.controller;

import com.hongshu.common.model.ApiResponse;
import com.hongshu.modules.ai.entity.ContentDirection;
import com.hongshu.modules.ai.service.ContentDirectionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/ai")
@PreAuthorize("hasRole('ADMIN')")
public class ContentDirectionController {

    private final ContentDirectionService directionService;

    public ContentDirectionController(ContentDirectionService directionService) {
        this.directionService = directionService;
    }

    @GetMapping("/directions")
    public ApiResponse<List<ContentDirection>> getAllDirections() {
        return ApiResponse.ok(directionService.getAllDirections());
    }

    @PostMapping("/directions")
    public ApiResponse<ContentDirection> createDirection(@RequestBody ContentDirection direction) {
        return ApiResponse.ok(directionService.createDirection(direction));
    }

    @PutMapping("/directions/{id}")
    public ApiResponse<ContentDirection> updateDirection(@PathVariable Long id, @RequestBody ContentDirection direction) {
        return ApiResponse.ok(directionService.updateDirection(id, direction));
    }

    @DeleteMapping("/directions/{id}")
    public ApiResponse<?> deleteDirection(@PathVariable Long id) {
        directionService.deleteDirection(id);
        return ApiResponse.ok();
    }
}
