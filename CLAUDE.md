# CLAUDE.md - TreeHole 项目上下文

## 项目简介

TreeHole（树洞）是一个匿名社区后端系统，用户可以匿名发布帖子、评论，管理员可审核用户和管理内容。项目定位为简历项目，需持续优化以展示工程能力。

## 技术栈

- Java 17 + Spring Boot 4.0.3
- MyBatis（原生，非 MyBatis-Plus）
- MySQL 8.x (utf8mb4)
- Spring Session + Redis（分布式会话）
- Spring Security Crypto（BCrypt 密码加密）
- Lombok
- Jackson（JSR-310 时间模块）

## 项目结构

```
src/main/java/com/huanghaha/treehole/
├── common/
│   ├── Result.java              # 统一返回结果 (code/msg/data)
│   └── ForbiddenWordUtil.java   # 敏感词过滤工具（抛 BusinessException）
├── config/
│   ├── PasswordConfig.java      # BCrypt PasswordEncoder Bean
│   ├── RedisConfig.java         # RedisTemplate 配置 (JDK序列化)
│   ├── RedisSessionConfig.java  # Spring Session + Cookie 配置
│   └── WebConfig.java           # 拦截器注册 + 路径规则
├── controller/
│   ├── UserController.java      # 注册/登录/用户信息/退出
│   ├── PostController.java      # 发帖/列表/分页/删除/点赞/收藏
│   ├── CommentController.java   # 评论/按帖子查评论/删除
│   └── AdminController.java     # 用户审核/删帖/删评论（权限校验抽为 getAdminFromSession）
├── entity/
│   ├── User.java                # id/username/password/status/isDeleted/createTime
│   ├── Post.java                # id/userId/username/content/likeCount/favoriteCount/isDeleted/createTime
│   ├── PostLike.java            # id/userId/postId/isDeleted/createTime
│   ├── PostFavorite.java        # id/userId/postId/isDeleted/createTime
│   └── Comment.java             # id/userId/username/content/postId/isDeleted/createTime
├── exception/
│   ├── BusinessException.java   # 自定义业务异常（code + message）
│   └── GlobalExceptionHandler.java  # 全局异常处理（区分业务/系统异常）
├── interceptor/
│   └── LoginInterceptor.java    # 登录拦截器
├── mapper/
│   ├── UserMapper.java
│   ├── PostMapper.java
│   ├── PostLikeMapper.java
│   ├── PostFavoriteMapper.java
│   └── CommentMapper.java
├── service/
│   ├── UserService.java / UserServiceImpl.java
│   ├── PostService.java / PostServiceImpl.java
│   ├── LikeService.java / LikeServiceImpl.java
│   ├── FavoriteService.java / FavoriteServiceImpl.java
│   ├── CommentService.java / CommentServiceImpl.java
│   └── AdminService.java / AdminServiceImpl.java
└── TreeholeApplication.java     # 启动类

src/main/resources/
├── mapper/                      # MyBatis XML 映射文件
│   ├── UserMapper.xml
│   ├── PostMapper.xml
│   ├── PostLikeMapper.xml
│   ├── PostFavoriteMapper.xml
│   └── CommentMapper.xml
├── static/
│   ├── index.html               # 前端单页应用
│   ├── css/style.css            # 样式文件
│   └── js/app.js                # 前端逻辑
└── application.yml              # 主配置文件
```

## 数据库

- 数据库名：`treehole`，字符集 `utf8mb4`
- 初始化脚本：`treehole.sql`
- 五张表：`user`、`post`、`comment`、`post_like`、`post_favorite`
- User.status: 0=待审核, 1=正常, 2=封禁, 99=管理员
- 所有表均使用逻辑删除：`is_deleted`（0=未删除, 1=已删除）
- 删除操作为 UPDATE is_deleted=1，查询均带 WHERE is_deleted=0 条件
- Post 表含冗余计数字段 `like_count`、`favorite_count`，点赞/收藏时通过 SQL 原子更新（`GREATEST(count-1, 0)` 防负数）
- post_like 和 post_favorite 均有 `uk_user_post` 唯一索引，防止同一用户重复点赞/收藏

