package com.catface996.auth.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catface996.auth.infrastructure.repository.entity.LoginAttemptDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus mapper for LoginAttempt entity
 */
@Mapper
public interface LoginAttemptMapper extends BaseMapper<LoginAttemptDO> {

    /**
     * Count failed attempts since a given time
     */
    @Select("SELECT COUNT(*) FROM login_attempts " +
            "WHERE user_id = #{userId} AND success = false AND created_at >= #{since}")
    int countRecentFailedAttempts(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
