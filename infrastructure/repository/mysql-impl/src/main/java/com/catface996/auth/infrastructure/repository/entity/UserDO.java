package com.catface996.auth.infrastructure.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * User database entity
 */
@Data
@TableName("users")
public class UserDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String email;

    @TableField("password_hash")
    private String passwordHash;

    private String status;

    @TableField("failed_attempts")
    private Integer failedAttempts;

    @TableField("locked_until")
    private LocalDateTime lockedUntil;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
