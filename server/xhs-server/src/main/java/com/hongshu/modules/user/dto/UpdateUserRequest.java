package com.hongshu.modules.user.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String nickname;
    private String email;
    private Long defaultAgentId;
}
