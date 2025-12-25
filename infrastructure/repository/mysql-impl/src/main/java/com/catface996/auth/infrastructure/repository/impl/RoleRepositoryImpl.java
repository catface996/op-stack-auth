package com.catface996.auth.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catface996.auth.domain.model.role.Role;
import com.catface996.auth.domain.repository.RoleRepository;
import com.catface996.auth.infrastructure.repository.entity.RoleDO;
import com.catface996.auth.infrastructure.repository.mapper.RoleMapper;
import com.catface996.auth.infrastructure.repository.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MySQL implementation of RoleRepository
 */
@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public Optional<Role> findByName(String name) {
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleDO::getName, name);
        RoleDO roleDO = roleMapper.selectOne(wrapper);
        return Optional.ofNullable(roleDO).map(this::toDomain);
    }

    @Override
    public List<Role> findByUserId(Long userId) {
        return roleMapper.findByUserId(userId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void assignRole(Long userId, Long roleId) {
        userRoleMapper.assignRole(userId, roleId);
    }

    @Override
    public void removeRole(Long userId, Long roleId) {
        userRoleMapper.removeRole(userId, roleId);
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.selectList(null)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Role toDomain(RoleDO roleDO) {
        return Role.builder()
                .id(roleDO.getId())
                .name(roleDO.getName())
                .description(roleDO.getDescription())
                .createdAt(roleDO.getCreatedAt())
                .build();
    }
}
