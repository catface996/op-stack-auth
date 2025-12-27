package com.catface996.auth.bootstrap.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger 配置类
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:9090}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("认证服务 API")
                        .description("认证服务接口文档 - 提供用户注册、登录、登出、令牌刷新和验证等功能。\n\n" +
                                "## 认证方式\n" +
                                "本服务使用 JWT (JSON Web Token) 进行身份认证。\n\n" +
                                "### 获取令牌\n" +
                                "1. 调用 `/api/auth/v1/register` 注册新用户\n" +
                                "2. 调用 `/api/auth/v1/login` 登录获取访问令牌\n\n" +
                                "### 使用令牌\n" +
                                "在请求头中添加：`Authorization: Bearer {token}`\n\n" +
                                "### 令牌有效期\n" +
                                "- 普通登录：1小时\n" +
                                "- 记住我：30天")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("catface996")
                                .url("https://github.com/catface996"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("本地开发环境")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("请输入JWT令牌（不需要Bearer前缀）")));
    }
}
