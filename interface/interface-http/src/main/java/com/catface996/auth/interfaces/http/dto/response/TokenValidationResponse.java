package com.catface996.auth.interfaces.http.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response DTO for token validation (used by Gateway)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {

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
    private String message;

    /**
     * Create a successful validation response
     */
    public static TokenValidationResponse success(Long userId, String username, Set<String> roles) {
        return TokenValidationResponse.builder()
                .valid(true)
                .userId(userId)
                .username(username)
                .roles(roles)
                .build();
    }

    /**
     * Create a failed validation response
     */
    public static TokenValidationResponse failure(String message) {
        return TokenValidationResponse.builder()
                .valid(false)
                .message(message)
                .build();
    }
}
