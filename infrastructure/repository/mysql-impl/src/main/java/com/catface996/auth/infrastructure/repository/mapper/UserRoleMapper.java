package com.catface996.auth.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catface996.auth.infrastructure.repository.entity.UserRoleDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis-Plus mapper for User-Role mapping
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleDO> {

    /**
     * Assign role to user
     */
    @Insert("INSERT INTO user_roles (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void assignRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * Remove role from user
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    void removeRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
