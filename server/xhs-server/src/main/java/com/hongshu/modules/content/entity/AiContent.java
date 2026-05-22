package com.hongshu.modules.content.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_content")
public class AiContent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long agentId;
    private Long apiId;
    private String title;
    private String content;
    private String imageUrls;
    private String tags;
    private LocalDateTime createTime;
    private Integer auditStatus;  // 0-草稿, 1-审核中, 2-审核通过, 3-审核失败, 4-已删除
    private String createTerminal;
    @TableLogic
    private Integer deleted;
}
