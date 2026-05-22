package com.hongshu.modules.content.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("ai_content")
public class AiContent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long agentId;
    private Long apiId;
    private String title;
    private String content;
    private String imageUrls;
    private String tags;
    private LocalDateTime createTime;
    private Integer auditStatus;  // 0-草稿, 1-审核中, 2-审核通过, 3-审核失败, 4-已删除
    private String createTerminal;
    @TableLogic
    private Integer deleted;

    // === Getters and Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    public Long getApiId() { return apiId; }
    public void setApiId(Long apiId) { this.apiId = apiId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public Integer getAuditStatus() { return auditStatus; }
    public void setAuditStatus(Integer auditStatus) { this.auditStatus = auditStatus; }
    public String getCreateTerminal() { return createTerminal; }
    public void setCreateTerminal(String createTerminal) { this.createTerminal = createTerminal; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
