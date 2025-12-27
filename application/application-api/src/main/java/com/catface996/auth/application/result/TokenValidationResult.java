package com.catface996.auth.application.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Result of token validation operation (used by Gateway)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResult {

    /**
     * Whether the token is valid
     */
    private boolean valid;

    /**
     * User ID extracted from token
     */
    private Long userId;

    /**
     * Username extracted from token
     */
    private String username;

    /**
     * User roles extracted from token
     */
    private Set<String> roles;

    /**
     * Error message if token is invalid
     */
    private String errorMessage;

    /**
     * Create a successful validation result
     */
    public static TokenValidationResult success(Long userId, String username, Set<String> roles) {
        return TokenValidationResult.builder()
                .valid(true)
                .userId(userId)
                .username(username)
                .roles(roles)
                .build();
    }

    /**
     * Create a failed validation result
     */
    public static TokenValidationResult failure(String errorMessage) {
        return TokenValidationResult.builder()
                .valid(false)
                .errorMessage(errorMessage)
                .build();
    }
}
