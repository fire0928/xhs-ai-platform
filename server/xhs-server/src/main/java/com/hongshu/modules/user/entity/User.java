package com.hongshu.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String password;
    private String nickname;
    private String email;
    private LocalDateTime registerTime;
    private LocalDateTime lastLoginTime;
    private Integer status;        // 0-禁用, 1-正常
    private Long defaultAgentId;
    private String registerTerminal;
    private Integer memberLevel;   // 0-免费, 1-标准, 2-专业
    private LocalDateTime memberExpireTime;
    @TableLogic
    private Integer deleted;
}
