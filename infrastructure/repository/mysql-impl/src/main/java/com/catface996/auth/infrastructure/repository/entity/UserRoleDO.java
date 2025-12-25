package com.catface996.auth.infrastructure.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * User-Role mapping database entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_roles")
public class UserRoleDO {

    @TableField("user_id")
    private Long userId;

    @TableField("role_id")
    private Long roleId;
}
