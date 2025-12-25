package com.catface996.auth.interfaces.http.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response DTO for user registration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private Long id;
    private String username;
    private String email;
    private String status;
    private Set<String> roles;
    private String message;

    public static RegisterResponse from(Long id, String username, String email, String status, Set<String> roles) {
        return RegisterResponse.builder()
                .id(id)
                .username(username)
                .email(email)
                .status(status)
                .roles(roles)
                .message("User registered successfully")
                .build();
    }
}
