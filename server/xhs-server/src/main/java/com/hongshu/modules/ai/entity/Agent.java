package com.hongshu.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("agent")
public class Agent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String style;
    private String domain;
    private Long apiId;
    private String promptTemplate;
    private Integer sortOrder;
    private Integer status;        // 0-禁用, 1-启用
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getDescription() { return description; } public void setDescription(String d) { this.description = d; }
    public String getStyle() { return style; } public void setStyle(String s) { this.style = s; }
    public String getDomain() { return domain; } public void setDomain(String d) { this.domain = d; }
    public Long getApiId() { return apiId; } public void setApiId(Long id) { this.apiId = id; }
    public String getPromptTemplate() { return promptTemplate; } public void setPromptTemplate(String t) { this.promptTemplate = t; }
    public Integer getSortOrder() { return sortOrder; } public void setSortOrder(Integer o) { this.sortOrder = o; }
    public Integer getStatus() { return status; } public void setStatus(Integer s) { this.status = s; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime t) { this.createTime = t; }
    public Integer getDeleted() { return deleted; } public void setDeleted(Integer d) { this.deleted = d; }
}
