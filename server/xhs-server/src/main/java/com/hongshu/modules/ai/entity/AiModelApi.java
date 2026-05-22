package com.hongshu.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_model_api")
public class AiModelApi {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String apiUrl;
    private String apiKey;
    private String modelName;
    private String requestParams;
    private Integer rateLimit;
    private Integer status;        // 0-禁用, 1-启用
    private Long usedTokens;
    private Long totalTokens;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