## API 概览

| 模块 | 方法   | 路径                       | 需登录 | 说明                      |
| ---- | ------ | -------------------------- | ------ | ------------------------- |
| 用户 | POST   | /user/register             | 否     | 注册（默认待审核）        |
| 用户 | POST   | /user/login                | 否     | 登录                      |
| 用户 | GET    | /user/me                   | 否     | 获取当前用户信息          |
| 用户 | GET    | /user/logout               | 否     | 退出登录                  |
| 帖子 | POST   | /post/publish              | 是     | 发布帖子（≤500字）        |
| 帖子 | GET    | /post/list                 | 否     | 全量帖子列表              |
| 帖子 | GET    | /post/page                 | 否     | 分页帖子（Redis缓存5min） |
| 帖子 | DELETE | /post/delete               | 是     | 删除自己的帖子            |
| 帖子 | POST   | /post/like/{postId}        | 是     | 点赞/取消点赞             |
| 帖子 | GET    | /post/liked/{postId}       | 是     | 查询当前用户是否点赞      |
| 帖子 | POST   | /post/favorite/{postId}    | 是     | 收藏/取消收藏             |
| 帖子 | GET    | /post/favorited/{postId}   | 是     | 查询当前用户是否收藏      |
| 帖子 | GET    | /post/favorites            | 是     | 获取当前用户收藏列表      |
| 评论 | POST   | /comment                   | 是     | 发布评论（≤200字）        |
| 评论 | GET    | /comment/post/{postId}     | 否     | 按帖子查评论              |
| 评论 | DELETE | /comment/{id}              | 是     | 删除自己的评论            |
| 管理 | GET    | /admin/waitAuditUserList   | 是     | 待审核用户列表            |
| 管理 | POST   | /admin/auditUser           | 是     | 审核用户（通过/封禁）     |
| 管理 | DELETE | /admin/post/{postId}       | 是     | 管理员删帖                |
| 管理 | DELETE | /admin/comment/{commentId} | 是     | 管理员删评论              |

## 关键业务规则

- 注册后 status=0（待审核），需管理员审核通过（status=1）才能登录
- 登录时校验：用户存在 → 密码正确 → 账号非待审核 → 账号非封禁
- 管理员权限判断：`user.getStatus() == 99`（AdminController 抽取为 getAdminFromSession 方法，AdminServiceImpl 抽取为 validateAdmin 方法）
- 帖子分页使用 Redis 缓存，key 格式 `post:page:{page}:{size}`，TTL 5分钟（Redis 连接失败时自动降级为数据库查询，不影响业务）
- 发布帖子后调用 `clearPageCache()` 清除分页缓存（Redis 不可用时静默跳过）
- 敏感词列表配置在 `application.yml` 的 `treehole.forbidden-words` 中
- 帖子内容上限 500 字，评论内容上限 200 字
- 点赞/收藏采用 toggle 模式 + 原子 SQL（INSERT ... ON DUPLICATE KEY UPDATE），避免并发竞态导致重复点赞报错；前端 500ms 防抖防止快速点击

## 登录拦截器路径规则

拦截（需登录）：

- `/post/publish`, `/post/delete`
- `/post/like/*`, `/post/liked/*`, `/post/favorite/*`, `/post/favorited/*`, `/post/favorites`
- `/comment`, `/comment/*`
- `/admin/*`

放行（无需登录）：

- `/user/login`, `/user/register`, `/user/me`, `/user/logout`

## 配置要点

- 服务端口：8080
- Session 超时：30 分钟
- Redis：localhost:6379
- MySQL：localhost:3306/treehole
- Cookie 名称：`TREEHOLE_SESSION`，SameSite=Lax
- MyBatis 开启驼峰映射和 null 设置

