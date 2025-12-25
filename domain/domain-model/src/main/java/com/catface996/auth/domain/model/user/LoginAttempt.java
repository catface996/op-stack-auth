package com.catface996.auth.domain.model.user;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Login attempt domain entity for security audit
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

    /**
     * Failure reasons
     */
    public static final String REASON_USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String REASON_INVALID_PASSWORD = "INVALID_PASSWORD";
    public static final String REASON_ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
    public static final String REASON_ACCOUNT_DISABLED = "ACCOUNT_DISABLED";

    private Long id;
    private Long userId;
    private String username;
    private boolean success;
    private String ipAddress;
    private String userAgent;
    private String failureReason;
    private LocalDateTime createdAt;

    public static LoginAttempt success(Long userId, String username, String ipAddress, String userAgent) {
        return LoginAttempt.builder()
                .userId(userId)
                .username(username)
                .success(true)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static LoginAttempt failure(Long userId, String username, String ipAddress,
                                       String userAgent, String reason) {
        return LoginAttempt.builder()
                .userId(userId)
                .username(username)
                .success(false)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .failureReason(reason)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
