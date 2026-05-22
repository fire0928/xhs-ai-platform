package com.hongshu.modules.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@TableName("member_plan")
public class MemberPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Integer level;
    private Integer price;
    private String period;
    private Integer dailyAiCreate;
    private Integer maxAccounts;
    private Integer maxAgents;
    private String analysisLevel;
    private Integer batchEnabled;
    private Integer scheduleEnabled;
    private Integer exportEnabled;
    private Integer customAgentEnabled;
    private Integer sortOrder;
    private Integer status;
    private String color;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getDescription() { return description; } public void setDescription(String d) { this.description = d; }
    public Integer getLevel() { return level; } public void setLevel(Integer l) { this.level = l; }
    public Integer getPrice() { return price; } public void setPrice(Integer p) { this.price = p; }
    public String getPeriod() { return period; } public void setPeriod(String p) { this.period = p; }
    public Integer getDailyAiCreate() { return dailyAiCreate; } public void setDailyAiCreate(Integer d) { this.dailyAiCreate = d; }
    public Integer getMaxAccounts() { return maxAccounts; } public void setMaxAccounts(Integer m) { this.maxAccounts = m; }
    public Integer getMaxAgents() { return maxAgents; } public void setMaxAgents(Integer m) { this.maxAgents = m; }
    public String getAnalysisLevel() { return analysisLevel; } public void setAnalysisLevel(String a) { this.analysisLevel = a; }
    public Integer getBatchEnabled() { return batchEnabled; } public void setBatchEnabled(Integer b) { this.batchEnabled = b; }
    public Integer getScheduleEnabled() { return scheduleEnabled; } public void setScheduleEnabled(Integer s) { this.scheduleEnabled = s; }
    public Integer getExportEnabled() { return exportEnabled; } public void setExportEnabled(Integer e) { this.exportEnabled = e; }
    public Integer getCustomAgentEnabled() { return customAgentEnabled; } public void setCustomAgentEnabled(Integer c) { this.customAgentEnabled = c; }
    public Integer getSortOrder() { return sortOrder; } public void setSortOrder(Integer s) { this.sortOrder = s; }
    public Integer getStatus() { return status; } public void setStatus(Integer s) { this.status = s; }
    public String getColor() { return color; } public void setColor(String c) { this.color = c; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime t) { this.createTime = t; }
    public LocalDateTime getUpdateTime() { return updateTime; } public void setUpdateTime(LocalDateTime t) { this.updateTime = t; }
    public Integer getDeleted() { return deleted; } public void setDeleted(Integer d) { this.deleted = d; }
}
