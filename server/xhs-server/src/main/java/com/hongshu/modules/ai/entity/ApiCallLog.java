package com.hongshu.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
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
}
