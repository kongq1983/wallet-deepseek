package com.hzsun.aidevops.bootstrap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI 研发平台后端启动类。
 *
 * <p>负责 Spring Boot 应用的装配与启动，Mapper 扫描范围限定在基础设施层。</p>
 */
@SpringBootApplication(scanBasePackages = "com.hzsun.aidevops")
@MapperScan("com.hzsun.aidevops.**.infrastructure.mapper")
public class AiDevOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDevOpsApplication.class, args);
    }
}
