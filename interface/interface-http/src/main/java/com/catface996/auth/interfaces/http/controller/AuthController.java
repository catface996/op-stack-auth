package com.catface996.auth.interfaces.http.controller;

import com.catface996.auth.application.command.LoginCommand;
import com.catface996.auth.application.command.RegisterCommand;
import com.catface996.auth.application.result.AuthResult;
import com.catface996.auth.application.result.TokenValidationResult;
import com.catface996.auth.application.result.UserInfo;
import com.catface996.auth.application.service.AuthService;
import com.catface996.auth.common.result.Result;
import com.catface996.auth.interfaces.http.dto.request.LoginRequest;
import com.catface996.auth.interfaces.http.dto.request.RegisterRequest;
import com.catface996.auth.interfaces.http.dto.response.TokenValidationResponse;
import com.catface996.auth.interfaces.http.dto.response.LoginResponse;
import com.catface996.auth.interfaces.http.dto.response.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器 - 处理用户注册、登录、登出等认证相关操作
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/v1")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证与授权相关接口，包括注册、登录、登出、Token刷新和验证等功能")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户注册", description = "创建新用户账号，需要提供用户名、邮箱和密码。用户名需为3-32位字母数字下划线，密码需包含大小写字母和数字，长度8-128位。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "注册成功，返回用户信息"),
            @ApiResponse(responseCode = "400", description = "请求参数不合法"),
            @ApiResponse(responseCode = "409", description = "用户名或邮箱已被注册")
    })
    @PostMapping("/register")
    public ResponseEntity<Result<RegisterResponse>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户注册信息", required = true)
            @Valid @RequestBody RegisterRequest request) {

        log.info("Processing registration request for username: {}", request.getUsername());

        RegisterCommand command = RegisterCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        UserInfo userInfo = authService.register(command);

        RegisterResponse response = RegisterResponse.from(
                userInfo.getId(),
                userInfo.getUsername(),
                userInfo.getEmail(),
                userInfo.getStatus(),
                userInfo.getRoles()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success(response));
    }

    @Operation(summary = "用户登录", description = "使用用户名/邮箱和密码进行身份认证，认证成功后返回JWT访问令牌。支持记住我功能，开启后令牌有效期延长至30天。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功，返回访问令牌和用户信息"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "423", description = "账号已被锁定，请稍后重试")
    })
    @PostMapping("/login")
    public ResponseEntity<Result<LoginResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户登录凭证", required = true)
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest) {

        log.info("Processing login request for identifier: {}", request.getIdentifier());

        LoginCommand command = LoginCommand.builder()
                .identifier(request.getIdentifier())
                .password(request.getPassword())
                .rememberMe(request.isRememberMe())
                .ipAddress(getClientIp(servletRequest))
                .userAgent(servletRequest.getHeader("User-Agent"))
                .build();

        AuthResult authResult = authService.login(command);

        LoginResponse response = LoginResponse.from(authResult);

        return ResponseEntity.ok(Result.success(response));
    }

    @Operation(summary = "用户登出", description = "使当前JWT令牌失效，将令牌加入黑名单。登出后该令牌无法再用于访问受保护的资源。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登出成功")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Result<Void>> logout(
            @Parameter(description = "Bearer令牌，格式：Bearer {token}", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }

        return ResponseEntity.ok(Result.success());
    }

    @Operation(summary = "刷新令牌", description = "使用当前有效的令牌获取新的访问令牌。适用于令牌即将过期时进行无感续期，新令牌会继承原令牌的记住我设置。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "刷新成功，返回新的访问令牌"),
            @ApiResponse(responseCode = "401", description = "令牌无效或已过期")
    })
    @PostMapping("/refresh")
    public ResponseEntity<Result<LoginResponse>> refresh(
            @Parameter(description = "Bearer令牌，格式：Bearer {token}", required = true)
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        AuthResult authResult = authService.refreshToken(token);
        LoginResponse response = LoginResponse.from(authResult);

        return ResponseEntity.ok(Result.success(response));
    }

    @Operation(summary = "获取当前用户信息", description = "根据访问令牌获取当前登录用户的详细信息，包括用户ID、用户名、邮箱、状态和角色列表。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功，返回用户信息"),
            @ApiResponse(responseCode = "401", description = "令牌无效或缺失")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<Result<UserInfo>> getCurrentUser(
            @Parameter(description = "Bearer令牌，格式：Bearer {token}", required = true)
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        UserInfo userInfo = authService.getCurrentUser(token);

        return ResponseEntity.ok(Result.success(userInfo));
    }

    @Operation(
            summary = "验证令牌（Gateway专用）",
            description = "验证JWT令牌的有效性并返回用户信息。此接口供API网关调用，用于在请求转发到下游服务之前进行身份认证。返回结果中的valid字段表示令牌是否有效。"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "返回验证结果，通过valid字段判断令牌是否有效")
    })
    @GetMapping("/validate")
    public ResponseEntity<Result<TokenValidationResponse>> validateToken(
            @Parameter(description = "待验证的Bearer令牌，格式：Bearer {token}")
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            TokenValidationResponse response = TokenValidationResponse.failure("Missing or invalid Authorization header");
            return ResponseEntity.ok(Result.success(response));
        }

        String token = authHeader.substring(7);
        TokenValidationResult result = authService.validateToken(token);

        TokenValidationResponse response;
        if (result.isValid()) {
            response = TokenValidationResponse.success(
                    result.getUserId(),
                    result.getUsername(),
                    result.getRoles()
            );
        } else {
            response = TokenValidationResponse.failure(result.getErrorMessage());
        }

        return ResponseEntity.ok(Result.success(response));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
