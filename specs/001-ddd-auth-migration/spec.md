# Feature Specification: DDD Auth Migration

**Feature Branch**: `001-ddd-auth-migration`
**Created**: 2025-12-25
**Status**: Draft
**Input**: User description: "参考op-stack-service的DDD工程结构，在当前目录中复刻一个相同结构的工程，并将相关注册、登录、鉴权功能迁移到当前项目"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - User Registration (Priority: P1)

A new user visits the application and wants to create an account to access protected resources.

**Why this priority**: Registration is the entry point for all users. Without registration capability, no other authentication features can be utilized. This is the foundational user acquisition flow.

**Independent Test**: Can be fully tested by submitting a registration form with valid user information and verifying the account is created and accessible.

**Acceptance Scenarios**:

1. **Given** a visitor on the registration page, **When** they submit valid username, email, and password, **Then** their account is created and they receive a confirmation.
2. **Given** a visitor attempting registration, **When** they submit an email that already exists, **Then** the system displays an appropriate error message without creating a duplicate account.
3. **Given** a visitor attempting registration, **When** they submit a weak password (less than minimum requirements), **Then** the system rejects the submission with password policy guidance.

---

### User Story 2 - User Login (Priority: P1)

A registered user wants to log into the system to access their account and protected features.

**Why this priority**: Login is equally critical as registration - it's the primary authentication flow that enables all subsequent user interactions with the system.

**Independent Test**: Can be fully tested by submitting valid credentials and verifying access is granted with appropriate session/token.

**Acceptance Scenarios**:

1. **Given** a registered user on the login page, **When** they submit correct username/email and password, **Then** they are authenticated and receive a valid session token.
2. **Given** a user attempting login, **When** they submit incorrect credentials, **Then** the system displays a generic authentication error (without revealing which field was wrong).
3. **Given** a user who has failed login 5 times consecutively, **When** they attempt to login again, **Then** their account is temporarily locked to prevent brute force attacks.
4. **Given** a user checking "Remember Me", **When** they successfully login, **Then** their session persists for 30 days.

---

### User Story 3 - Token-Based Authentication (Priority: P2)

An authenticated user makes requests to protected API endpoints using their authentication token.

**Why this priority**: Once users can register and login, they need token-based authentication to access protected resources. This enables stateless API security.

**Independent Test**: Can be fully tested by making API requests with valid/invalid tokens and verifying appropriate access control.

**Acceptance Scenarios**:

1. **Given** an authenticated user with a valid token, **When** they request a protected resource, **Then** the request succeeds with the expected data.
2. **Given** a user with an expired token, **When** they request a protected resource, **Then** the request is rejected with an appropriate error indicating authentication is required.
3. **Given** a user with a tampered token, **When** they request a protected resource, **Then** the request is rejected as unauthorized.

---

### User Story 4 - User Logout (Priority: P2)

An authenticated user wants to end their session securely.

**Why this priority**: Logout is essential for security, allowing users to terminate their sessions especially on shared devices.

**Independent Test**: Can be fully tested by logging out and verifying the previous token is invalidated.

**Acceptance Scenarios**:

1. **Given** an authenticated user, **When** they request to logout, **Then** their current session token is invalidated.
2. **Given** a user who has logged out, **When** they attempt to use their old token, **Then** the request is rejected as unauthorized.

---

### User Story 5 - Role-Based Access Control (Priority: P3)

System administrators need to restrict certain API endpoints to users with specific roles.

**Why this priority**: After core authentication is working, role-based access enables fine-grained authorization for different user types.

**Independent Test**: Can be fully tested by accessing role-restricted endpoints with users of different roles and verifying appropriate access/denial.

**Acceptance Scenarios**:

1. **Given** a user with ADMIN role, **When** they access admin-only endpoints, **Then** the request succeeds.
2. **Given** a user without ADMIN role, **When** they attempt to access admin-only endpoints, **Then** the request is rejected with a forbidden error.

---

### Edge Cases

