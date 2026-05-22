package com.hongshu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 小红书AI自动创作更新系统 - 启动类
 */
@SpringBootApplication
@MapperScan("com.hongshu.**.mapper")
@EnableTransactionManagement
@EnableScheduling
public class XhsApplication {

    public static void main(String[] args) {
        SpringApplication.run(XhsApplication.class, args);
    }
}
