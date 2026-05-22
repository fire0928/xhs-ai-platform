package com.hongshu.modules.user.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.exception.BusinessException;
import com.hongshu.common.security.JwtUtil;
import com.hongshu.common.util.CryptoUtil;
import com.hongshu.modules.user.entity.User;
import com.hongshu.modules.user.mapper.UserMapper;
import com.hongshu.modules.user.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final CryptoUtil cryptoUtil;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserService(CryptoUtil cryptoUtil, JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.cryptoUtil = cryptoUtil;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    private static final String CODE_PREFIX = "sms:code:";
    private static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * 发送验证码
     */
    public void sendVerifyCode(String phone) {
        String code = cryptoUtil.generateVerifyCode();
        redisTemplate.opsForValue().set(CODE_PREFIX + phone, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        logger.info("验证码已发送到 {}: {}", phone, code);
        // TODO: 对接短信服务商
    }

    /**
     * 用户注册
     */
    @Transactional
    public LoginResponse register(RegisterRequest req) {
        // 校验验证码
        String cachedCode = (String) redisTemplate.opsForValue().get(CODE_PREFIX + req.getPhone());
        if (cachedCode == null || !cachedCode.equals(req.getCode())) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        // 校验手机号唯一性
        if (existsByPhone(req.getPhone())) {
            throw new BusinessException(400, "该手机号已注册");
        }

        // 创建用户
        User user = new User();
        user.setPhone(req.getPhone());
        user.setPassword(cryptoUtil.encodePassword(req.getPassword()));
        user.setRegisterTime(LocalDateTime.now());
        user.setStatus(1);
        user.setMemberLevel(0);
        user.setRegisterTerminal(req.getTerminal() != null ? req.getTerminal() : "computer");

        save(user);

        // 清除验证码
        redisTemplate.delete(CODE_PREFIX + req.getPhone());

        // 生成token
        return buildLoginResponse(user);
    }

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest req) {
        User user = getUserByPhone(req.getPhone());

        if (!cryptoUtil.verifyPassword(req.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "手机号或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);

        return buildLoginResponse(user);
    }

    /**
     * 刷新Token
     */
    public LoginResponse refreshToken(String refreshToken) {
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        if (userId == null) {
            throw new BusinessException(401, "无效的刷新令牌");
        }
        User user = getById(userId);
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException(403, "账号异常");
        }
        return buildLoginResponse(user);
    }

    /**
     * 重置密码
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        String cachedCode = (String) redisTemplate.opsForValue().get(CODE_PREFIX + req.getPhone());
        if (cachedCode == null || !cachedCode.equals(req.getCode())) {
            throw new BusinessException(400, "验证码错误或已过期");
        }
        User user = getUserByPhone(req.getPhone());
        user.setPassword(cryptoUtil.encodePassword(req.getNewPassword()));
        updateById(user);
        redisTemplate.delete(CODE_PREFIX + req.getPhone());
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        User user = getById(userId);
        if (!cryptoUtil.verifyPassword(req.getOldPassword(), user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        user.setPassword(cryptoUtil.encodePassword(req.getNewPassword()));
        updateById(user);
    }

    /**
     * 获取用户信息
     */
    public UserDTO getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return toDTO(user);
    }

    /**
     * 更新用户信息
     */
    public void updateUserInfo(Long userId, UpdateUserRequest req) {
        User user = getById(userId);
        if (StrUtil.isNotBlank(req.getNickname())) {
            user.setNickname(req.getNickname());
        }
        if (StrUtil.isNotBlank(req.getEmail())) {
            user.setEmail(req.getEmail());
        }
        if (req.getDefaultAgentId() != null) {
            user.setDefaultAgentId(req.getDefaultAgentId());
        }
        updateById(user);
    }

    /**
     * 分页查询用户(管理后台)
     */
    public IPage<UserDTO> pageUsers(int page, int pageSize, String keyword, Integer status, String terminal) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(User::getPhone, keyword);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        if (StrUtil.isNotBlank(terminal)) {
            wrapper.eq(User::getRegisterTerminal, terminal);
        }
        wrapper.orderByDesc(User::getRegisterTime);

        return page(new Page<>(page, pageSize), wrapper).convert(this::toDTO);
    }

    /**
     * 禁/启用用户
     */
    public void toggleUserStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setStatus(status);
        updateById(user);
    }

    // ---- 内部辅助方法 ----

    private boolean existsByPhone(String phone) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone)) != null;
    }

    private User getUserByPhone(String phone) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            throw new BusinessException(400, "手机号未注册");
        }
        return user;
    }

    private LoginResponse buildLoginResponse(User user) {
        String role = "USER";
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), role);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setRefreshToken(refreshToken);
        resp.setUser(toDTO(user));
        return resp;
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setPhone(maskPhone(user.getPhone()));
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setRegisterTime(user.getRegisterTime());
        dto.setLastLoginTime(user.getLastLoginTime());
        dto.setStatus(user.getStatus());
        dto.setMemberLevel(user.getMemberLevel());
        dto.setMemberExpireTime(user.getMemberExpireTime());
        dto.setRegisterTerminal(user.getRegisterTerminal());
        dto.setDefaultAgentId(user.getDefaultAgentId());
        return dto;
    }

    private String maskPhone(String phone) {
        if (phone != null && phone.length() == 11) {
            return phone.substring(0, 3) + "****" + phone.substring(7);
        }
        return phone;
    }
}
