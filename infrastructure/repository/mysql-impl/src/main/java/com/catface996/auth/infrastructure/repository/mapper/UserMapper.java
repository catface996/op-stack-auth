package com.catface996.auth.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catface996.auth.infrastructure.repository.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus mapper for User entity
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * Update failed attempts and lock status
     */
    @Update("UPDATE users SET failed_attempts = #{attempts}, locked_until = #{lockedUntil}, " +
            "updated_at = NOW() WHERE id = #{userId}")
    void updateFailedAttempts(@Param("userId") Long userId,
                              @Param("attempts") int attempts,
                              @Param("lockedUntil") LocalDateTime lockedUntil);

    /**
     * Update user status
     */
    @Update("UPDATE users SET status = #{status}, updated_at = NOW() WHERE id = #{userId}")
    void updateStatus(@Param("userId") Long userId, @Param("status") String status);
}
