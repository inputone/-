# 树洞（TreeHole）API 接口文档

## 概述

- **项目名称**：树洞匿名社区
- **技术栈**：Vue 3 + Spring Boot + MyBatis + MySQL + Redis
- **基础URL**：`http://localhost:8080`
- **认证方式**：Session + Cookie（Cookie名称：`TREEHOLE_SESSION`）
- **统一响应格式**：`{ "code": 200, "msg": "success", "data": ... }`

---

## 统一响应结构

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200=成功，其他=失败 |
| msg | String | 状态信息 |
| data | Object | 返回数据（可选） |

---

## 用户模块 `/user`

### 1. 用户注册

```
POST /user/register
Content-Type: application/x-www-form-urlencoded
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名，3-20位 |
| password | String | 是 | 密码，6位以上 |

**响应示例**

```json
{
  "code": 200,
  "msg": "注册成功，等待审核",
  "data": null
}
```

---

### 2. 用户登录

```
POST /user/login
Content-Type: application/x-www-form-urlencoded
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**响应示例（成功）**

```json
{
  "code": 200,
  "msg": "登录成功",
  "data": null
}
```

> 登录成功后，后端会自动设置 Session 和 Cookie

---

### 3. 获取当前用户信息

```
GET /user/me
```

**响应示例（已登录）**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "status": 1,
    "createTime": "2024-01-01T12:00:00"
  }
}
```

> status 说明：0=待审核，1=正常，2=封禁，99=管理员

---

### 4. 退出登录

```
GET /user/logout
```

**响应示例**

```json
{
  "code": 200,
  "msg": "已退出",
  "data": null
}
```

---

## 帖子模块 `/post`

### 5. 发布帖子（需登录）

```
POST /post/publish
Content-Type: application/x-www-form-urlencoded
Cookie: TREEHOLE_SESSION=xxx
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| content | String | 是 | 帖子内容，最多500字 |

**响应示例**

```json
{
  "code": 200,
  "msg": "发布成功",
  "data": null
}
```

---

### 6. 获取帖子列表（无需分页）

```
GET /post/list
```

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "username": "testuser",
      "content": "帖子内容",
      "likeCount": 10,
      "favoriteCount": 5,
      "commentCount": 3,
      "createTime": "2024-01-01T12:00:00"
    }
  ]
}
```

---

### 7. 分页获取帖子（支持Redis缓存）

```
GET /post/page?page=1&size=10
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页条数，默认10 |

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 1,
        "username": "testuser",
        "content": "帖子内容",
        "likeCount": 10,
        "favoriteCount": 5,
        "commentCount": 3,
        "createTime": "2024-01-01T12:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "totalPages": 10
  }
}
```

---

### 8. 删除自己的帖子（需登录）

```
DELETE /post/delete?id=1
Cookie: TREEHOLE_SESSION=xxx
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

---

### 9. 点赞/取消点赞（需登录）

```
POST /post/like/{postId}
Cookie: TREEHOLE_SESSION=xxx
```

**路径参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| postId | Long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

---

### 10. 查询当前用户是否已点赞

```
GET /post/liked/{postId}
Cookie: TREEHOLE_SESSION=xxx
```

**路径参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| postId | Long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "liked": true
  }
}
```

---

### 11. 收藏/取消收藏（需登录）

```
POST /post/favorite/{postId}
Cookie: TREEHOLE_SESSION=xxx
```

**路径参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| postId | Long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

---

### 12. 查询当前用户是否已收藏

```
GET /post/favorited/{postId}
Cookie: TREEHOLE_SESSION=xxx
```

**路径参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| postId | Long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "favorited": false
  }
}
```

---

### 13. 获取当前用户收藏的帖子列表（需登录）

```
GET /post/favorites
Cookie: TREEHOLE_SESSION=xxx
```

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "username": "testuser",
      "content": "帖子内容",
      "likeCount": 10,
      "favoriteCount": 5,
      "commentCount": 3,
      "createTime": "2024-01-01T12:00:00"
    }
  ]
}
```

---

## 评论模块 `/comment`

### 14. 发布评论（需登录）

```
POST /comment
Content-Type: application/x-www-form-urlencoded
Cookie: TREEHOLE_SESSION=xxx
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| postId | Long | 是 | 帖子ID |
| content | String | 是 | 评论内容，最多200字 |

**响应示例**

```json
{
  "code": 200,
  "msg": "评论成功",
  "data": null
}
```

---

### 15. 按帖子查询评论

```
GET /comment/post/{postId}
```

**路径参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| postId | Long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "username": "testuser",
      "postId": 1,
      "content": "评论内容",
      "createTime": "2024-01-01T12:00:00"
    }
  ]
}
```

---

### 16. 删除自己的评论（需登录）

```
DELETE /comment/{id}
Cookie: TREEHOLE_SESSION=xxx
```

**路径参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 评论ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

---

## 管理模块 `/admin`

> 所有接口需要管理员权限（status=99）

### 17. 获取待审核用户列表（需管理员）

```
GET /admin/waitAuditUserList
Cookie: TREEHOLE_SESSION=xxx
```

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "username": "newuser",
      "status": 0,
      "createTime": "2024-01-01T12:00:00"
    }
  ]
}
```

