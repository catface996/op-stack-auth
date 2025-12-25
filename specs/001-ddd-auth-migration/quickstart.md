# Quickstart: DDD Auth Migration

**Feature**: 001-ddd-auth-migration
**Date**: 2025-12-25

## Prerequisites

- Java 21 (JDK)
- Maven 3.8+
- MySQL 8.0+
- IDE (IntelliJ IDEA recommended)

## Project Setup

### 1. Clone and Initialize

```bash
# Navigate to project directory
cd /Users/catface/Documents/code/GitHub/op-stack/op-stack-auth

# Verify you're on the correct branch
git branch  # Should show: * 001-ddd-auth-migration
```

### 2. Create Project Structure

The project follows DDD multi-module architecture. Create the following directory structure:

```bash
# Create root directories
mkdir -p common/src/main/java/com/catface996/auth/common/{exception,result,util}
mkdir -p domain/domain-model/src/main/java/com/catface996/auth/domain/model/{user,role,session}
mkdir -p domain/domain-api/src/main/java/com/catface996/auth/domain/api/service
mkdir -p domain/domain-impl/src/main/java/com/catface996/auth/domain/impl/service
mkdir -p domain/repository-api/src/main/java/com/catface996/auth/domain/repository
mkdir -p domain/security-api/src/main/java/com/catface996/auth/domain/security
mkdir -p application/application-api/src/main/java/com/catface996/auth/application/api/service
mkdir -p application/application-impl/src/main/java/com/catface996/auth/application/impl/service
mkdir -p infrastructure/repository/mysql-impl/src/main/java/com/catface996/auth/infrastructure/repository/{mapper,entity,impl}
mkdir -p infrastructure/security/jwt-impl/src/main/java/com/catface996/auth/infrastructure/security/jwt
mkdir -p interface/interface-http/src/main/java/com/catface996/auth/interfaces/http/{controller,dto,config}
mkdir -p bootstrap/src/main/java/com/catface996/auth/bootstrap/config
mkdir -p bootstrap/src/main/resources
mkdir -p bootstrap/src/test/java
```

### 3. Database Setup

```sql
-- Create database
CREATE DATABASE op_stack_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (adjust credentials as needed)
CREATE USER 'auth_user'@'localhost' IDENTIFIED BY 'auth_password';
GRANT ALL PRIVILEGES ON op_stack_auth.* TO 'auth_user'@'localhost';
FLUSH PRIVILEGES;

-- Switch to database
USE op_stack_auth;

-- Run schema from data-model.md (see Database Schema section)
```

### 4. Configuration

Create `bootstrap/src/main/resources/application.yml`:

```yaml
server:
  port: 8081

spring:
  application:
    name: op-stack-auth
  datasource:
    url: jdbc:mysql://localhost:3306/op_stack_auth?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
    username: auth_user
    password: auth_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.catface996.auth.infrastructure.repository.entity
  configuration:
    map-underscore-to-camel-case: true

# JWT Configuration
auth:
  jwt:
    secret: your-256-bit-secret-key-here-change-in-production
    expiration: 3600000        # 1 hour in milliseconds
    remember-me-expiration: 2592000000  # 30 days in milliseconds

# Security
security:
  lockout:
    max-attempts: 5
    duration-minutes: 15

logging:
  level:
    com.catface996.auth: DEBUG
```

### 5. Build and Run

```bash
# Build the project
mvn clean install -DskipTests

# Run the application
cd bootstrap
mvn spring-boot:run

# Or run the JAR directly
java -jar target/bootstrap-1.0.0-SNAPSHOT.jar
```

### 6. Verify Installation

```bash
# Health check
curl http://localhost:8081/actuator/health

# Register a test user
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "TestPass123"
  }'

# Login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "testuser",
    "password": "TestPass123"
  }'
```

## Development Workflow

### Layer Dependencies

```
interface → application → domain ← infrastructure
                ↓
             common
```

**Dependency Rules:**
- `domain-model`: No dependencies (pure domain objects)
- `domain-api`: Depends on `domain-model`
- `domain-impl`: Depends on `domain-api`, `repository-api`, `security-api`
- `application-api`: Depends on `domain-model`
- `application-impl`: Depends on `application-api`, `domain-api`
- `infrastructure/*`: Depends on respective `*-api` modules
- `interface-http`: Depends on `application-api`
- `bootstrap`: Depends on all impl modules (wires everything)

### Adding a New Feature

1. **Define domain model** in `domain-model`
2. **Define domain service interface** in `domain-api`
3. **Define repository interface** in `repository-api` (if persistence needed)
4. **Implement domain service** in `domain-impl`
5. **Implement repository** in `infrastructure/repository/mysql-impl`
6. **Define application service interface** in `application-api`
7. **Implement application service** in `application-impl`
8. **Create controller and DTOs** in `interface-http`
9. **Wire beans** in `bootstrap/config`

### Running Tests

```bash
# Run all tests
mvn test

# Run specific module tests
mvn test -pl bootstrap

# Run with coverage
mvn test jacoco:report
```

## Common Issues

### Issue: Module not found
**Solution**: Run `mvn clean install` from project root first

### Issue: Database connection failed
**Solution**: Verify MySQL is running and credentials in `application.yml` are correct

### Issue: JWT signature invalid
**Solution**: Ensure `auth.jwt.secret` is at least 256 bits (32+ characters)

## API Reference

See [contracts/auth-api.yaml](./contracts/auth-api.yaml) for full OpenAPI specification.

Quick reference:
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/auth/register` | POST | Register new user |
| `/api/v1/auth/login` | POST | Login and get token |
| `/api/v1/auth/logout` | POST | Invalidate token |
| `/api/v1/auth/refresh` | POST | Refresh token |
| `/api/v1/auth/me` | GET | Get current user profile |
| `/api/v1/auth/validate` | POST | Validate token (service-to-service) |
