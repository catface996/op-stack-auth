# OP-Stack Auth Service

A DDD (Domain-Driven Design) based authentication and authorization service providing user registration, login, JWT token authentication, and RBAC access control.

## Architecture

![OP-Stack Auth Architecture](doc/images/op-stack-auth-architecture.png)

## Tech Stack

| Technology | Version | Description |
|------------|---------|-------------|
| Java | 21 | LTS Version |
| Spring Boot | 3.4.1 | Base Framework |
| Spring Security | 6.x | Security Framework |
| MyBatis-Plus | 3.5.7 | ORM Framework |
| JJWT | 0.12.6 | JWT Token Processing |
| MySQL | 8.0 | Database |
| Druid | 1.2.20 | Connection Pool |

## Project Structure

```
op-stack-auth/
├── common/                          # Common module
│   └── src/main/java/.../common/
│       ├── exception/               # Exception definitions
│       └── result/                  # Unified response
├── domain/                          # Domain layer
│   ├── domain-model/                # Domain models
│   │   └── src/main/java/.../model/
│   │       ├── user/                # User aggregate root
│   │       ├── role/                # Role entity
│   │       └── session/             # Token value object
│   ├── domain-api/                  # Domain service interfaces
│   ├── domain-impl/                 # Domain service implementations
│   ├── repository-api/              # Repository interfaces
│   └── security-api/                # Security interfaces
├── application/                     # Application layer
│   ├── application-api/             # Application service interfaces
│   └── application-impl/            # Application service implementations
├── infrastructure/                  # Infrastructure layer
│   ├── repository/
│   │   └── mysql-impl/              # MySQL repository implementation
│   └── security/
│       └── jwt-impl/                # JWT token implementation
├── interface/                       # Interface layer
│   └── interface-http/              # REST API controllers
└── bootstrap/                       # Bootstrap module
    └── src/main/
        ├── java/.../bootstrap/
        │   ├── AuthApplication.java # Application entry
        │   └── config/              # Configuration classes
        └── resources/
            ├── application.yml      # Configuration files
            └── db/migration/        # Database scripts
```

## Features

### Authentication
- **User Registration**: Username, email, password registration with password policy validation
- **User Login**: Username/email login with Remember-Me support
- **Token Refresh**: Seamless token refresh
- **User Logout**: Token blacklist mechanism

### Security
- **JWT Token**: Stateless authentication with 1 hour / 30 days expiration
- **Password Encryption**: BCrypt hashing
- **Account Lockout**: Lock after 5 failed attempts for 15 minutes, auto-unlock
- **Login Audit**: Track login attempts (IP, User-Agent)

### Access Control
- **RBAC**: Role-based access control
- **Predefined Roles**: USER (standard user), ADMIN (administrator)
- **Method-level Security**: @PreAuthorize annotation support

## Quick Start

### Prerequisites
- JDK 21+
- Maven 3.8+
- MySQL 8.0+

### Database Setup

```sql
-- Create database
CREATE DATABASE auth_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Run initialization script
-- Location: bootstrap/src/main/resources/db/migration/V1__init_schema.sql
```

### Configuration

Edit `bootstrap/src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### Build & Run

```bash
# Build project
mvn clean package -DskipTests

# Run service
mvn spring-boot:run -pl bootstrap

# Or run JAR directly
java -jar bootstrap/target/bootstrap-1.0.0-SNAPSHOT.jar
```

Service runs at `http://localhost:9090` by default.

## API Endpoints

### Authentication APIs

| Method | Path | Description | Auth Required |
|--------|------|-------------|---------------|
| POST | `/api/v1/auth/register` | User registration | No |
| POST | `/api/v1/auth/login` | User login | No |
| POST | `/api/v1/auth/logout` | User logout | Yes |
| POST | `/api/v1/auth/refresh` | Refresh token | No |
| GET | `/api/v1/auth/me` | Get current user | Yes |

### Admin APIs

| Method | Path | Description | Role |
|--------|------|-------------|------|
| GET | `/api/v1/admin/dashboard` | Admin dashboard | ADMIN |
| GET | `/api/v1/admin/stats` | System statistics | ADMIN |

### API Examples

#### Register User
```bash
curl -X POST http://localhost:9090/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test1234"
  }'
```

#### User Login
```bash
curl -X POST http://localhost:9090/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "testuser",
    "password": "Test1234",
    "rememberMe": false
  }'
```

#### Get Current User
```bash
curl -X GET http://localhost:9090/api/v1/auth/me \
  -H "Authorization: Bearer <your_token>"
```

## Configuration Reference

### JWT Configuration

```yaml
auth:
  jwt:
    secret: your-256-bit-secret-key         # JWT signing key
    expiration-seconds: 3600                 # Token expiration (1 hour)
    remember-me-expiration-seconds: 2592000  # Remember-Me expiration (30 days)
    issuer: op-stack-auth                    # Token issuer
  security:
    max-failed-attempts: 5                   # Max failed login attempts
    lockout-duration-minutes: 15             # Lockout duration (minutes)
```

### Database Tables

| Table | Description |
|-------|-------------|
| users | User information |
| roles | Role definitions |
| user_roles | User-role associations |
| login_attempts | Login attempt records |

## Error Codes

| Code | Description |
|------|-------------|
| AUTH_001 | Invalid credentials |
| AUTH_002 | Account locked |
| AUTH_003 | Token expired |
| AUTH_004 | Invalid token |
| AUTH_005 | Access denied |
| AUTH_006 | Insufficient permissions |
| AUTH_007 | Username already exists |
| AUTH_008 | Email already exists |
| AUTH_009 | Account inactive |
| AUTH_010 | Authentication required |

## Development Guide

### Adding New API Endpoints

1. Define Command/Query in `application-api`
2. Implement application service in `application-impl`
3. Create Controller and DTOs in `interface-http`
4. Configure access rules in `SecurityConfig`

### Adding New Domain Services

1. Define interface in `domain-api`
2. Implement service in `domain-impl`
3. Define repository interface in `repository-api` (if needed)
4. Implement repository in `mysql-impl`

## Monitoring Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Health check |
| `/actuator/info` | Application info |
| `/actuator/metrics` | Metrics data |

## License

MIT License
