package com.hongshu.modules.user.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String phone;
    private String nickname;
    private String email;
    private LocalDateTime registerTime;
    private LocalDateTime lastLoginTime;
    private Integer status;
    private Integer memberLevel;
    private LocalDateTime memberExpireTime;
    private String registerTerminal;
    private Long defaultAgentId;
}
