-- ============================================
-- 小红书AI自动创作更新系统 - 初始化数据库
-- ============================================

CREATE DATABASE IF NOT EXISTS xhs_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xhs_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `phone` VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号',
    `password` VARCHAR(128) NOT NULL COMMENT 'MD5+盐值加密',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '用户昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `register_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER/ADMIN',
    `default_agent_id` BIGINT DEFAULT NULL COMMENT '默认Agent ID',
    `register_terminal` VARCHAR(20) NOT NULL DEFAULT 'computer' COMMENT '注册终端: computer/harmony/mini_program',
    `member_level` TINYINT DEFAULT 0 COMMENT '会员等级: 0-免费, 1-标准, 2-专业',
    `member_expire_time` DATETIME DEFAULT NULL COMMENT '会员到期时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX `idx_phone` (`phone`),
    INDEX `idx_status` (`status`),
    INDEX `idx_register_time` (`register_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 小红书账号绑定表
CREATE TABLE IF NOT EXISTS `xiaohongshu_account` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
    `xh_account` VARCHAR(256) NOT NULL COMMENT '小红书账号(加密存储)',
    `xh_password` VARCHAR(256) DEFAULT NULL COMMENT '小红书密码(加密存储)',
    `xh_nickname` VARCHAR(50) DEFAULT NULL COMMENT '小红书昵称',
    `xh_avatar` VARCHAR(500) DEFAULT NULL COMMENT '小红书头像URL',
    `bind_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    `is_default` TINYINT DEFAULT 1 COMMENT '是否默认: 0-否, 1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-失效, 1-正常',
    `cookie_json` TEXT DEFAULT NULL COMMENT '浏览器Cookie(加密存储)',
    `session_status` TINYINT DEFAULT 0 COMMENT '会话状态: 0-离线, 1-在线, 2-过期',
    `last_active_time` DATETIME DEFAULT NULL COMMENT '最后活跃时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小红书账号绑定表';

-- Agent 表
CREATE TABLE IF NOT EXISTS `agent` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT 'Agent名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `style` VARCHAR(200) DEFAULT NULL COMMENT '创作风格',
    `domain` VARCHAR(100) DEFAULT NULL COMMENT '适用领域',
    `api_id` BIGINT DEFAULT NULL COMMENT '绑定的API模型ID',
    `prompt_template` TEXT DEFAULT NULL COMMENT '提示词模板',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent表';

-- API模型配置表
CREATE TABLE IF NOT EXISTS `ai_model_api` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT 'API名称',
    `api_url` VARCHAR(500) NOT NULL COMMENT '调用地址',
    `api_key` VARCHAR(500) NOT NULL COMMENT 'API密钥(加密)',
    `model_name` VARCHAR(100) DEFAULT NULL COMMENT '模型名称',
    `request_params` TEXT DEFAULT NULL COMMENT '请求参数JSON',
    `rate_limit` INT DEFAULT 60 COMMENT '频率限制(次/分钟)',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `used_tokens` BIGINT DEFAULT 0 COMMENT '已使用额度',
    `total_tokens` BIGINT DEFAULT 0 COMMENT '总额度',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI模型API配置表';

-- AI创作内容表
CREATE TABLE IF NOT EXISTS `ai_content` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `agent_id` BIGINT DEFAULT NULL COMMENT 'Agent ID',
    `api_id` BIGINT DEFAULT NULL COMMENT '使用的API ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '文案内容',
    `image_urls` VARCHAR(2000) DEFAULT NULL COMMENT '配图URL,逗号分隔',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '话题标签',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    `audit_status` TINYINT DEFAULT 0 COMMENT '审核状态: 0-草稿, 1-审核中, 2-审核通过, 3-审核失败, 4-已删除',
    `create_terminal` VARCHAR(20) NOT NULL DEFAULT 'computer' COMMENT '生成终端',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_audit_status` (`audit_status`),
    INDEX `idx_create_time` (`create_time`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI创作内容表';

-- 发布队列表
CREATE TABLE IF NOT EXISTS `publish_queue` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content_id` BIGINT NOT NULL COMMENT '内容ID',
    `xh_account_id` BIGINT DEFAULT NULL COMMENT '发布账号ID',
    `publish_time` DATETIME NOT NULL COMMENT '发布时间',
    `publish_status` TINYINT DEFAULT 0 COMMENT '状态: 0-待发布, 1-发布中, 2-已发布, 3-发布失败',
    `publish_result` VARCHAR(500) DEFAULT NULL COMMENT '发布结果',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `retry_count` TINYINT DEFAULT 0 COMMENT '重试次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `actual_publish_time` DATETIME DEFAULT NULL COMMENT '实际发布时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_publish_status` (`publish_status`),
    INDEX `idx_publish_time` (`publish_time`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`content_id`) REFERENCES `ai_content`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发布队列表';

-- 发布日志表
CREATE TABLE IF NOT EXISTS `publish_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `queue_id` BIGINT NOT NULL COMMENT '队列ID',
    `step` VARCHAR(50) DEFAULT NULL COMMENT '步骤: init/upload/input/publish/verify',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-进行中, 1-成功, 2-失败',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `screenshot_url` VARCHAR(500) DEFAULT NULL COMMENT '失败截图URL',
    `duration_ms` INT DEFAULT 0 COMMENT '耗时(ms)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_queue_id` (`queue_id`),
    FOREIGN KEY (`queue_id`) REFERENCES `publish_queue`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发布日志表';

-- 小红书账号数据表
CREATE TABLE IF NOT EXISTS `xh_account_data` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `xh_account_id` BIGINT NOT NULL COMMENT '小红书账号绑定ID',
    `record_date` DATE NOT NULL COMMENT '记录日期',
    `views` INT DEFAULT 0 COMMENT '浏览量',
    `likes` INT DEFAULT 0 COMMENT '点赞量',
    `collects` INT DEFAULT 0 COMMENT '收藏量',
    `comments` INT DEFAULT 0 COMMENT '评论量',
    `fans` INT DEFAULT 0 COMMENT '粉丝数',
    `new_fans` INT DEFAULT 0 COMMENT '新增粉丝',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_account_date` (`xh_account_id`, `record_date`),
    INDEX `idx_account_id` (`xh_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小红书账号数据表';

-- API调用记录表
CREATE TABLE IF NOT EXISTS `api_call_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `api_id` BIGINT DEFAULT NULL COMMENT 'API ID',
    `agent_id` BIGINT DEFAULT NULL COMMENT 'Agent ID',
    `terminal` VARCHAR(20) DEFAULT NULL COMMENT '终端',
    `request_content` TEXT DEFAULT NULL COMMENT '请求内容',
    `response_content` TEXT DEFAULT NULL COMMENT '响应内容',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-失败, 1-成功',
    `duration_ms` INT DEFAULT 0 COMMENT '耗时',
    `tokens_used` INT DEFAULT 0 COMMENT '消耗Token',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API调用记录表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `operation` VARCHAR(100) NOT NULL COMMENT '操作类型',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '操作内容',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `config_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    `config_value` TEXT DEFAULT NULL COMMENT '配置值',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================
-- 初始化默认数据
-- ============================================

-- 默认超级管理员 (手机号: 13800000000, 密码: admin123456)
INSERT INTO `user` (`phone`, `password`, `nickname`, `email`, `register_time`, `status`, `role`, `register_terminal`, `member_level`) VALUES
('13800000000', 'ecfda3814781cbf585669833af918e00', '超级管理员', 'admin@hongshu.ai', NOW(), 1, 'ADMIN', 'computer', 0);

-- 默认测试用户 (手机号: 13900000001, 密码: user123456)
INSERT INTO `user` (`phone`, `password`, `nickname`, `email`, `register_time`, `status`, `role`, `register_terminal`, `member_level`) VALUES
('13900000001', 'ba93116dffb57d4af13c5c51678039d2', '测试用户', 'test@hongshu.ai', NOW(), 1, 'USER', 'computer', 0);

-- 默认Agent
INSERT INTO `agent` (`name`, `description`, `style`, `domain`, `sort_order`, `prompt_template`) VALUES
('生活分享 Agent', '擅长日常生活类内容创作，风格轻松自然，适合生活方式博主', '轻松自然', '生活方式', 1, '你是一个擅长生活分享的小红书博主，创作风格轻松自然。请根据用户输入的主题创作高质量的文案。'),
('美食探店 Agent', '专注美食领域，写出令人食欲大开的探店内容和食谱分享', '生动诱人', '美食探店', 2, '你是一个美食探店博主，擅长写出令人食欲大开的美食内容。请根据用户输入的主题创作诱人的探店文案。'),
('旅行攻略 Agent', '编写详细的旅行攻略和路线推荐，适合旅行博主', '干货详细', '旅行攻略', 3, '你是一个旅行攻略博主，擅长编写详细的旅行指南和路线推荐。请根据用户输入的目的地创作实用的攻略内容。'),
('穿搭时尚 Agent', '时尚穿搭内容创作，了解流行趋势和搭配技巧', '时尚专业', '穿搭时尚', 4, '你是一个时尚穿搭博主，对流行趋势和搭配技巧了如指掌。请根据用户需求创作时尚的穿搭分享。');

-- 默认系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('register_enabled', 'true', '是否开放注册'),
('default_member_level', '0', '新用户默认会员等级'),
('sms_code_expire_minutes', '5', '验证码有效期(分钟)'),
('publish_daily_limit', '5', '单账号每日发布上限'),
('publish_interval_minutes', '30', '发布最小间隔(分钟)'),
('content_sensitive_words', '[]', '敏感词列表JSON'),
('audit_auto_detect', 'true', '是否自动检测违规内容');
