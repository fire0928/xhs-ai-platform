package com.hongshu.modules.account.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("xiaohongshu_account")
public class XiaohongshuAccount {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String xhAccount;
    private String xhPassword;
    private String xhNickname;
    private String xhAvatar;
    private LocalDateTime bindTime;
    private Integer isDefault;     // 0-否, 1-是
    private Integer status;        // 0-失效, 1-正常
    private String cookieJson;
    private Integer sessionStatus; // 0-离线, 1-在线, 2-过期
    private LocalDateTime lastActiveTime;
    @TableLogic
    private Integer deleted;
}
