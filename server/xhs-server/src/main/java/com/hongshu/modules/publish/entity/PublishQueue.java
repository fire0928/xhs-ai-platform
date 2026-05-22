package com.hongshu.modules.publish.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
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
}
