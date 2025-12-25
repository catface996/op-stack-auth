package com.catface996.auth.interfaces.http.controller;

import com.catface996.auth.application.command.LoginCommand;
import com.catface996.auth.application.command.RegisterCommand;
import com.catface996.auth.application.result.AuthResult;
import com.catface996.auth.application.result.UserInfo;
import com.catface996.auth.application.service.AuthService;
import com.catface996.auth.common.result.Result;
import com.catface996.auth.interfaces.http.dto.request.LoginRequest;
import com.catface996.auth.interfaces.http.dto.request.RegisterRequest;
import com.catface996.auth.interfaces.http.dto.response.LoginResponse;
import com.catface996.auth.interfaces.http.dto.response.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<Result<RegisterResponse>> register(
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

    /**
     * Login with username/email and password
     */
    @PostMapping("/login")
    public ResponseEntity<Result<LoginResponse>> login(
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

    /**
     * Logout current user
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }

        return ResponseEntity.ok(Result.success());
    }

    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Result<LoginResponse>> refresh(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        AuthResult authResult = authService.refreshToken(token);
        LoginResponse response = LoginResponse.from(authResult);

        return ResponseEntity.ok(Result.success(response));
    }

    /**
     * Get current user profile
     */
    @GetMapping("/me")
    public ResponseEntity<Result<UserInfo>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        UserInfo userInfo = authService.getCurrentUser(token);

        return ResponseEntity.ok(Result.success(userInfo));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
