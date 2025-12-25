package com.catface996.auth.interfaces.http.dto.response;

import com.catface996.auth.application.result.AuthResult;
import com.catface996.auth.application.result.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response DTO for user login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserDetails user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDetails {
        private Long id;
        private String username;
        private String email;
        private String status;
        private Set<String> roles;
    }

    public static LoginResponse from(AuthResult authResult) {
        UserInfo userInfo = authResult.getUser();
        return LoginResponse.builder()
                .accessToken(authResult.getAccessToken())
                .tokenType(authResult.getTokenType())
                .expiresIn(authResult.getExpiresIn())
                .user(UserDetails.builder()
                        .id(userInfo.getId())
                        .username(userInfo.getUsername())
                        .email(userInfo.getEmail())
                        .status(userInfo.getStatus())
                        .roles(userInfo.getRoles())
                        .build())
                .build();
    }
}
