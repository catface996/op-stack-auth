# Data Model: DDD Auth Migration

**Feature**: 001-ddd-auth-migration
**Date**: 2025-12-25

## Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                           User                                   │
├─────────────────────────────────────────────────────────────────┤
│ id: Long (PK)                                                   │
│ username: String (unique, 3-32 chars, alphanumeric + _)         │
│ email: String (unique, valid email format)                      │
│ passwordHash: String (BCrypt encoded)                           │
│ status: UserStatus (ACTIVE, LOCKED, DISABLED)                   │
│ failedAttempts: Integer (default 0)                             │
│ lockedUntil: LocalDateTime (nullable)                           │
│ createdAt: LocalDateTime                                        │
│ updatedAt: LocalDateTime                                        │
└─────────────────────────────────────────────────────────────────┘
         │
         │ many-to-many
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                         UserRole                                 │
├─────────────────────────────────────────────────────────────────┤
│ userId: Long (FK → User.id)                                     │
│ roleId: Long (FK → Role.id)                                     │
│ (composite PK: userId + roleId)                                 │
└─────────────────────────────────────────────────────────────────┘
         │
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                           Role                                   │
├─────────────────────────────────────────────────────────────────┤
│ id: Long (PK)                                                   │
│ name: String (unique, e.g., "USER", "ADMIN")                    │
│ description: String                                             │
│ createdAt: LocalDateTime                                        │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       LoginAttempt                               │
├─────────────────────────────────────────────────────────────────┤
│ id: Long (PK)                                                   │
│ userId: Long (FK → User.id, nullable for failed with unknown)   │
│ username: String (attempted username/email)                     │
│ success: Boolean                                                │
│ ipAddress: String                                               │
│ userAgent: String                                               │
│ failureReason: String (nullable)                                │
│ createdAt: LocalDateTime                                        │
└─────────────────────────────────────────────────────────────────┘
```

## Entity Definitions

### User (Aggregate Root)

The User entity is the central aggregate in the auth domain.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto-increment | Unique identifier |
| username | String | Unique, 3-32 chars, `^[a-zA-Z0-9_]+$` | Login identifier |
| email | String | Unique, valid email format | User email address |
| passwordHash | String | Not null, BCrypt format | Hashed password |
| status | UserStatus | Not null, default ACTIVE | Account status |
| failedAttempts | Integer | Not null, default 0 | Consecutive failed login count |
| lockedUntil | LocalDateTime | Nullable | Lockout expiry timestamp |
| createdAt | LocalDateTime | Not null | Creation timestamp |
| updatedAt | LocalDateTime | Not null | Last update timestamp |

**Validation Rules:**
- Username: alphanumeric + underscore only, 3-32 characters
- Email: Valid RFC 5322 format
- Password (before hashing): Minimum 8 characters, at least 1 uppercase, 1 lowercase, 1 digit

**State Transitions:**
```
ACTIVE ──[5 failed attempts]──► LOCKED
LOCKED ──[15 min elapsed]──────► ACTIVE (auto-unlock)
ACTIVE ──[admin action]────────► DISABLED
DISABLED ──[admin action]──────► ACTIVE
```

### Role

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto-increment | Unique identifier |
| name | String | Unique, not null | Role name (USER, ADMIN) |
| description | String | Nullable | Human-readable description |
| createdAt | LocalDateTime | Not null | Creation timestamp |

**Predefined Roles:**
- `USER` (id=1): Default role for all registered users
- `ADMIN` (id=2): Administrative access

### UserRole (Join Table)

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| userId | Long | FK → User.id, composite PK | User reference |
| roleId | Long | FK → Role.id, composite PK | Role reference |

### LoginAttempt

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | PK, auto-increment | Unique identifier |
| userId | Long | FK → User.id, nullable | User reference (null if user not found) |
| username | String | Not null | Attempted login identifier |
| success | Boolean | Not null | Whether login succeeded |
| ipAddress | String | Not null | Client IP address |
| userAgent | String | Nullable | Client user agent |
| failureReason | String | Nullable | Reason for failure |
| createdAt | LocalDateTime | Not null | Attempt timestamp |

**Failure Reasons:**
- `USER_NOT_FOUND`
- `INVALID_PASSWORD`
- `ACCOUNT_LOCKED`
- `ACCOUNT_DISABLED`

## Value Objects

### UserStatus (Enum)

```java
public enum UserStatus {
    ACTIVE,    // Normal, can login
    LOCKED,    // Temporarily locked due to failed attempts
    DISABLED   // Administratively disabled
}
```

### TokenClaims (Value Object)

```java
public record TokenClaims(
    Long userId,
    String username,
    Set<String> roles,
    LocalDateTime issuedAt,
    LocalDateTime expiresAt,
    boolean rememberMe
) {}
```

## Database Schema (MySQL)

```sql
-- Users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(32) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    failed_attempts INT NOT NULL DEFAULT 0,
    locked_until DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_users_username (username),
    UNIQUE INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Roles table
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User-Role mapping
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Login attempts for audit/security
CREATE TABLE login_attempts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL,
    username VARCHAR(255) NOT NULL,
    success BOOLEAN NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(500) NULL,
    failure_reason VARCHAR(50) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_login_attempts_user_id (user_id),
    INDEX idx_login_attempts_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed data for roles
INSERT INTO roles (id, name, description) VALUES
(1, 'USER', 'Standard user with basic access'),
(2, 'ADMIN', 'Administrator with full access');
```

## Repository Interfaces

### UserRepository

```java
public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String identifier);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User save(User user);
    void updateFailedAttempts(Long userId, int attempts, LocalDateTime lockedUntil);
    void updateStatus(Long userId, UserStatus status);
}
```

### RoleRepository

```java
public interface RoleRepository {
    Optional<Role> findByName(String name);
    List<Role> findByUserId(Long userId);
    void assignRole(Long userId, Long roleId);
    void removeRole(Long userId, Long roleId);
}
```

### LoginAttemptRepository

```java
public interface LoginAttemptRepository {
    void save(LoginAttempt attempt);
    List<LoginAttempt> findByUserId(Long userId, int limit);
    int countRecentFailedAttempts(Long userId, LocalDateTime since);
}
```

## Domain Invariants

1. **Username Uniqueness**: No two users can have the same username (case-insensitive)
2. **Email Uniqueness**: No two users can have the same email (case-insensitive)
3. **Password Strength**: Password must meet minimum complexity before storage
4. **Lockout Threshold**: Account locks after exactly 5 consecutive failed attempts
5. **Lockout Duration**: Locked accounts auto-unlock after exactly 15 minutes
6. **Role Assignment**: Every user must have at least the USER role
7. **Token Validity**: Tokens must be validated against blacklist before accepting
