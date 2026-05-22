package com.hongshu.modules.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("member_order")
public class MemberOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private String userName;
    private Long planId;
    private String planName;
    private Integer amount;
    private Integer status;
    private String payType;
    private String remark;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; } public void setOrderNo(String o) { this.orderNo = o; }
    public Long getUserId() { return userId; } public void setUserId(Long u) { this.userId = u; }
    public String getUserName() { return userName; } public void setUserName(String u) { this.userName = u; }
    public Long getPlanId() { return planId; } public void setPlanId(Long p) { this.planId = p; }
    public String getPlanName() { return planName; } public void setPlanName(String p) { this.planName = p; }
    public Integer getAmount() { return amount; } public void setAmount(Integer a) { this.amount = a; }
    public Integer getStatus() { return status; } public void setStatus(Integer s) { this.status = s; }
    public String getPayType() { return payType; } public void setPayType(String p) { this.payType = p; }
    public String getRemark() { return remark; } public void setRemark(String r) { this.remark = r; }
    public LocalDateTime getPayTime() { return payTime; } public void setPayTime(LocalDateTime t) { this.payTime = t; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime t) { this.createTime = t; }
    public Integer getDeleted() { return deleted; } public void setDeleted(Integer d) { this.deleted = d; }
}
