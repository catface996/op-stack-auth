package com.catface996.auth.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catface996.auth.domain.model.user.LoginAttempt;
import com.catface996.auth.domain.repository.LoginAttemptRepository;
import com.catface996.auth.infrastructure.repository.entity.LoginAttemptDO;
import com.catface996.auth.infrastructure.repository.mapper.LoginAttemptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL implementation of LoginAttemptRepository
 */
@Repository
@RequiredArgsConstructor
public class LoginAttemptRepositoryImpl implements LoginAttemptRepository {

    private final LoginAttemptMapper loginAttemptMapper;

    @Override
    public void save(LoginAttempt attempt) {
        LoginAttemptDO entity = toEntity(attempt);
        entity.setCreatedAt(LocalDateTime.now());
        loginAttemptMapper.insert(entity);
        attempt.setId(entity.getId());
    }

    @Override
    public List<LoginAttempt> findByUserId(Long userId, int limit) {
        LambdaQueryWrapper<LoginAttemptDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginAttemptDO::getUserId, userId)
               .orderByDesc(LoginAttemptDO::getCreatedAt)
               .last("LIMIT " + limit);
        return loginAttemptMapper.selectList(wrapper)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int countRecentFailedAttempts(Long userId, LocalDateTime since) {
        return loginAttemptMapper.countRecentFailedAttempts(userId, since);
    }

    @Override
    public List<LoginAttempt> findByUsername(String username, int limit) {
        LambdaQueryWrapper<LoginAttemptDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginAttemptDO::getUsername, username)
               .orderByDesc(LoginAttemptDO::getCreatedAt)
               .last("LIMIT " + limit);
        return loginAttemptMapper.selectList(wrapper)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private LoginAttempt toDomain(LoginAttemptDO entity) {
        return LoginAttempt.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .success(entity.getSuccess())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .failureReason(entity.getFailureReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private LoginAttemptDO toEntity(LoginAttempt attempt) {
        LoginAttemptDO entity = new LoginAttemptDO();
        entity.setId(attempt.getId());
        entity.setUserId(attempt.getUserId());
        entity.setUsername(attempt.getUsername());
        entity.setSuccess(attempt.isSuccess());
        entity.setIpAddress(attempt.getIpAddress());
        entity.setUserAgent(attempt.getUserAgent());
        entity.setFailureReason(attempt.getFailureReason());
        entity.setCreatedAt(attempt.getCreatedAt());
        return entity;
    }
}
