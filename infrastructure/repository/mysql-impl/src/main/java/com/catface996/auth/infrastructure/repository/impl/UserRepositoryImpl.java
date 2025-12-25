package com.catface996.auth.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catface996.auth.domain.model.user.User;
import com.catface996.auth.domain.model.user.UserStatus;
import com.catface996.auth.domain.repository.UserRepository;
import com.catface996.auth.infrastructure.repository.entity.UserDO;
import com.catface996.auth.infrastructure.repository.mapper.RoleMapper;
import com.catface996.auth.infrastructure.repository.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MySQL implementation of UserRepository
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    public Optional<User> findById(Long id) {
        UserDO userDO = userMapper.selectById(id);
        return Optional.ofNullable(userDO).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, username);
        UserDO userDO = userMapper.selectOne(wrapper);
        return Optional.ofNullable(userDO).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getEmail, email);
        UserDO userDO = userMapper.selectOne(wrapper);
        return Optional.ofNullable(userDO).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String identifier) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, identifier)
               .or()
               .eq(UserDO::getEmail, identifier);
        UserDO userDO = userMapper.selectOne(wrapper);
        return Optional.ofNullable(userDO).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, username);
        return userMapper.exists(wrapper);
    }

    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getEmail, email);
        return userMapper.exists(wrapper);
    }

    @Override
    public User save(User user) {
        UserDO userDO = toEntity(user);
        if (user.getId() == null) {
            userDO.setCreatedAt(LocalDateTime.now());
            userDO.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(userDO);
            user.setId(userDO.getId());
        } else {
            userDO.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(userDO);
        }
        return user;
    }

    @Override
    public void updateFailedAttempts(Long userId, int attempts, LocalDateTime lockedUntil) {
        userMapper.updateFailedAttempts(userId, attempts, lockedUntil);
    }

    @Override
    public void updateStatus(Long userId, UserStatus status) {
        userMapper.updateStatus(userId, status.name());
    }

    private User toDomain(UserDO userDO) {
        Set<String> roles = roleMapper.findByUserId(userDO.getId())
                .stream()
                .map(r -> r.getName())
                .collect(Collectors.toSet());

        return User.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .email(userDO.getEmail())
                .passwordHash(userDO.getPasswordHash())
                .status(UserStatus.valueOf(userDO.getStatus()))
                .failedAttempts(userDO.getFailedAttempts())
                .lockedUntil(userDO.getLockedUntil())
                .createdAt(userDO.getCreatedAt())
                .updatedAt(userDO.getUpdatedAt())
                .roles(roles)
                .build();
    }

    private UserDO toEntity(User user) {
        UserDO userDO = new UserDO();
        userDO.setId(user.getId());
        userDO.setUsername(user.getUsername());
        userDO.setEmail(user.getEmail());
        userDO.setPasswordHash(user.getPasswordHash());
        userDO.setStatus(user.getStatus().name());
        userDO.setFailedAttempts(user.getFailedAttempts());
        userDO.setLockedUntil(user.getLockedUntil());
        userDO.setCreatedAt(user.getCreatedAt());
        userDO.setUpdatedAt(user.getUpdatedAt());
        return userDO;
    }
}
