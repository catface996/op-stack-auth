# Research: DDD Auth Migration

**Feature**: 001-ddd-auth-migration
**Date**: 2025-12-25

## Overview

This document consolidates research findings for implementing the auth service migration from op-stack-service to op-stack-auth.

---

## 1. JWT Token Implementation Strategy

### Decision
Use JJWT library (io.jsonwebtoken) version 0.12.6 with HS256 signing algorithm.

### Rationale
- JJWT is already in use in op-stack-service, ensuring consistency
- HS256 (HMAC-SHA256) is sufficient for single-service token validation
- Simpler key management than asymmetric algorithms (RS256)
- Good performance for high-volume token operations

### Alternatives Considered
| Alternative | Rejected Because |
|-------------|------------------|
| Spring Security OAuth2 Resource Server | Overkill for simple JWT validation; adds complexity |
| RS256 asymmetric signing | Requires key pair management; only beneficial for distributed validation |
| Auth0 Java JWT | Less adoption than JJWT in Spring ecosystem |

### Implementation Notes
- Access token: 1 hour expiration, contains userId, username, roles
- Remember-me token: 30 days expiration
- Token blacklist for logout: Store invalidated tokens in memory/Redis until expiry
- Signing key: Load from configuration, support rotation via multiple keys

---

## 2. Password Hashing Strategy

### Decision
Use BCrypt with strength factor 10 (default in Spring Security).

### Rationale
- BCrypt is intentionally slow, resistant to brute force
- Strength 10 balances security vs. performance (~100ms per hash)
- Built-in salt generation prevents rainbow table attacks
- Spring Security's BCryptPasswordEncoder is battle-tested

### Alternatives Considered
| Alternative | Rejected Because |
|-------------|------------------|
| Argon2 | Newer, less ecosystem support in Spring; BCrypt sufficient |
| PBKDF2 | Less resistant to GPU attacks than BCrypt |
| SCrypt | Memory-hard but more complex to tune; BCrypt is simpler |

### Implementation Notes
- Use `BCryptPasswordEncoder` from Spring Security
- Never store plain passwords, even in logs
- Rehash on login if strength factor changes (future-proofing)

---

## 3. Account Lockout Implementation

### Decision
Track failed attempts in database, auto-unlock after 15 minutes.

### Rationale
- Database persistence survives service restarts
- 15 minutes is industry standard (OWASP recommendation range: 15-30 min)
- 5 attempts threshold balances security vs. user friction
- Failed attempt tracking enables security monitoring

### Alternatives Considered
| Alternative | Rejected Because |
|-------------|------------------|
| Redis-based tracking | Adds infrastructure dependency; DB sufficient for auth service |
| IP-based rate limiting | Doesn't protect against credential stuffing from distributed IPs |
| Permanent lockout | Too aggressive; increases support burden |

### Implementation Notes
- Store: `failed_attempts` count, `locked_until` timestamp on User entity
- Reset failed_attempts on successful login
- Check lockout status before password verification (prevent timing attacks)
- Log all lockout events for security monitoring

---

## 4. Session/Token Invalidation on Logout

### Decision
Token blacklist with in-memory cache, fallback to database.

### Rationale
- JWT is stateless, but logout requires invalidation capability
- Blacklist only needs to persist until token expiry (1 hour max for standard tokens)
- In-memory cache provides fast validation; DB backup for restarts

### Alternatives Considered
| Alternative | Rejected Because |
|-------------|------------------|
| Short-lived tokens + refresh | Doesn't solve logout requirement; refresh can be stolen |
| Redis blacklist | Adds infrastructure; in-memory sufficient for single instance |
| Token version in DB | Requires DB hit on every request; defeats stateless benefit |

### Implementation Notes
- Blacklist structure: `Set<String>` of token JTI (JWT ID)
- Periodic cleanup of expired blacklist entries
- Consider Redis if scaling to multiple instances later

---

## 5. Role-Based Access Control Pattern

### Decision
Spring Security with custom `@PreAuthorize` annotations and role hierarchy.

### Rationale
- Spring Security's method security is declarative and maintainable
- Role hierarchy (ADMIN > USER) reduces permission complexity
- Annotations on controller methods are explicit and auditable

### Alternatives Considered
| Alternative | Rejected Because |
|-------------|------------------|
| Custom filter chain | More code, less standardized than @PreAuthorize |
| ACL (Access Control Lists) | Overkill for simple role-based access |
| External authorization service (OPA) | Adds latency and infrastructure complexity |

### Implementation Notes
- Roles stored as enum: `USER`, `ADMIN`
- Many-to-many User-Role relationship
- `@PreAuthorize("hasRole('ADMIN')")` on protected endpoints
- Default role assignment: `USER` on registration

---

## 6. Database Schema Design

### Decision
Normalized schema with separate tables for users, roles, user_roles, login_attempts.

### Rationale
- Normalized design supports future expansion (more roles, permissions)
- Separate login_attempts table keeps user table clean
- Indexing strategy optimized for auth queries (username, email lookups)

### Implementation Notes
```sql
-- Core tables
users (id, username, email, password_hash, status, failed_attempts, locked_until, created_at, updated_at)
roles (id, name, description, created_at)
user_roles (user_id, role_id)
login_attempts (id, user_id, success, ip_address, user_agent, created_at)
```

---

## 7. API Versioning Strategy

### Decision
URL path versioning: `/api/v1/auth/*`

### Rationale
- Explicit and visible in URLs
- Easy to maintain multiple versions if needed
- Consistent with op-stack-service patterns

### Alternatives Considered
| Alternative | Rejected Because |
|-------------|------------------|
| Header versioning | Less discoverable, harder to test in browser |
| Query parameter | Non-standard, pollutes URLs |

---

## 8. Error Response Format

### Decision
Standardized JSON error response with code, message, timestamp.

### Rationale
- Consistent format simplifies client error handling
- No security-sensitive information in error messages
- Includes request tracking for debugging

### Implementation Notes
```json
{
  "code": "AUTH_001",
  "message": "Invalid credentials",
  "timestamp": "2025-12-25T10:00:00Z",
  "traceId": "abc123"
}
```

Error codes:
- AUTH_001: Invalid credentials
- AUTH_002: Account locked
- AUTH_003: Token expired
- AUTH_004: Token invalid
- AUTH_005: Access denied
- AUTH_006: Email already exists
- AUTH_007: Username already exists
- AUTH_008: Password policy violation

---

## Summary

All technical unknowns have been resolved. The implementation will use established patterns from op-stack-service with minor adaptations for auth-specific requirements. No NEEDS CLARIFICATION items remain.

**Ready for Phase 1: Design & Contracts**
