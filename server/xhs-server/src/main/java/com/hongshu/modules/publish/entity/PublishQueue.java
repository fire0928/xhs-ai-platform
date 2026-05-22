package com.hongshu.modules.publish.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("publish_queue")
public class PublishQueue {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long contentId;
    private Long xhAccountId;
    private LocalDateTime publishTime;
    private Integer publishStatus; // 0-待发布, 1-发布中, 2-已发布, 3-发布失败
    private String publishResult;
    private Integer sortOrder;
    private Integer retryCount;
    private LocalDateTime createTime;
    private LocalDateTime actualPublishTime;
    @TableLogic
    private Integer deleted;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long uid) { this.userId = uid; }
    public Long getContentId() { return contentId; } public void setContentId(Long cid) { this.contentId = cid; }
    public Long getXhAccountId() { return xhAccountId; } public void setXhAccountId(Long aid) { this.xhAccountId = aid; }
    public LocalDateTime getPublishTime() { return publishTime; } public void setPublishTime(LocalDateTime t) { this.publishTime = t; }
    public Integer getPublishStatus() { return publishStatus; } public void setPublishStatus(Integer s) { this.publishStatus = s; }
    public String getPublishResult() { return publishResult; } public void setPublishResult(String r) { this.publishResult = r; }
    public Integer getSortOrder() { return sortOrder; } public void setSortOrder(Integer o) { this.sortOrder = o; }
    public Integer getRetryCount() { return retryCount; } public void setRetryCount(Integer c) { this.retryCount = c; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime t) { this.createTime = t; }
    public LocalDateTime getActualPublishTime() { return actualPublishTime; } public void setActualPublishTime(LocalDateTime t) { this.actualPublishTime = t; }
    public Integer getDeleted() { return deleted; } public void setDeleted(Integer d) { this.deleted = d; }
}
