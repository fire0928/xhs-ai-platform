package com.hongshu.modules.account.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
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
}