- What happens when the database is unavailable during registration/login?
- How does the system handle concurrent login attempts from the same account?
- What happens when a user attempts to register with special characters in username? → Registration is rejected with validation error (only alphanumeric + underscore allowed)
- How does the system behave when token signing key is rotated?
- What happens when a user's role is changed while they have an active session?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to register with username (alphanumeric + underscore, 3-32 characters), email, and password
- **FR-002**: System MUST validate email format and uniqueness during registration
- **FR-003**: System MUST enforce password policy (minimum length, complexity requirements)
- **FR-004**: System MUST hash passwords using a secure algorithm before storage
- **FR-005**: System MUST allow users to login with username/email and password
- **FR-006**: System MUST issue secure tokens upon successful authentication
- **FR-007**: System MUST validate tokens on each protected API request
- **FR-008**: System MUST support token expiration (1 hour default) and refresh mechanisms
- **FR-009**: System MUST invalidate tokens upon user logout
- **FR-010**: System MUST implement account lockout after 5 consecutive failed login attempts, with automatic unlock after 15 minutes
- **FR-011**: System MUST support "Remember Me" functionality with 30-day extended sessions
- **FR-012**: System MUST support role-based access control (at minimum: USER, ADMIN roles)
- **FR-013**: System MUST log all security-related events (login attempts, registration, logout)
- **FR-014**: System MUST return appropriate error messages without leaking security-sensitive information

### Key Entities

- **User**: Represents a system user with credentials (username, email, password hash), profile information, account status (active, locked), and assigned roles
- **Role**: Represents a permission level (USER, ADMIN) that can be assigned to users to control access to resources
- **Session/Token**: Represents an authenticated session with expiration, refresh capability, and device/client information
- **LoginAttempt**: Tracks authentication attempts for security monitoring and account lockout logic

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can complete account registration in under 30 seconds
- **SC-002**: Users can complete login in under 10 seconds
- **SC-003**: System handles 1000 concurrent authentication requests without degradation
- **SC-004**: 99% of valid authentication requests succeed on first attempt
- **SC-005**: Account lockout correctly triggers after 5 consecutive failed attempts
- **SC-006**: Token validation adds less than 100ms overhead to API requests
- **SC-007**: All security events are logged within 1 second of occurrence

## Project Structure Requirements

### Architecture

The project MUST follow Domain-Driven Design (DDD) principles with the following layer separation:

- **Interface Layer**: HTTP controllers, request/response DTOs, API documentation
- **Application Layer**: Application services coordinating domain logic, use case orchestration
- **Domain Layer**: Core business entities, domain services, repository interfaces
- **Infrastructure Layer**: Repository implementations, security implementations (token generation), external integrations
- **Bootstrap Layer**: Application configuration, security configuration, startup logic
- **Common Layer**: Shared utilities, cross-cutting concerns

### Module Structure

The project should be organized as a multi-module structure:
- `interface-http`: REST controllers and HTTP handling
- `application`: Application services
- `domain-api`: Domain service interfaces
- `domain-model`: Domain entities and value objects
- `domain-impl`: Domain service implementations
- `infrastructure/repository/mysql-impl`: Database repository implementations
- `infrastructure/security/jwt-impl`: JWT token provider implementation
- `infrastructure/cache/redis-impl`: Redis caching implementation (optional)
- `bootstrap`: Application startup and configuration
- `common`: Shared utilities

## Clarifications

### Session 2025-12-25

- Q: How long should account lockout last after 5 failed attempts? → A: 15 minutes auto-unlock (industry standard)
- Q: What are the token expiration times? → A: 1 hour standard / 30 days remember-me
- Q: What are the username constraints? → A: Alphanumeric + underscore, 3-32 characters

## Assumptions

- The target technology stack is Java 21 with Spring Boot 3.x (matching op-stack-service)
- MySQL will be used as the primary database
- JWT will be used for token-based authentication
- BCrypt will be used for password hashing
- The project will use Maven for build management
- MyBatis-Plus will be used as the ORM framework
- Standard RESTful API conventions will be followed
- The system operates as a single service (not microservices) initially
