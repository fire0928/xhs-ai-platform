package com.hongshu.modules.ai.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.exception.BusinessException;
import com.hongshu.common.util.CryptoUtil;
import com.hongshu.modules.ai.entity.Agent;
import com.hongshu.modules.ai.entity.AiModelApi;
import com.hongshu.modules.ai.entity.ApiCallLog;
import com.hongshu.modules.ai.mapper.AgentMapper;
import com.hongshu.modules.ai.mapper.AiModelApiMapper;
import com.hongshu.modules.ai.mapper.ApiCallLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AiService extends ServiceImpl<AgentMapper, Agent> {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private final AiModelApiMapper apiMapper;
    private final ApiCallLogMapper callLogMapper;
    private final CryptoUtil cryptoUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiService(AiModelApiMapper apiMapper, ApiCallLogMapper callLogMapper,
                     CryptoUtil cryptoUtil, RedisTemplate<String, Object> redisTemplate) {
        this.apiMapper = apiMapper;
        this.callLogMapper = callLogMapper;
        this.cryptoUtil = cryptoUtil;
        this.redisTemplate = redisTemplate;
    }

    // ===== Agent管理 =====

    public List<Agent> getAvailableAgents() {
        return list(new LambdaQueryWrapper<Agent>()
                .eq(Agent::getStatus, 1)
                .orderByAsc(Agent::getSortOrder));
    }

    public Agent createAgent(Agent agent) {
        save(agent);
        return agent;
    }

    public Agent updateAgent(Long id, Agent agent) {
        agent.setId(id);
        updateById(agent);
        return getById(id);
    }

    // ===== API模型管理 =====

    public List<AiModelApi> getApis() {
        return apiMapper.selectList(new LambdaQueryWrapper<AiModelApi>()
                .orderByDesc(AiModelApi::getCreateTime));
    }

    public AiModelApi createApi(AiModelApi api) {
        if (StrUtil.isNotBlank(api.getApiKey())) {
            api.setApiKey(cryptoUtil.aesEncrypt(api.getApiKey()));
        }
        apiMapper.insert(api);
        return api;
    }

    public AiModelApi updateApi(Long id, AiModelApi api) {
        api.setId(id);
        if (StrUtil.isNotBlank(api.getApiKey())) {
            api.setApiKey(cryptoUtil.aesEncrypt(api.getApiKey()));
        } else {
            AiModelApi existing = apiMapper.selectById(id);
            if (existing != null) api.setApiKey(existing.getApiKey());
        }
        apiMapper.updateById(api);
        return apiMapper.selectById(id);
    }

    public void deleteApi(Long id) {
        apiMapper.deleteById(id);
    }

    // ===== AI标题生成 =====

    @SuppressWarnings("unchecked")
    public List<String> generateTitles(Long userId, Long agentId, String prompt, int count) {
        Agent agent = getById(agentId);
        if (agent == null || agent.getStatus() == 0) {
            throw new BusinessException(400, "Agent不可用");
        }

        AiModelApi api = apiMapper.selectById(agent.getApiId());
        if (api == null) {
            api = getDefaultApi();
        }
        if (api == null) {
            throw new BusinessException(500, "未配置可用的AI模型");
        }

        String systemPrompt = StrUtil.isNotBlank(agent.getPromptTemplate())
                ? agent.getPromptTemplate()
                : "你是一个小红书创作专家，请根据用户需求生成吸引人的标题。";

        String userPrompt = String.format("请生成 %d 个小红书风格标题，主题：%s，风格：%s。要求：吸引眼球、适合小红书平台。只返回编号列表，每个标题一行。",
                count, prompt, agent.getStyle());

        long startTime = System.currentTimeMillis();
        String response = callAiApi(api, systemPrompt, userPrompt);
        long duration = System.currentTimeMillis() - startTime;

        // 解析响应
        List<String> titles = parseTitlesFromResponse(response);

        // 记录调用日志
        logCall(userId, api.getId(), agentId, prompt, response, true, (int) duration);

        return titles;
    }

    /**
     * 生成文案内容
     */
    public String generateContent(Long userId, Long agentId, String title, String prompt) {
        Agent agent = getById(agentId);
        AiModelApi api = agent.getApiId() != null ? apiMapper.selectById(agent.getApiId()) : getDefaultApi();
        if (api == null) throw new BusinessException(500, "未配置可用的AI模型");

        String userPrompt = String.format(
                "请为以下小红书标题创作一篇正文文案：\n" +
                "标题：%s\n创作需求：%s\n风格：%s\n" +
                "要求：使用emoji表情，添加2-3个话题标签（#），分段清晰，字数300-500字。",
                title, prompt, agent.getStyle());

        long startTime = System.currentTimeMillis();
        String content = callAiApi(api, "你是小红书爆款文案创作专家。", userPrompt);
        long duration = System.currentTimeMillis() - startTime;

        logCall(userId, api.getId(), agentId, title, content, true, (int) duration);
        return content;
    }

    // ===== API调用记录 =====

    public Page<ApiCallLog> getCallLogs(int page, int pageSize, Long apiId, String terminal) {
        LambdaQueryWrapper<ApiCallLog> wrapper = new LambdaQueryWrapper<>();
        if (apiId != null) wrapper.eq(ApiCallLog::getApiId, apiId);
        if (StrUtil.isNotBlank(terminal)) wrapper.eq(ApiCallLog::getTerminal, terminal);
        wrapper.orderByDesc(ApiCallLog::getCreateTime);
        return (Page<ApiCallLog>) callLogMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    // ---- 内部辅助 ----

    private AiModelApi getDefaultApi() {
        return apiMapper.selectList(new LambdaQueryWrapper<AiModelApi>()
                .eq(AiModelApi::getStatus, 1)
                .last("LIMIT 1"))
                .stream().findFirst().orElse(null);
    }

    private String callAiApi(AiModelApi api, String systemPrompt, String userPrompt) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", api.getModelName() != null ? api.getModelName() : "gpt-3.5-turbo");
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userPrompt));
            body.put("messages", messages);
            body.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + cryptoUtil.aesDecrypt(api.getApiKey()));
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    api.getApiUrl() + "/chat/completions", entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> msg = (Map<String, String>) choice.get("message");
                    return msg != null ? msg.get("content") : "";
                }
            }

            throw new BusinessException(500, "AI接口返回异常: " + response.getStatusCode());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI API调用失败", e);
            throw new BusinessException(500, "AI服务调用失败: " + e.getMessage());
        }
    }

    private List<String> parseTitlesFromResponse(String response) {
        List<String> titles = new ArrayList<>();
        if (StrUtil.isBlank(response)) return titles;

        for (String line : response.split("\\n")) {
            String cleaned = line.replaceAll("^\\d+[\\.\\)、]\\s*", "").trim();
            if (cleaned.length() > 3 && cleaned.length() < 100) {
                titles.add(cleaned);
            }
        }

        if (titles.isEmpty()) {
            titles.add(response.trim().substring(0, Math.min(response.length(), 100)));
        }

        return titles.size() > 10 ? titles.subList(0, 10) : titles;
    }

    private void logCall(Long userId, Long apiId, Long agentId, String request, String response,
                         boolean success, int duration) {
        ApiCallLog log = new ApiCallLog();
        log.setUserId(userId);
        log.setApiId(apiId);
        log.setAgentId(agentId);
        log.setRequestContent(request.length() > 500 ? request.substring(0, 500) : request);
        log.setResponseContent(response.length() > 500 ? response.substring(0, 500) : response);
        log.setStatus(success ? 1 : 0);
        log.setDurationMs(duration);
        log.setCreateTime(LocalDateTime.now());
        callLogMapper.insert(log);
    }
}
