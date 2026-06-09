# TreeHole 树洞社区

一个轻量级的匿名社区后端系统，基于 Spring Boot + MyBatis + Redis 构建，支持用户发帖、评论互动、AI 智能回复及后台审核管理。

## 功能特性

- **用户认证**：BCrypt 密码加密，Spring Session + Redis 分布式会话
- **内容发布**：发帖（≤500字）、评论（≤200字）
- **互动功能**：点赞、收藏（toggle 模式 + 原子操作）
- **AI 回复**：集成 DeepSeek API，自动分析帖子情绪生成个性化回复
- **后台管理**：用户审核、内容管理
- **安全防护**：敏感词过滤、登录拦截器、全局异常处理

## 技术栈

| 分类       | 技术                            |
| ---------- | ------------------------------- |
| 后端框架   | Spring Boot 4.0.3               |
| 数据库     | MySQL 8.x + MyBatis 4.0.1       |
| 缓存与会话 | Redis + Spring Session          |
| 安全       | Spring Security Crypto (BCrypt) |
| 前端       | Vue 3 + 原生 JavaScript         |
| 工具       | Lombok, Jackson (JSR-310)       |

## 项目结构

```
src/main/java/com/huanghaha/treehole/
├── common/          # 通用基础类（Result、BusinessException）
├── util/            # 工具类（敏感词过滤）
├── enums/           # 枚举类（AI 提示词）
├── config/          # Spring 配置类
├── entity/          # 数据库实体
├── handler/         # 全局处理器
├── controller/      # 控制器层
├── service/         # 服务层
├── mapper/          # 数据访问层
└── interceptor/     # 登录拦截器
```

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.x
- Redis 6.x+
- Maven 3.8+

### 1. 初始化数据库

```sql
-- 创建数据库
CREATE DATABASE treehole DEFAULT CHARACTER SET utf8mb4;

-- 执行初始化脚本
SOURCE treehole.sql;
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/treehole?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379

treehole:
  ai:
    api-url: https://api.deepseek.com/chat/completions
    api-key: your_api_key
  forbidden-words:
    - 敏感词1
    - 敏感词2
```

### 3. 构建运行

```bash
# 构建
mvn clean package -DskipTests

# 运行
java -jar target/treehole-0.0.1-SNAPSHOT.jar
```

访问 http://localhost:8080

## Docker 部署

### 1. 安装 Docker

```bash
curl -fsSL https://get.docker.com | sh
```

### 2. 创建部署目录

```bash
mkdir -p /opt/treehole
cd /opt/treehole
```

### 3. 上传项目并配置

将项目文件上传到服务器，创建以下文件：

**docker-compose.yml**

```yaml
version: "3.8"
services:
  mysql:
    image: mysql:8.0
    container_name: treehole_mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: treehole
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./treehole.sql:/docker-entrypoint-initdb.d/treehole.sql
    command: --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7-alpine
    container_name: treehole_redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  app:
    build: .
    container_name: treehole_app
    restart: always
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis

volumes:
  mysql_data:
  redis_data:
```

**Dockerfile**

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/treehole-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 4. 构建启动

```bash
# 构建 jar 包并上传（或在服务器上执行）
mvn clean package -DskipTests

# 启动所有服务
docker-compose up -d --build
```

## API 文档

| 模块 | 方法 | 路径                     | 说明         |
| ---- | ---- | ------------------------ | ------------ |
| 用户 | POST | /user/register           | 注册         |
| 用户 | POST | /user/login              | 登录         |
| 用户 | GET  | /user/me                 | 获取当前用户 |
| 帖子 | POST | /post/publish            | 发布帖子     |
| 帖子 | GET  | /post/page               | 分页列表     |
| 帖子 | POST | /post/like/{id}          | 点赞/取消    |
| 帖子 | POST | /post/favorite/{id}      | 收藏/取消    |
| 评论 | POST | /comment                 | 发布评论     |
| 管理 | GET  | /admin/waitAuditUserList | 待审核用户   |
| 管理 | POST | /admin/auditUser         | 审核用户     |
| AI   | POST | /ai/reply/{postId}       | 生成 AI 回复 |

详细接口请参考代码注释或使用 Swagger 文档。
