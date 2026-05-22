package com.hongshu.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("api_call_log")
public class ApiCallLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long apiId;
    private Long agentId;
    private String terminal;
    private String requestContent;
    private String responseContent;
    private Integer status;
    private Integer durationMs;
    private Integer tokensUsed;
    private LocalDateTime createTime;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long uid) { this.userId = uid; }
    public Long getApiId() { return apiId; } public void setApiId(Long id) { this.apiId = id; }
    public Long getAgentId() { return agentId; } public void setAgentId(Long id) { this.agentId = id; }
    public String getTerminal() { return terminal; } public void setTerminal(String t) { this.terminal = t; }
    public String getRequestContent() { return requestContent; } public void setRequestContent(String c) { this.requestContent = c; }
    public String getResponseContent() { return responseContent; } public void setResponseContent(String c) { this.responseContent = c; }
    public Integer getStatus() { return status; } public void setStatus(Integer s) { this.status = s; }
    public Integer getDurationMs() { return durationMs; } public void setDurationMs(Integer ms) { this.durationMs = ms; }
    public Integer getTokensUsed() { return tokensUsed; } public void setTokensUsed(Integer t) { this.tokensUsed = t; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime t) { this.createTime = t; }
}
