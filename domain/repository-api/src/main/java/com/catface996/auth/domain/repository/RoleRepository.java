package com.catface996.auth.domain.repository;

import com.catface996.auth.domain.model.role.Role;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Role entity
 */
public interface RoleRepository {

    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);

    /**
     * Find all roles assigned to a user
     */
    List<Role> findByUserId(Long userId);

    /**
     * Assign role to user
     */
    void assignRole(Long userId, Long roleId);

    /**
     * Remove role from user
     */
    void removeRole(Long userId, Long roleId);

    /**
     * Find all roles
     */
    List<Role> findAll();
}
