package com.hongshu.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
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
}
