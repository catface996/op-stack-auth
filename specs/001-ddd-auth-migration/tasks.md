# Tasks: DDD Auth Migration

**Input**: Design documents from `/specs/001-ddd-auth-migration/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Not explicitly requested in spec. Tests omitted from task list.

**Organization**: Tasks grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

This is a **multi-module Maven DDD project**:
- `common/` - Shared utilities
- `domain/domain-model/` - Domain entities
- `domain/domain-api/` - Domain service interfaces
- `domain/domain-impl/` - Domain service implementations
- `domain/repository-api/` - Repository interfaces
- `domain/security-api/` - Security provider interfaces
- `application/application-api/` - Application service interfaces
- `application/application-impl/` - Application service implementations
- `infrastructure/repository/mysql-impl/` - MySQL implementations
- `infrastructure/security/jwt-impl/` - JWT implementations
- `interface/interface-http/` - REST controllers
- `bootstrap/` - Application startup

---

## Phase 1: Setup (Project Initialization)

**Purpose**: Create multi-module Maven project structure matching op-stack-service

- [x] T001 Create parent pom.xml with Java 21, Spring Boot 3.4.1, dependency management in pom.xml
- [x] T002 [P] Create common module pom.xml in common/pom.xml
- [x] T003 [P] Create domain parent pom.xml in domain/pom.xml
- [x] T004 [P] Create domain-model module pom.xml in domain/domain-model/pom.xml
- [x] T005 [P] Create domain-api module pom.xml in domain/domain-api/pom.xml
- [x] T006 [P] Create domain-impl module pom.xml in domain/domain-impl/pom.xml
- [x] T007 [P] Create repository-api module pom.xml in domain/repository-api/pom.xml
- [x] T008 [P] Create security-api module pom.xml in domain/security-api/pom.xml
- [x] T009 [P] Create application parent pom.xml in application/pom.xml
- [x] T010 [P] Create application-api module pom.xml in application/application-api/pom.xml
- [x] T011 [P] Create application-impl module pom.xml in application/application-impl/pom.xml
- [x] T012 [P] Create infrastructure parent pom.xml in infrastructure/pom.xml
- [x] T013 [P] Create repository parent pom.xml in infrastructure/repository/pom.xml
- [x] T014 [P] Create mysql-impl module pom.xml in infrastructure/repository/mysql-impl/pom.xml
- [x] T015 [P] Create security parent pom.xml in infrastructure/security/pom.xml
- [x] T016 [P] Create jwt-impl module pom.xml in infrastructure/security/jwt-impl/pom.xml
- [x] T017 [P] Create interface parent pom.xml in interface/pom.xml
- [x] T018 [P] Create interface-http module pom.xml in interface/interface-http/pom.xml
- [x] T019 [P] Create bootstrap module pom.xml in bootstrap/pom.xml
- [x] T020 Verify Maven project structure compiles with mvn clean compile

**Checkpoint**: Project structure created, Maven build passes

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Common Module Setup

- [ ] T021 [P] Create Result wrapper class in common/src/main/java/com/catface996/auth/common/result/Result.java
- [ ] T022 [P] Create ErrorCode enum in common/src/main/java/com/catface996/auth/common/result/ErrorCode.java
- [ ] T023 [P] Create BusinessException class in common/src/main/java/com/catface996/auth/common/exception/BusinessException.java
- [ ] T024 [P] Create AuthException class in common/src/main/java/com/catface996/auth/common/exception/AuthException.java

### Domain Model - Core Entities (Used by Multiple Stories)

- [ ] T025 [P] Create UserStatus enum in domain/domain-model/src/main/java/com/catface996/auth/domain/model/user/UserStatus.java
- [ ] T026 [P] Create User domain entity in domain/domain-model/src/main/java/com/catface996/auth/domain/model/user/User.java
- [ ] T027 [P] Create Role domain entity in domain/domain-model/src/main/java/com/catface996/auth/domain/model/role/Role.java
- [ ] T028 [P] Create TokenClaims value object in domain/domain-model/src/main/java/com/catface996/auth/domain/model/session/TokenClaims.java
- [ ] T029 [P] Create LoginAttempt domain entity in domain/domain-model/src/main/java/com/catface996/auth/domain/model/user/LoginAttempt.java

### Repository API - Interfaces

- [ ] T030 [P] Create UserRepository interface in domain/repository-api/src/main/java/com/catface996/auth/domain/repository/UserRepository.java
- [ ] T031 [P] Create RoleRepository interface in domain/repository-api/src/main/java/com/catface996/auth/domain/repository/RoleRepository.java
- [ ] T032 [P] Create LoginAttemptRepository interface in domain/repository-api/src/main/java/com/catface996/auth/domain/repository/LoginAttemptRepository.java

### Security API - Token Provider Interface

- [ ] T033 Create TokenProvider interface in domain/security-api/src/main/java/com/catface996/auth/domain/security/TokenProvider.java

### Infrastructure - Database Layer

- [ ] T034 Create database schema SQL in bootstrap/src/main/resources/db/migration/V1__init_schema.sql
- [ ] T035 [P] Create UserDO database entity in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/entity/UserDO.java
- [ ] T036 [P] Create RoleDO database entity in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/entity/RoleDO.java
- [ ] T037 [P] Create UserRoleDO database entity in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/entity/UserRoleDO.java
- [ ] T038 [P] Create LoginAttemptDO database entity in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/entity/LoginAttemptDO.java
- [ ] T039 [P] Create UserMapper interface in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/mapper/UserMapper.java
- [ ] T040 [P] Create RoleMapper interface in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/mapper/RoleMapper.java
- [ ] T041 [P] Create UserRoleMapper interface in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/mapper/UserRoleMapper.java
- [ ] T042 [P] Create LoginAttemptMapper interface in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/mapper/LoginAttemptMapper.java
- [ ] T043 Implement UserRepositoryImpl in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/impl/UserRepositoryImpl.java
- [ ] T044 [P] Implement RoleRepositoryImpl in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/impl/RoleRepositoryImpl.java
- [ ] T045 [P] Implement LoginAttemptRepositoryImpl in infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/impl/LoginAttemptRepositoryImpl.java

### Infrastructure - JWT Implementation

- [ ] T046 Implement JwtTokenProvider in infrastructure/security/jwt-impl/src/main/java/com/catface996/auth/infrastructure/security/jwt/JwtTokenProvider.java
- [ ] T047 Create JwtProperties configuration in infrastructure/security/jwt-impl/src/main/java/com/catface996/auth/infrastructure/security/jwt/JwtProperties.java

### Bootstrap - Application Setup

- [ ] T048 Create AuthApplication main class in bootstrap/src/main/java/com/catface996/auth/bootstrap/AuthApplication.java
- [ ] T049 Create application.yml configuration in bootstrap/src/main/resources/application.yml
- [ ] T050 [P] Create application-local.yml for development in bootstrap/src/main/resources/application-local.yml
- [ ] T051 Create SecurityConfig for Spring Security in bootstrap/src/main/java/com/catface996/auth/bootstrap/config/SecurityConfig.java
- [ ] T052 Create GlobalExceptionHandler in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/config/GlobalExceptionHandler.java

### Interface - Common DTOs

- [ ] T053 [P] Create ErrorResponse DTO in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/dto/ErrorResponse.java
- [ ] T054 [P] Create SuccessResponse DTO in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/dto/SuccessResponse.java
- [ ] T055 [P] Create UserProfileResponse DTO in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/dto/UserProfileResponse.java

- [ ] T056 Verify foundational layer builds and application starts with mvn spring-boot:run

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - User Registration (Priority: P1) 🎯 MVP

**Goal**: Allow new users to create accounts with username, email, and password

**Independent Test**: POST /api/v1/auth/register with valid data returns 201 and user is created

### Domain Layer for US1

- [ ] T057 [US1] Create PasswordEncoder interface in domain/security-api/src/main/java/com/catface996/auth/domain/security/PasswordEncoder.java
- [ ] T058 [US1] Implement BCryptPasswordEncoder in infrastructure/security/jwt-impl/src/main/java/com/catface996/auth/infrastructure/security/jwt/BCryptPasswordEncoderImpl.java
- [ ] T059 [US1] Create UserDomainService interface in domain/domain-api/src/main/java/com/catface996/auth/domain/api/service/UserDomainService.java
- [ ] T060 [US1] Implement UserDomainServiceImpl with registration logic in domain/domain-impl/src/main/java/com/catface996/auth/domain/impl/service/UserDomainServiceImpl.java

### Application Layer for US1

- [ ] T061 [US1] Create RegisterCommand in application/application-api/src/main/java/com/catface996/auth/application/api/command/RegisterCommand.java
- [ ] T062 [US1] Create AuthApplicationService interface in application/application-api/src/main/java/com/catface996/auth/application/api/service/AuthApplicationService.java
- [ ] T063 [US1] Implement AuthApplicationServiceImpl.register() in application/application-impl/src/main/java/com/catface996/auth/application/impl/service/AuthApplicationServiceImpl.java

### Interface Layer for US1

- [ ] T064 [P] [US1] Create RegisterRequest DTO in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/dto/RegisterRequest.java
- [ ] T065 [P] [US1] Create RegisterResponse DTO in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/dto/RegisterResponse.java
- [ ] T066 [US1] Create AuthController with POST /api/v1/auth/register in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/controller/AuthController.java
- [ ] T067 [US1] Add input validation for RegisterRequest (username pattern, email format, password policy)

**Checkpoint**: User Story 1 (Registration) is fully functional and testable independently

---

## Phase 4: User Story 2 - User Login (Priority: P1)

**Goal**: Allow registered users to authenticate with username/email and password

**Independent Test**: POST /api/v1/auth/login with valid credentials returns 200 with JWT token

### Domain Layer for US2

- [ ] T068 [US2] Create AuthDomainService interface in domain/domain-api/src/main/java/com/catface996/auth/domain/api/service/AuthDomainService.java
- [ ] T069 [US2] Implement AuthDomainServiceImpl with authenticate, lockout logic in domain/domain-impl/src/main/java/com/catface996/auth/domain/impl/service/AuthDomainServiceImpl.java

### Application Layer for US2

- [ ] T070 [US2] Create LoginCommand in application/application-api/src/main/java/com/catface996/auth/application/api/command/LoginCommand.java
- [ ] T071 [US2] Create LoginResult in application/application-api/src/main/java/com/catface996/auth/application/api/result/LoginResult.java
- [ ] T072 [US2] Add login() method to AuthApplicationService interface
- [ ] T073 [US2] Implement AuthApplicationServiceImpl.login() with token generation

### Interface Layer for US2

- [ ] T074 [P] [US2] Create LoginRequest DTO in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/dto/LoginRequest.java
- [ ] T075 [P] [US2] Create LoginResponse DTO in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/dto/LoginResponse.java
- [ ] T076 [US2] Add POST /api/v1/auth/login endpoint to AuthController
- [ ] T077 [US2] Implement account lockout response (423 status) for locked accounts
- [ ] T078 [US2] Implement Remember Me functionality (30-day token expiration)

**Checkpoint**: User Story 2 (Login) is fully functional and testable independently

---

## Phase 5: User Story 3 - Token-Based Authentication (Priority: P2)

**Goal**: Validate JWT tokens on protected API requests

**Independent Test**: GET /api/v1/auth/me with valid token returns user profile; invalid token returns 401

### Infrastructure Layer for US3

- [ ] T079 [US3] Create TokenBlacklist interface in domain/security-api/src/main/java/com/catface996/auth/domain/security/TokenBlacklist.java
- [ ] T080 [US3] Implement InMemoryTokenBlacklist in infrastructure/security/jwt-impl/src/main/java/com/catface996/auth/infrastructure/security/jwt/InMemoryTokenBlacklist.java
- [ ] T081 [US3] Create JwtAuthenticationFilter in bootstrap/src/main/java/com/catface996/auth/bootstrap/config/JwtAuthenticationFilter.java
- [ ] T082 [US3] Register JwtAuthenticationFilter in SecurityConfig

### Application Layer for US3

- [ ] T083 [US3] Create ValidateTokenCommand in application/application-api/src/main/java/com/catface996/auth/application/api/command/ValidateTokenCommand.java
- [ ] T084 [US3] Add validateToken() method to AuthApplicationService interface
- [ ] T085 [US3] Implement AuthApplicationServiceImpl.validateToken()
- [ ] T086 [US3] Add getCurrentUser() method to AuthApplicationService

### Interface Layer for US3

- [ ] T087 [P] [US3] Create ValidateTokenRequest DTO in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/dto/ValidateTokenRequest.java
- [ ] T088 [P] [US3] Create TokenValidationResponse DTO in interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/dto/TokenValidationResponse.java
- [ ] T089 [US3] Add GET /api/v1/auth/me endpoint to AuthController
- [ ] T090 [US3] Add POST /api/v1/auth/validate endpoint to AuthController
- [ ] T091 [US3] Add POST /api/v1/auth/refresh endpoint to AuthController

**Checkpoint**: User Story 3 (Token Auth) is fully functional and testable independently

---

## Phase 6: User Story 4 - User Logout (Priority: P2)

**Goal**: Allow users to invalidate their current token on logout

**Independent Test**: POST /api/v1/auth/logout invalidates token; subsequent requests with that token return 401

### Application Layer for US4

- [ ] T092 [US4] Add logout() method to AuthApplicationService interface
- [ ] T093 [US4] Implement AuthApplicationServiceImpl.logout() with token blacklisting

### Interface Layer for US4

- [ ] T094 [US4] Add POST /api/v1/auth/logout endpoint to AuthController
- [ ] T095 [US4] Verify blacklisted token is rejected on subsequent requests

**Checkpoint**: User Story 4 (Logout) is fully functional and testable independently

---

## Phase 7: User Story 5 - Role-Based Access Control (Priority: P3)

**Goal**: Restrict certain endpoints to users with specific roles (ADMIN)

**Independent Test**: Admin-only endpoint returns 200 for ADMIN user, 403 for USER user

### Domain Layer for US5

- [ ] T096 [US5] Add role checking methods to AuthDomainService interface
- [ ] T097 [US5] Implement role checking in AuthDomainServiceImpl

### Bootstrap Layer for US5

- [ ] T098 [US5] Update SecurityConfig with role-based URL patterns
- [ ] T099 [US5] Add @PreAuthorize annotations support in SecurityConfig
- [ ] T100 [US5] Create custom AccessDeniedHandler in bootstrap/src/main/java/com/catface996/auth/bootstrap/config/CustomAccessDeniedHandler.java

### Interface Layer for US5

- [ ] T101 [US5] Create sample admin-only endpoint for testing RBAC in AuthController
- [ ] T102 [US5] Verify USER role cannot access admin endpoint (403 response)

**Checkpoint**: User Story 5 (RBAC) is fully functional and testable independently

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T103 [P] Add structured logging for security events (FR-013) using Logback/SLF4J
- [ ] T104 [P] Add request traceId to all responses for debugging
- [ ] T105 [P] Configure Swagger/OpenAPI documentation in bootstrap
- [ ] T106 [P] Add health check endpoint (/actuator/health)
- [ ] T107 Code review and cleanup across all modules
- [ ] T108 Run quickstart.md validation - verify all curl commands work
- [ ] T109 Final Maven build verification: mvn clean package

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-7)**: All depend on Foundational phase completion
  - US1 and US2 are both P1 - can run in parallel after Phase 2
  - US3 and US4 are both P2 - can run in parallel after Phase 2
  - US5 is P3 - can run after Phase 2 (no dependency on other stories)
- **Polish (Phase 8)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (Registration)**: No dependencies on other stories
- **User Story 2 (Login)**: No dependencies on other stories (uses same User entity from Phase 2)
- **User Story 3 (Token Auth)**: No dependencies on other stories (tokens from login are tested independently)
- **User Story 4 (Logout)**: Depends on US3 (token blacklist infrastructure)
- **User Story 5 (RBAC)**: No dependencies on other stories (roles from Phase 2)

### Within Each User Story

- Domain layer before Application layer
- Application layer before Interface layer
- DTOs (marked [P]) can be created in parallel
- Core implementation before integration

### Parallel Opportunities

Within Phase 2 (Foundational):
- All common module tasks (T021-T024) can run in parallel
- All domain model tasks (T025-T029) can run in parallel
- All repository interfaces (T030-T032) can run in parallel
- All database entities (T035-T038) can run in parallel
- All mappers (T039-T042) can run in parallel

Within User Stories:
- DTOs marked [P] can be created in parallel within each story
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: Foundational Phase

```bash
# Launch all common module tasks together:
Task: "Create Result wrapper class in common/.../Result.java"
Task: "Create ErrorCode enum in common/.../ErrorCode.java"
Task: "Create BusinessException class in common/.../BusinessException.java"
Task: "Create AuthException class in common/.../AuthException.java"

