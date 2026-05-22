package com.hongshu.modules.account.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("xh_account_data")
public class XhAccountData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long xhAccountId;
    private LocalDate recordDate;
    private Integer views;
    private Integer likes;
    private Integer collects;
    private Integer comments;
    private Integer fans;
    private Integer newFans;
    private LocalDateTime createTime;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getXhAccountId() { return xhAccountId; } public void setXhAccountId(Long id) { this.xhAccountId = id; }
    public LocalDate getRecordDate() { return recordDate; } public void setRecordDate(LocalDate d) { this.recordDate = d; }
    public Integer getViews() { return views; } public void setViews(Integer v) { this.views = v; }
    public Integer getLikes() { return likes; } public void setLikes(Integer l) { this.likes = l; }
    public Integer getCollects() { return collects; } public void setCollects(Integer c) { this.collects = c; }
    public Integer getComments() { return comments; } public void setComments(Integer c) { this.comments = c; }
    public Integer getFans() { return fans; } public void setFans(Integer f) { this.fans = f; }
    public Integer getNewFans() { return newFans; } public void setNewFans(Integer n) { this.newFans = n; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime t) { this.createTime = t; }
}
