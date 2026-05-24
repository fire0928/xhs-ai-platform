package com.hongshu.modules.ai.controller;

import com.hongshu.common.model.ApiResponse;
import com.hongshu.modules.ai.entity.Agent;
import com.hongshu.modules.ai.entity.ContentDirection;
import com.hongshu.modules.ai.service.AiService;
import com.hongshu.modules.ai.service.ContentDirectionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户端 AI 创作接口（无需 ADMIN 角色）
 */
@RestController
@RequestMapping("/ai")
public class UserAiController {

    private final AiService aiService;
    private final ContentDirectionService directionService;

    public UserAiController(AiService aiService, ContentDirectionService directionService) {
        this.aiService = aiService;
        this.directionService = directionService;
    }

    @GetMapping("/agents")
    public ApiResponse<List<Agent>> getAvailableAgents() {
        return ApiResponse.ok(aiService.getAvailableAgents());
    }

    @GetMapping("/image-agents")
    public ApiResponse<List<Agent>> getAvailableImageAgents() {
        return ApiResponse.ok(aiService.getAvailableImageAgents());
    }

    @GetMapping("/directions")
    public ApiResponse<List<ContentDirection>> getActiveDirections() {
        return ApiResponse.ok(directionService.getActiveDirections());
    }

    @PostMapping("/generate-titles")
    public ApiResponse<List<String>> generateTitles(Authentication auth,
                                                     @RequestBody Map<String, Object> body) {
        Long userId = (Long) auth.getPrincipal();
        Long agentId = Long.valueOf(body.get("agentId").toString());
        String prompt = body.get("prompt").toString();
        int count = body.containsKey("count") ? Integer.parseInt(body.get("count").toString()) : 3;
        return ApiResponse.ok(aiService.generateTitles(userId, agentId, prompt, count));
    }

    @PostMapping("/generate-content")
    public ApiResponse<String> generateContent(Authentication auth,
                                               @RequestBody Map<String, String> body) {
        Long userId = (Long) auth.getPrincipal();
        Long agentId = Long.valueOf(body.get("agentId"));
        String title = body.get("title");
        String prompt = body.getOrDefault("prompt", "");
        return ApiResponse.ok(aiService.generateContent(userId, agentId, title, prompt));
    }

    @PostMapping("/generate-image-prompt")
    public ApiResponse<String> generateImagePrompt(Authentication auth,
                                                   @RequestBody Map<String, String> body) {
        Long userId = (Long) auth.getPrincipal();
        Long agentId = Long.valueOf(body.get("agentId"));
        String title = body.get("title");
        String content = body.getOrDefault("content", "");
        return ApiResponse.ok(aiService.generateImagePrompt(userId, agentId, title, content));
    }

    @PostMapping("/generate-images")
    public ApiResponse<List<String>> generateImages(Authentication auth,
                                                     @RequestBody Map<String, Object> body) {
        Long userId = (Long) auth.getPrincipal();
        Long imageAgentId = Long.valueOf(body.get("imageAgentId").toString());
        String prompt = body.get("prompt").toString();
        int count = body.containsKey("count") ? Integer.parseInt(body.get("count").toString()) : 4;
        return ApiResponse.ok(aiService.generateImages(userId, imageAgentId, prompt, count));
    }
}
