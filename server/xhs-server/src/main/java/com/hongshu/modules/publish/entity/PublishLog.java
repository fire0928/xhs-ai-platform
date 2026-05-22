package com.hongshu.modules.publish.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
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
}
