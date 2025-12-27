<!--
Sync Impact Report
==================
Version change: 0.0.0 → 1.0.0 (initial ratification)
Added sections:
  - Principle I: DDD Architecture
  - Principle II: API Standards
  - Principle III: Gateway Integration
  - Principle IV: Pagination Standards
  - Section: Technology Constraints
  - Section: Code Style
  - Governance rules
Templates requiring updates:
  - .specify/templates/plan-template.md: ⚠ pending review
  - .specify/templates/spec-template.md: ⚠ pending review
  - .specify/templates/tasks-template.md: ⚠ pending review
Follow-up TODOs: None
-->

# OP-Stack Auth Constitution

## Core Principles

### I. DDD Architecture

项目 MUST 遵循领域驱动设计（DDD）分层架构：

- **interface**: 接口层，REST API 控制器和 DTO
- **application**: 应用层，应用服务、Command/Query 对象
- **domain**: 领域层，领域模型、领域服务、仓储接口
- **infrastructure**: 基础设施层，仓储实现、外部服务集成
- **bootstrap**: 启动模块，配置类、应用入口
- **common**: 公共模块，异常定义、统一响应

跨层依赖 MUST 遵循：interface → application → domain ← infrastructure

### II. API Standards

所有 REST API MUST 遵循以下规范：

- **URL 格式**: `/api/{module}/v{version}/{resource}`，例如 `/api/auth/v1/login`
- **统一响应格式**: 使用 `Result<T>` 包装，包含 `success`, `code`, `message`, `data`, `traceId` 字段
- **分页请求**: 继承 `PageableRequest` 基类
- **分页响应**: 使用 `PageResult<T>` 包装
- **Swagger 文档**: 所有接口 MUST 使用中文注解描述

### III. Gateway Integration

认证服务 MUST 支持 Gateway 统一鉴权模式：

- 提供 `/api/auth/v1/validate` 接口供 Gateway 验证 Token
- 接口级权限控制由 Gateway 负责，服务端不使用 `@PreAuthorize` 注解
- Gateway 注入的上下文字段（`tenantId`, `traceId`, `userId`）通过请求基类传递

### IV. Pagination Standards

分页请求参数格式（基类 `PageableRequest`）：

```json
{
  "page": 1,          // 页码（从 1 开始），默认 1，最小 1
  "size": 20,         // 每页大小，默认 20，范围 1-100
  "tenantId": null,   // 租户ID（网关注入，hidden）
  "traceId": null,    // 追踪ID（网关注入，hidden）
  "userId": null      // 用户ID（网关注入，hidden）
}
```

分页响应结果格式（类 `PageResult<T>`）：

```json
{
  "code": 0,
  "message": "success",
  "success": true,
  "data": {
    "content": [],           // 数据列表
    "page": 1,               // 当前页码（从1开始）
    "size": 10,              // 每页大小
    "totalElements": 100,    // 总记录数
    "totalPages": 10,        // 总页数
    "first": true,           // 是否为第一页
    "last": false            // 是否为最后一页
  }
}
```

## Technology Constraints

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | LTS 版本 |
| Spring Boot | 3.4.x | 基础框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis-Plus | 3.5.x | ORM 框架 |
| JJWT | 0.12.x | JWT 处理 |
| MySQL | 8.0 | 数据库 |
| SpringDoc | 2.x | API 文档 |

## Code Style

- Java 代码 MUST 遵循标准 Java 编码规范
- 类和方法 MUST 使用中文注释说明用途
- API 接口 MUST 使用 OpenAPI 注解并提供中文描述
- 密码等敏感信息 MUST NOT 出现在日志中

## Governance

- 本宪法优先于所有其他实践规范
- 修订 MUST 经过文档记录、审批和迁移计划
- 所有 PR/Review MUST 验证是否符合宪法规定
- 复杂度增加 MUST 有合理的业务理由

**Version**: 1.0.0 | **Ratified**: 2025-12-27 | **Last Amended**: 2025-12-27
