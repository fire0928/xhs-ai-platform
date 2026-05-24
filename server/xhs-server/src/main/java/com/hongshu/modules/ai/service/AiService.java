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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${doubao.api-key:}")
    private String doubaoApiKey;

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
                .eq(Agent::getAgentType, 0)
                .orderByAsc(Agent::getSortOrder));
    }

    public List<Agent> getAvailableImageAgents() {
        return list(new LambdaQueryWrapper<Agent>()
                .eq(Agent::getStatus, 1)
                .eq(Agent::getAgentType, 1)
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

    public void deleteAgent(Long id) {
        if (getById(id) == null) throw new BusinessException(404, "Agent不存在");
        removeById(id);
    }

    public List<Agent> getAllAgents() {
        return list(new LambdaQueryWrapper<Agent>()
                .orderByAsc(Agent::getSortOrder));
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
        String apiKey = cryptoUtil.aesDecrypt(api.getApiKey());
        String response = callAiApi(api, apiKey, systemPrompt, userPrompt);
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
        String apiKey = cryptoUtil.aesDecrypt(api.getApiKey());
        String content = callAiApi(api, apiKey, "你是小红书爆款文案创作专家。", userPrompt);
        long duration = System.currentTimeMillis() - startTime;

        logCall(userId, api.getId(), agentId, title, content, true, (int) duration);
        return content;
    }

    /**
     * 生成图片提示词（使用文案AI）
     */
    public String generateImagePrompt(Long userId, Long agentId, String title, String content) {
        Agent agent = getById(agentId);
        AiModelApi api = agent.getApiId() != null ? apiMapper.selectById(agent.getApiId()) : getDefaultApi();
        if (api == null) throw new BusinessException(500, "未配置可用的AI模型");

        String systemPrompt = "你是一位专业的AI绘画提示词工程师。请根据以下文案内容生成适合AI图片生成的英文提示词。";
        String userPrompt = String.format(
                "请根据以下小红书文案内容生成一段英文AI绘画提示词（prompt）。\n\n" +
                "文案标题：%s\n文案摘要：%s\n\n" +
                "要求：1.只用英文；2.描述详细画面感强；3.小红书风格高质量图片；4.只输出提示词不要解释。",
                title, content != null ? content.substring(0, Math.min(content.length(), 500)) : "");

        long startTime = System.currentTimeMillis();
        String apiKey = cryptoUtil.aesDecrypt(api.getApiKey());
        String prompt = callAiApi(api, apiKey, systemPrompt, userPrompt);
        long duration = System.currentTimeMillis() - startTime;

        logCall(userId, api.getId(), agentId, title, prompt, true, (int) duration);
        return prompt;
    }

    /**
     * 生成图片
     */
    public List<String> generateImages(Long userId, Long imageAgentId, String prompt, int count) {
        Agent agent = getById(imageAgentId);
        if (agent == null || agent.getStatus() == 0 || agent.getAgentType() != 1) {
            throw new BusinessException(400, "图片Agent不可用");
        }
        AiModelApi api = agent.getApiId() != null ? apiMapper.selectById(agent.getApiId()) : null;
        if (api == null) throw new BusinessException(500, "未配置可用的AI模型");

        String provider = api.getProvider() != null ? api.getProvider() : "openai";
        String modelName = api.getModelName() != null ? api.getModelName() : "gpt-3.5-turbo";
        String apiKey = resolveApiKey(api);

        long startTime = System.currentTimeMillis();

        try {
            if ("doubao".equals(provider)) {
                // 豆包图片生成 API — 使用 /images/generations 端点
                return generateDoubaoImages(api, apiKey, modelName, prompt, count, userId, imageAgentId, startTime);
            } else {
                // 其他 provider：用 chat API 生成图片提示词文本
                String systemPrompt = StrUtil.isNotBlank(agent.getPromptTemplate())
                        ? agent.getPromptTemplate()
                        : "你是一个专业的AI绘画提示词优化专家。请根据用户需求生成高质量的图片生成提示词。";
                String userPrompt = String.format("请根据以下需求生成%d张图片的详细描述提示词，用于AI图像生成。主题：%s，风格：%s。",
                        count, prompt, agent.getStyle());
                String response = callAiApi(api, apiKey, systemPrompt, userPrompt);
                long duration = System.currentTimeMillis() - startTime;
                List<String> imagePrompts = parseImagePrompts(response, count);
                logCall(userId, api.getId(), imageAgentId, prompt, response, true, (int) duration);
                return imagePrompts;
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("图片生成失败", e);
            throw new BusinessException(500, "图片生成失败: " + e.getMessage());
        }
    }

    /**
     * 调用豆包图片生成 API（/images/generations）
     */
    private List<String> generateDoubaoImages(AiModelApi api, String apiKey, String modelName,
                                                String prompt, int count, Long userId,
                                                Long imageAgentId, long startTime) throws Exception {
        List<String> imageUrls = new ArrayList<>();
        String url = api.getApiUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("prompt", prompt);
        body.put("sequential_image_generation", "disabled");
        body.put("response_format", "url");
        body.put("size", "2K");
        body.put("stream", false);
        body.put("watermark", true);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
        String endpoint = url + "/images/generations";

        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);
        long duration = System.currentTimeMillis() - startTime;

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");
            if (data != null) {
                for (Map<String, Object> item : data) {
                    String imgUrl = (String) item.get("url");
                    if (StrUtil.isNotBlank(imgUrl)) {
                        imageUrls.add(imgUrl);
                    }
                }
            }
        } else {
            throw new BusinessException(500, "豆包图片API返回异常: " + response.getStatusCode());
        }

        logCall(userId, api.getId(), imageAgentId, prompt,
                imageUrls.isEmpty() ? "[]" : String.join(",", imageUrls), !imageUrls.isEmpty(), (int) duration);
        return imageUrls;
    }

    /**
     * 解析 API Key：优先用配置中的 key，否则从数据库解密
     */
    private String resolveApiKey(AiModelApi api) {
        // 豆包优先用配置文件中的 API Key
        if ("doubao".equals(api.getProvider()) && StrUtil.isNotBlank(doubaoApiKey)) {
            return doubaoApiKey;
        }
        // 其他情况从数据库解密
        return cryptoUtil.aesDecrypt(api.getApiKey());
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

    private String callAiApi(AiModelApi api, String apiKey, String systemPrompt, String userPrompt) {
        try {
            String provider = api.getProvider() != null ? api.getProvider() : "openai";
            String url = api.getApiUrl();
            String modelName = api.getModelName() != null ? api.getModelName() : "gpt-3.5-turbo";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity;
            String endpoint;

            switch (provider) {
                case "doubao": // 字节跳动豆包
                    endpoint = url + "/chat/completions";
                    headers.set("Authorization", "Bearer " + apiKey);
                    Map<String, Object> doubaoBody = new HashMap<>();
                    doubaoBody.put("model", modelName);
                    List<Map<String, String>> doubaoMessages = new ArrayList<>();
                    doubaoMessages.add(Map.of("role", "system", "content", systemPrompt));
                    doubaoMessages.add(Map.of("role", "user", "content", userPrompt));
                    doubaoBody.put("messages", doubaoMessages);
                    doubaoBody.put("temperature", 0.7);
                    entity = new HttpEntity<>(objectMapper.writeValueAsString(doubaoBody), headers);
                    break;

                case "baidu": // 百度文心一言
                    endpoint = url + "/chat/completions";
                    headers.set("Authorization", "Bearer " + apiKey);
                    Map<String, Object> baiduBody = new HashMap<>();
                    baiduBody.put("model", modelName);
                    List<Map<String, String>> baiduMessages = new ArrayList<>();
                    baiduMessages.add(Map.of("role", "system", "content", systemPrompt));
                    baiduMessages.add(Map.of("role", "user", "content", userPrompt));
                    baiduBody.put("messages", baiduMessages);
                    baiduBody.put("temperature", 0.7);
                    entity = new HttpEntity<>(objectMapper.writeValueAsString(baiduBody), headers);
                    break;

                case "aliyun": // 阿里通义千问
                    endpoint = url + "/chat/completions";
                    headers.set("Authorization", "Bearer " + apiKey);
                    Map<String, Object> aliBody = new HashMap<>();
                    aliBody.put("model", modelName);
                    List<Map<String, String>> aliMessages = new ArrayList<>();
                    aliMessages.add(Map.of("role", "system", "content", systemPrompt));
                    aliMessages.add(Map.of("role", "user", "content", userPrompt));
                    aliBody.put("messages", aliMessages);
                    aliBody.put("temperature", 0.7);
                    entity = new HttpEntity<>(objectMapper.writeValueAsString(aliBody), headers);
                    break;

                case "deepseek": // DeepSeek
                    endpoint = url + "/chat/completions";
                    headers.set("Authorization", "Bearer " + apiKey);
                    Map<String, Object> dsBody = new HashMap<>();
                    dsBody.put("model", modelName);
                    List<Map<String, String>> dsMessages = new ArrayList<>();
                    dsMessages.add(Map.of("role", "system", "content", systemPrompt));
                    dsMessages.add(Map.of("role", "user", "content", userPrompt));
                    dsBody.put("messages", dsMessages);
                    dsBody.put("temperature", 0.7);
                    entity = new HttpEntity<>(objectMapper.writeValueAsString(dsBody), headers);
                    break;

                default: // OpenAI 标准格式
                    endpoint = url + "/chat/completions";
                    headers.set("Authorization", "Bearer " + apiKey);
                    Map<String, Object> openaiBody = new HashMap<>();
                    openaiBody.put("model", modelName);
                    List<Map<String, String>> openaiMessages = new ArrayList<>();
                    openaiMessages.add(Map.of("role", "system", "content", systemPrompt));
                    openaiMessages.add(Map.of("role", "user", "content", userPrompt));
                    openaiBody.put("messages", openaiMessages);
                    openaiBody.put("temperature", 0.7);
                    entity = new HttpEntity<>(objectMapper.writeValueAsString(openaiBody), headers);
                    break;
            }

            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);

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

    private List<String> parseImagePrompts(String response, int count) {
        List<String> prompts = new ArrayList<>();
        if (StrUtil.isBlank(response)) return prompts;

        for (String line : response.split("\\n")) {
            String cleaned = line.replaceAll("^\\d+[\\.\\)、]\\s*", "").trim();
            if (cleaned.length() > 5) {
                prompts.add(cleaned);
            }
        }

        // 如果解析不到足够数量，用原始内容补充
        while (prompts.size() < count) {
            prompts.add(response.trim());
        }

        return prompts.size() > count ? prompts.subList(0, count) : prompts;
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

        // 更新 API 模型调用统计
        if (apiId != null) {
            AiModelApi api = apiMapper.selectById(apiId);
            if (api != null) {
                // 估算 token 用量（简单按字符数/2估算）
                long reqTokens = (request != null ? request.length() : 0) / 2;
                long respTokens = (response != null ? response.length() : 0) / 2;
                api.setUsedTokens((api.getUsedTokens() != null ? api.getUsedTokens() : 0) + reqTokens + respTokens);
                api.setTodayCall((api.getTodayCall() != null ? api.getTodayCall() : 0) + 1);
                apiMapper.updateById(api);
            }
        }
    }
}