## 编码规范

- 统一使用 `@Autowired`
- 统一返回 `Result<T>`，成功 code=200，失败 code=500
- Controller 层只做参数获取和 Session 读取，校验逻辑统一在 Service 层
- 业务异常通过 `throw new BusinessException()` 抛出，系统异常通过 RuntimeException 抛出
- GlobalExceptionHandler 区分处理：BusinessException 用 log.warn，RuntimeException 用 log.error
- 使用 `@Slf4j` + `log.info/warn/error` 记录日志
- 实体类使用 Lombok `@Data`
- Mapper 使用 XML 映射文件，非注解方式
- 管理员权限常量：`ADMIN_STATUS = 99`

## 已完成优化

1. ✅ 自定义 BusinessException 替代 RuntimeException，GlobalExceptionHandler 区分业务/系统异常
2. ✅ AdminController 移除 UserMapper 直接注入，改走 AdminService
3. ✅ 消除 Controller/Service 层重复参数校验，Controller 只做参数获取，校验统一在 Service
4. ✅ 管理员权限校验抽取为独立方法（Controller: getAdminFromSession, Service: validateAdmin）
5. ✅ Post/Comment 改为逻辑删除，三张表统一使用 is_deleted 字段
6. ✅ 前端页面美化（暗色主题、卡片布局、Toast 提示、Tab 切换、响应式设计）
7. ✅ Redis 操作容错处理（PostServiceImpl 所有 Redis 调用 try-catch 包裹，连接失败时降级为数据库查询并打印 warn 日志）
8. ✅ 帖子点赞/收藏功能（PostLike/PostFavorite 关联表 + Post 冗余计数字段，toggle 模式 + INSERT ON DUPLICATE KEY UPDATE 原子操作 + 前端 500ms 防抖）
9. ✅ Post/Comment 返回 username 而非 userId（JOIN 查询），前端展示发布者昵称
10. ✅ 前端 UI 改版：发帖入口改为右上角 + 按钮、卡片式帖子布局、评论折叠展开
11. ✅ 修复 PostLikeMapper/PostFavoriteMapper toggle SQL 中 createTime 参数缺失问题（MyBatis Parameter not found）
12. ✅ 修复 AdminController auditUser 接口字段名不匹配问题（userName → username，@RequestBody → @ModelAttribute）

## 待优化方向（简历项目增强）

### 功能层面

1. **缺少帖子详情接口**：目前只有列表和分页，没有单帖查询
2. **评论无分页**：数据量大时有性能风险
3. **缺少用户修改密码/个人信息功能**
4. **缺少帖子搜索功能**

### 安全层面

5. **数据库密码明文**：application.yml 中密码为明文 1234
6. ✅ ~~**用户信息泄露风险**：Post/Comment 返回了 userId，匿名社区不应暴露~~（已改为返回 username）
7. **缺少接口限流**：注册/登录等接口无防刷机制
8. **CORS 未配置**：前后端分离时需要

### 技术亮点可增强

9. **Redis 缓存仅用于帖子分页**：可扩展到评论、用户信息等
10. **缺少 Swagger/OpenAPI 文档**：简历项目应有在线接口文档
11. **缺少 Docker 部署支持**
12. **缺少单元测试覆盖**：目前仅有 PostServiceTest 和 UserServiceTest
13. **可引入消息队列**：如审核通知、帖子发布通知
14. **可引入 Elasticsearch**：实现帖子全文搜索

## 构建与运行

```bash
# Maven 构建
./mvnw clean package -DskipTests

# 运行（需先启动 MySQL 和 Redis）
./mvnw spring-boot:run

# 运行测试
./mvnw test
```

## 依赖版本

- Spring Boot: 4.0.3
- MyBatis Spring Boot Starter: 4.0.1
- Spring Session Data Redis: 3.4.0
- Java: 17
