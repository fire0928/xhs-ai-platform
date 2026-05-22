package com.hongshu.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("ai_model_api")
public class AiModelApi {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String apiUrl;
    private String apiKey;
    private String modelName;
    private String requestParams;
    private Integer rateLimit;
    private Integer status;        // 0-禁用, 1-启用
    private Long usedTokens;
    private Long totalTokens;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getApiUrl() { return apiUrl; } public void setApiUrl(String u) { this.apiUrl = u; }
    public String getApiKey() { return apiKey; } public void setApiKey(String k) { this.apiKey = k; }
    public String getModelName() { return modelName; } public void setModelName(String n) { this.modelName = n; }
    public String getRequestParams() { return requestParams; } public void setRequestParams(String p) { this.requestParams = p; }
    public Integer getRateLimit() { return rateLimit; } public void setRateLimit(Integer r) { this.rateLimit = r; }
    public Integer getStatus() { return status; } public void setStatus(Integer s) { this.status = s; }
    public Long getUsedTokens() { return usedTokens; } public void setUsedTokens(Long t) { this.usedTokens = t; }
    public Long getTotalTokens() { return totalTokens; } public void setTotalTokens(Long t) { this.totalTokens = t; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime t) { this.createTime = t; }
    public Integer getDeleted() { return deleted; } public void setDeleted(Integer d) { this.deleted = d; }
}
