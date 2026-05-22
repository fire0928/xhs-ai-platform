package com.hongshu.modules.publish.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("publish_log")
public class PublishLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long queueId;
    private String step;
    private Integer status;       // 0-进行中, 1-成功, 2-失败
    private String errorMsg;
    private String screenshotUrl;
    private Integer durationMs;
    private LocalDateTime createTime;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getQueueId() { return queueId; } public void setQueueId(Long qid) { this.queueId = qid; }
    public String getStep() { return step; } public void setStep(String s) { this.step = s; }
    public Integer getStatus() { return status; } public void setStatus(Integer s) { this.status = s; }
    public String getErrorMsg() { return errorMsg; } public void setErrorMsg(String e) { this.errorMsg = e; }
    public String getScreenshotUrl() { return screenshotUrl; } public void setScreenshotUrl(String u) { this.screenshotUrl = u; }
    public Integer getDurationMs() { return durationMs; } public void setDurationMs(Integer ms) { this.durationMs = ms; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime t) { this.createTime = t; }
}
