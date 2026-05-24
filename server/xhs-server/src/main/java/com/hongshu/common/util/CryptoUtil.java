package com.hongshu.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * 加密工具类
 * - MD5+盐值 用于密码
 * - AES-256 用于敏感数据
 */
@Component
public class CryptoUtil {

    private static final Logger log = LoggerFactory.getLogger(CryptoUtil.class);

    @Value("${aes.key}")
    private String aesKey;

    private SymmetricCrypto aes;

    private static final String MD5_SALT = "XhsMD5@Salt2024";

    @PostConstruct
    public void init() {
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), aesKey.getBytes(StandardCharsets.UTF_8)).getEncoded();
        this.aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
    }

    /**
     * MD5 + 盐值加密密码（不可逆）
     */
    public String encodePassword(String password) {
        return SecureUtil.md5(password + MD5_SALT);
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return encodePassword(rawPassword).equals(encodedPassword);
    }

    /**
     * AES加密
     */
    public String aesEncrypt(String plainText) {
        return aes.encryptBase64(plainText);
    }

    /**
     * AES解密
     */
    public String aesDecrypt(String cipherText) {
        if (StrUtil.isBlank(cipherText)) return "";
        return aes.decryptStr(cipherText);
    }

    /**
     * 验证码生成
     */
    public String generateVerifyCode() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    }
}
