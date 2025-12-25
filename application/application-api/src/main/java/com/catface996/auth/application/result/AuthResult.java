package com.catface996.auth.application.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of authentication operation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResult {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserInfo user;

    public static AuthResult of(String accessToken, long expiresIn, UserInfo user) {
        return AuthResult.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(user)
                .build();
    }
}
