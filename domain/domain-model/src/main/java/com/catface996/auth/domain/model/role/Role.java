package com.catface996.auth.domain.model.role;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Role domain entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    public static Role user() {
        return Role.builder()
                .id(1L)
                .name(USER)
                .description("Standard user with basic access")
                .build();
    }

    public static Role admin() {
        return Role.builder()
                .id(2L)
                .name(ADMIN)
                .description("Administrator with full access")
                .build();
    }
}