---

### 18. 审核用户（需管理员）

```
POST /admin/auditUser
Content-Type: application/x-www-form-urlencoded
Cookie: TREEHOLE_SESSION=xxx
```

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 要审核的用户名 |
| status | Integer | 是 | 审核结果，1=通过，2=封禁 |

**响应示例**

```json
{
  "code": 200,
  "msg": "审核成功",
  "data": null
}
```

---

### 19. 管理员删除任意帖子（需管理员）

```
DELETE /admin/post/{postId}
Cookie: TREEHOLE_SESSION=xxx
```

**路径参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| postId | Long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "帖子删除成功",
  "data": null
}
```

---

### 20. 管理员删除任意评论（需管理员）

```
DELETE /admin/comment/{commentId}
Cookie: TREEHOLE_SESSION=xxx
```

**路径参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| commentId | Long | 评论ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "评论删除成功",
  "data": null
}
```

---

## AI模块 `/ai`

### 21. 获取AI性格类型列表

```
GET /ai/prompt-types
```

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    { "type": "comfort", "name": "温暖安慰" },
    { "type": "humor", "name": "幽默吐槽" },
    { "type": "rational", "name": "理性分析" },
    { "type": "encourage", "name": "鼓励支持" },
    { "type": "comprehensive", "name": "综合回复" }
  ]
}
```

---

### 22. 生成AI回复（需登录）

```
POST /ai/reply/{postId}
Cookie: TREEHOLE_SESSION=xxx
```

**路径参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| postId | Long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "postId": 1,
    "content": "AI生成的回复内容...",
    "promptType": "comprehensive",
    "createTime": "2024-01-01T12:00:00"
  }
}
```

---

### 23. 获取帖子的AI回复列表

```
GET /ai/reply/{postId}
```

**路径参数**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| postId | Long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "postId": 1,
      "content": "AI生成的回复内容...",
      "promptType": "comprehensive",
      "createTime": "2024-01-01T12:00:00"
    }
  ]
}
```

---

## 错误码说明

| code | 说明 |
|------|------|
| 200 | 成功 |
| 500 | 系统错误或业务异常 |

---

## 拦截规则

### 需要登录的接口
- `POST /post/publish`
- `DELETE /post/delete`
- `POST /post/like/*`
- `GET /post/liked/*`
- `POST /post/favorite/*`
- `GET /post/favorited/*`
- `GET /post/favorites`
- `POST /comment`
- `DELETE /comment/{id}`
- `/admin/*`（需管理员）
- `POST /ai/reply/{postId}`
- `GET /ai/reply/{postId}`

### 无需登录的接口
- `POST /user/register`
- `POST /user/login`
- `GET /user/me`
- `GET /user/logout`
- `GET /post/list`
- `GET /post/page`
- `GET /comment/post/{postId}`
- `GET /ai/prompt-types`
- `GET /ai/reply/{postId}`

---

## 数据库表结构

### user 用户表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名 |
| password | VARCHAR(100) | 密码（BCrypt加密） |
| status | INT | 状态：0=待审核，1=正常，2=封禁，99=管理员 |
| is_deleted | TINYINT | 逻辑删除：0=未删除，1=已删除 |
| create_time | DATETIME | 创建时间 |

### post 帖子表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 发布者ID |
| username | VARCHAR(50) | 发布者昵称（冗余字段） |
| content | TEXT | 帖子内容 |
| like_count | INT | 点赞数 |
| favorite_count | INT | 收藏数 |
| comment_count | INT | 评论数 |
| is_deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

### comment 评论表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 评论者ID |
| username | VARCHAR(50) | 评论者昵称 |
| post_id | BIGINT | 所属帖子ID |
| content | TEXT | 评论内容 |
| is_deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

### post_like 点赞表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| post_id | BIGINT | 帖子ID |
| is_deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

### post_favorite 收藏表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| post_id | BIGINT | 帖子ID |
| is_deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

### ai_reply AI回复表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| post_id | BIGINT | 所属帖子ID |
| content | TEXT | AI回复内容 |
| prompt_type | VARCHAR(50) | 性格类型 |
| is_deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

---

## 项目技术栈

- **后端**：Java 17 + Spring Boot 4.0.3 + MyBatis + MySQL 8 + Redis
- **前端**：Vue 3 (CDN) + 原生 JavaScript + CSS3
- **认证**：Spring Session + Redis（分布式会话）
- **密码加密**：Spring Security Crypto (BCrypt)
- **AI**：DeepSeek API（调用外部AI服务）

---

## 启动说明

1. 确保 MySQL 和 Redis 已启动
2. 执行数据库初始化脚本 `treehole.sql`
3. 配置 `application.yml` 中的数据库连接信息
4. 运行 `TreeholeApplication.java` 启动后端服务
5. 访问 `http://localhost:8080` 查看前端页面
