package com.catface996.auth.bootstrap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Authentication Service Application Entry Point
 */
@SpringBootApplication(scanBasePackages = "com.catface996.auth")
@MapperScan("com.catface996.auth.infrastructure.repository.mapper")
@ConfigurationPropertiesScan("com.catface996.auth")
@EnableScheduling
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