# Launch all domain model tasks together:
Task: "Create UserStatus enum in domain/domain-model/.../UserStatus.java"
Task: "Create User domain entity in domain/domain-model/.../User.java"
Task: "Create Role domain entity in domain/domain-model/.../Role.java"
Task: "Create TokenClaims value object in domain/domain-model/.../TokenClaims.java"
Task: "Create LoginAttempt domain entity in domain/domain-model/.../LoginAttempt.java"
```

---

## Implementation Strategy

### MVP First (User Stories 1 & 2)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (Registration)
4. Complete Phase 4: User Story 2 (Login)
5. **STOP and VALIDATE**: Test both stories independently
6. Deploy/demo if ready - users can register and login!

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test → Deploy (users can register)
3. Add User Story 2 → Test → Deploy (users can login)
4. Add User Story 3 + 4 → Test → Deploy (full token auth + logout)
5. Add User Story 5 → Test → Deploy (RBAC enabled)
6. Polish → Final release

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (Registration)
   - Developer B: User Story 2 (Login)
3. After US1 & US2 complete:
   - Developer A: User Story 3 (Token Auth)
   - Developer B: User Story 4 (Logout)
4. Developer C: User Story 5 (RBAC) - can start after Foundational

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Reference op-stack-service code patterns when implementing
