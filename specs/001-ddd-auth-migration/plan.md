# Implementation Plan: DDD Auth Migration

**Branch**: `001-ddd-auth-migration` | **Date**: 2025-12-25 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-ddd-auth-migration/spec.md`

## Summary

Migrate authentication functionality (registration, login, logout, token-based auth, RBAC) from op-stack-service to a new standalone op-stack-auth service, replicating the DDD multi-module Maven project structure. The system will use JWT for stateless authentication with 1-hour standard tokens and 30-day remember-me sessions, BCrypt password hashing, and 15-minute account lockout after 5 failed attempts.

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Spring Boot 3.4.1, Spring Security, MyBatis-Plus 3.5.7, JJWT 0.12.6
**Storage**: MySQL (via Druid connection pool)
**Testing**: JUnit 5, Spring Boot Test, Mockito
**Target Platform**: Linux server (JVM-based microservice)
**Project Type**: Multi-module Maven DDD architecture
**Performance Goals**: 1000 concurrent auth requests, <100ms token validation overhead
**Constraints**: <200ms p95 for auth endpoints, stateless JWT authentication
**Scale/Scope**: Single auth service supporting multiple downstream services

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The constitution template has not been customized for this project. Using default governance:

- **Library-First**: N/A - This is a service, not a library
- **Test-First**: Will implement unit and integration tests for all auth flows
- **Observability**: Will include structured logging for security events (FR-013)
- **Simplicity**: Following established patterns from op-stack-service

**Gate Status**: PASS (no blocking violations)

## Project Structure

### Documentation (this feature)

```text
specs/001-ddd-auth-migration/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (OpenAPI specs)
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
op-stack-auth/
├── pom.xml                          # Parent POM with dependency management
├── common/                          # Shared utilities
│   ├── pom.xml
│   └── src/main/java/com/catface996/auth/common/
│       ├── exception/               # Custom exceptions
│       ├── result/                  # API response wrappers
│       └── util/                    # Utilities
│
├── domain/                          # Domain layer (business logic)
│   ├── pom.xml
│   ├── domain-model/                # Domain entities & value objects
│   │   ├── pom.xml
│   │   └── src/main/java/com/catface996/auth/domain/model/
│   │       ├── user/                # User aggregate
│   │       ├── role/                # Role entity
│   │       └── session/             # Session/Token entity
│   ├── domain-api/                  # Domain service interfaces
│   │   ├── pom.xml
│   │   └── src/main/java/com/catface996/auth/domain/api/
│   │       └── service/
│   ├── domain-impl/                 # Domain service implementations
│   │   ├── pom.xml
│   │   └── src/main/java/com/catface996/auth/domain/impl/
│   │       └── service/
│   ├── repository-api/              # Repository interfaces
│   │   ├── pom.xml
│   │   └── src/main/java/com/catface996/auth/domain/repository/
│   └── security-api/                # Security provider interfaces
│       ├── pom.xml
│       └── src/main/java/com/catface996/auth/domain/security/
│
├── application/                     # Application layer (use cases)
│   ├── pom.xml
│   ├── application-api/             # Application service interfaces
│   │   ├── pom.xml
│   │   └── src/main/java/com/catface996/auth/application/api/
│   │       └── service/
│   └── application-impl/            # Application service implementations
│       ├── pom.xml
│       └── src/main/java/com/catface996/auth/application/impl/
│           └── service/
│
├── infrastructure/                  # Infrastructure layer
│   ├── pom.xml
│   ├── repository/
│   │   ├── pom.xml
│   │   └── mysql-impl/              # MySQL repository implementations
│   │       ├── pom.xml
│   │       └── src/main/java/com/catface996/auth/infrastructure/repository/
│   │           ├── mapper/          # MyBatis-Plus mappers
│   │           ├── entity/          # Database entities
│   │           └── impl/            # Repository implementations
│   └── security/
│       ├── pom.xml
│       └── jwt-impl/                # JWT token provider
│           ├── pom.xml
│           └── src/main/java/com/catface996/auth/infrastructure/security/
│               └── jwt/
│
├── interface/                       # Interface layer (controllers)
│   ├── pom.xml
│   └── interface-http/              # REST API controllers
│       ├── pom.xml
│       └── src/main/java/com/catface996/auth/interfaces/http/
│           ├── controller/          # REST controllers
│           ├── dto/                 # Request/Response DTOs
│           └── config/              # Web configuration
│
└── bootstrap/                       # Application startup
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/catface996/auth/bootstrap/
        │   │   ├── AuthApplication.java
        │   │   └── config/          # Security, DB configs
        │   └── resources/
        │       ├── application.yml
        │       └── application-local.yml
        └── test/                    # Integration tests
            └── java/
```

**Structure Decision**: DDD multi-module Maven architecture matching op-stack-service. This separates concerns cleanly:
- Domain layer owns business logic (user, auth rules)
- Application layer orchestrates use cases
- Infrastructure provides technical implementations (MySQL, JWT)
- Interface handles HTTP translation
- Bootstrap wires everything together

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Multi-module structure (6 top-level modules) | Matches reference architecture, enforces layer boundaries | Single module would mix concerns, harder to enforce DDD layers |
| Repository pattern | Decouples domain from persistence, enables testing | Direct DB access couples domain to MySQL specifics |

## Next Steps

- Phase 0: Generate research.md (resolve any remaining unknowns)
- Phase 1: Generate data-model.md, contracts/, quickstart.md
