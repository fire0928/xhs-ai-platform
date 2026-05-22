package com.hongshu.modules.ai.controller;

import com.hongshu.common.model.ApiResponse;
import com.hongshu.modules.ai.entity.Agent;
import com.hongshu.modules.ai.entity.AiModelApi;
import com.hongshu.modules.ai.service.AiService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // ===== Agent =====
    @GetMapping("/agents")
    public ApiResponse<List<Agent>> getAgents() {
        return ApiResponse.ok(aiService.getAvailableAgents());
    }

    @PostMapping("/agents")
    public ApiResponse<Agent> createAgent(@RequestBody Agent agent) {
        return ApiResponse.ok(aiService.createAgent(agent));
    }

    @PutMapping("/agents/{id}")
    public ApiResponse<Agent> updateAgent(@PathVariable Long id, @RequestBody Agent agent) {
        return ApiResponse.ok(aiService.updateAgent(id, agent));
    }

    // ===== API Models =====
    @GetMapping("/apis")
    public ApiResponse<List<AiModelApi>> getApis() {
        return ApiResponse.ok(aiService.getApis());
    }

    @PostMapping("/apis")
    public ApiResponse<AiModelApi> createApi(@RequestBody AiModelApi api) {
        return ApiResponse.ok(aiService.createApi(api));
    }

    @PutMapping("/apis/{id}")
    public ApiResponse<AiModelApi> updateApi(@PathVariable Long id, @RequestBody AiModelApi api) {
        return ApiResponse.ok(aiService.updateApi(id, api));
    }

    @DeleteMapping("/apis/{id}")
    public ApiResponse<?> deleteApi(@PathVariable Long id) {
        aiService.deleteApi(id);
        return ApiResponse.ok();
    }

    // ===== AI创作 =====
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
}
