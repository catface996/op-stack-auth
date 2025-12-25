package com.catface996.auth.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catface996.auth.infrastructure.repository.entity.RoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * MyBatis-Plus mapper for Role entity
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleDO> {

    /**
     * Find all roles for a user
     */
    @Select("SELECT r.* FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<RoleDO> findByUserId(@Param("userId") Long userId);
}
