# OP-Stack Auth Service

基于 DDD（领域驱动设计）架构的认证授权服务，提供用户注册、登录、JWT Token 认证和 RBAC 权限控制功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | LTS 版本 |
| Spring Boot | 3.4.1 | 基础框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis-Plus | 3.5.7 | ORM 框架 |
| JJWT | 0.12.6 | JWT Token 处理 |
| MySQL | 8.0 | 数据库 |
| Druid | 1.2.20 | 数据库连接池 |

## 项目结构

```
op-stack-auth/
├── common/                          # 公共模块
│   └── src/main/java/.../common/
│       ├── exception/               # 异常定义
│       └── result/                  # 统一返回结果
├── domain/                          # 领域层
│   ├── domain-model/                # 领域模型
│   │   └── src/main/java/.../model/
│   │       ├── user/                # 用户聚合根
│   │       ├── role/                # 角色实体
│   │       └── session/             # Token 值对象
│   ├── domain-api/                  # 领域服务接口
│   ├── domain-impl/                 # 领域服务实现
│   ├── repository-api/              # 仓储接口
│   └── security-api/                # 安全接口
├── application/                     # 应用层
│   ├── application-api/             # 应用服务接口
│   └── application-impl/            # 应用服务实现
├── infrastructure/                  # 基础设施层
│   ├── repository/
│   │   └── mysql-impl/              # MySQL 仓储实现
│   └── security/
│       └── jwt-impl/                # JWT Token 实现
├── interface/                       # 接口层
│   └── interface-http/              # REST API 控制器
└── bootstrap/                       # 启动模块
    └── src/main/
        ├── java/.../bootstrap/
        │   ├── AuthApplication.java # 应用入口
        │   └── config/              # 配置类
        └── resources/
            ├── application.yml      # 配置文件
            └── db/migration/        # 数据库脚本
```

## 功能特性

### 用户认证
- **用户注册**: 支持用户名、邮箱、密码注册，密码策略校验
- **用户登录**: 支持用户名/邮箱登录，Remember-Me 功能
- **Token 刷新**: 支持 Token 无感刷新
- **用户登出**: Token 黑名单机制

### 安全特性
- **JWT Token**: 无状态认证，支持 1 小时/30 天过期时间
- **密码加密**: BCrypt 加密存储
- **账户锁定**: 5 次失败后锁定 15 分钟，自动解锁
- **登录审计**: 记录登录尝试（IP、User-Agent）

### 权限控制
- **RBAC**: 基于角色的访问控制
- **预置角色**: USER（普通用户）、ADMIN（管理员）
- **方法级权限**: 支持 @PreAuthorize 注解

## 快速开始

### 环境要求
- JDK 21+
- Maven 3.8+
- MySQL 8.0+

### 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE auth_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行初始化脚本
-- 脚本位置: bootstrap/src/main/resources/db/migration/V1__init_schema.sql
```

### 配置修改

编辑 `bootstrap/src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 构建运行

```bash
# 构建项目
mvn clean package -DskipTests

# 启动服务
mvn spring-boot:run -pl bootstrap

# 或者直接运行 JAR
java -jar bootstrap/target/bootstrap-1.0.0-SNAPSHOT.jar
```

服务默认运行在 `http://localhost:9090`

## API 接口

### 认证接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/auth/register` | 用户注册 | 否 |
| POST | `/api/v1/auth/login` | 用户登录 | 否 |
| POST | `/api/v1/auth/logout` | 用户登出 | 是 |
| POST | `/api/v1/auth/refresh` | 刷新 Token | 否 |
| GET | `/api/v1/auth/me` | 获取当前用户 | 是 |

### 管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/v1/admin/dashboard` | 管理面板 | ADMIN |
| GET | `/api/v1/admin/stats` | 系统统计 | ADMIN |

### 接口示例

#### 注册用户
```bash
curl -X POST http://localhost:9090/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test1234"
  }'
```

#### 用户登录
```bash
curl -X POST http://localhost:9090/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "testuser",
    "password": "Test1234",
    "rememberMe": false
  }'
```

#### 获取当前用户
```bash
curl -X GET http://localhost:9090/api/v1/auth/me \
  -H "Authorization: Bearer <your_token>"
```

## 配置说明

### JWT 配置

```yaml
auth:
  jwt:
    secret: your-256-bit-secret-key    # JWT 签名密钥
    expiration-seconds: 3600            # Token 过期时间（1小时）
    remember-me-expiration-seconds: 2592000  # Remember-Me 过期时间（30天）
    issuer: op-stack-auth               # Token 签发者
  security:
    max-failed-attempts: 5              # 最大失败尝试次数
    lockout-duration-minutes: 15        # 锁定时长（分钟）
```

### 数据库表结构

| 表名 | 说明 |
|------|------|
| users | 用户表 |
| roles | 角色表 |
| user_roles | 用户角色关联表 |
| login_attempts | 登录尝试记录表 |

## 错误码

| 错误码 | 说明 |
|--------|------|
| AUTH_001 | 无效的凭证 |
| AUTH_002 | 账户已锁定 |
| AUTH_003 | Token 已过期 |
| AUTH_004 | 无效的 Token |
| AUTH_005 | 访问被拒绝 |
| AUTH_006 | 权限不足 |
| AUTH_007 | 用户名已存在 |
| AUTH_008 | 邮箱已存在 |
| AUTH_009 | 账户未激活 |
| AUTH_010 | 需要认证 |

## 开发指南

### 添加新的 API 端点

1. 在 `application-api` 中定义 Command/Query
2. 在 `application-impl` 中实现应用服务
3. 在 `interface-http` 中创建 Controller 和 DTO
4. 在 `SecurityConfig` 中配置访问权限

### 添加新的领域服务

1. 在 `domain-api` 中定义接口
2. 在 `domain-impl` 中实现服务
3. 在 `repository-api` 中定义仓储接口（如需要）
4. 在 `mysql-impl` 中实现仓储

## 监控端点

| 端点 | 说明 |
|------|------|
| `/actuator/health` | 健康检查 |
| `/actuator/info` | 应用信息 |
| `/actuator/metrics` | 指标数据 |

## License

MIT License
