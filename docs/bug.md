# Bug 记录

本文档记录 TreeHole 项目中出现过的 Bug 及解决方案。

---

## Bug 1：MyBatis toggleLike/toggleFavorite SQL 参数缺失

**发现时间**：2026-04-22

**错误信息**：

```
org.apache.ibatis.binding.BindingException: Parameter 'createTime' not found.
Available parameters are [postId, userId, param1, param2]
```

**影响范围**：

- 帖子点赞功能
- 帖子收藏功能

**根本原因**：
XML SQL 中使用了 `#{createTime}` 参数，但 Java Mapper 接口方法只有 `userId` 和 `postId` 两个参数：

```xml
<!-- PostLikeMapper.xml / PostFavoriteMapper.xml -->
<insert id="toggleLike">
    INSERT INTO post_like (user_id, post_id, is_deleted, create_time)
    VALUES (#{userId}, #{postId}, 0, #{createTime})  <!-- createTime 参数不存在 -->
    ON DUPLICATE KEY UPDATE is_deleted = IF(is_deleted = 0, 1, 0), create_time = IF(is_deleted = 0, create_time, #{createTime})
</insert>
```

```java
// PostLikeMapper.java
void toggleFavorite(Long userId, Long postId);  // 没有 createTime 参数
```

**解决方案**：
使用 MySQL 的 `NOW()` 函数替代 Java 传参：

```xml
<insert id="toggleLike">
    INSERT INTO post_like (user_id, post_id, is_deleted, create_time)
    VALUES (#{userId}, #{postId}, 0, NOW())
    ON DUPLICATE KEY UPDATE is_deleted = IF(is_deleted = 0, 1, 0)
</insert>
```

**涉及文件**：

- `src/main/resources/mapper/PostLikeMapper.xml`
- `src/main/resources/mapper/PostFavoriteMapper.xml`

---

## Bug 2：管理员审核用户接口字段名不匹配 + 注解错误

**发现时间**：2026-04-22

**错误现象**：
管理员点击审核用户按钮后，前端弹出"系统繁忙，请稍后再试"

**影响范围**：

- 管理员审核新用户（通过/封禁）

**根本原因**：

1. **字段名不匹配**：前端发送 `username`，DTO 定义为 `userName`

   ```javascript
   // 前端 app.js
   async function auditUser(username, status) {
     const data = await api(
       "POST",
       "/admin/auditUser",
       `username=${username}&status=${status}`,
     );
   }
   ```

   ```java
   // AdminController.java DTO
   public static class UserAuditDTO {
       private String userName;  // 与前端发送的 username 不匹配
       private Integer status;
   }
   ```

2. **注解错误**：前端使用 `application/x-www-form-urlencoded` 发送表单数据，后端用 `@RequestBody` 接收（期望 JSON）
   ```java
   // 错误写法
   public Result<String> auditUser(@RequestBody UserAuditDTO auditDTO, HttpSession session)
   ```

**解决方案**：

1. 统一字段名为 `username`
2. 将 `@RequestBody` 改为 `@ModelAttribute`（支持表单数据绑定）

```java
public static class UserAuditDTO {
    private String username;  // 与前端参数名一致
    private Integer status;
}

@PostMapping("/auditUser")
public Result<String> auditUser(@ModelAttribute UserAuditDTO auditDTO, HttpSession session)
```

**涉及文件**：

- `src/main/java/com/huanghaha/treehole/controller/AdminController.java`

---

## Bug 3：AiReplyMapper 缺少 @Mapper 注解 + AiController Session 属性名错误

**发现时间**：2026-04-22

**错误现象**：

1. 启动时报错：`Field aiReplyMapper in AiServiceImpl required a bean of type 'AiReplyMapper' that could not be found`
2. 点击 AI 按钮提示"请先登录"，但用户已登录

**影响范围**：

- AI 回复功能完全不可用

**根本原因**：

1. **Mapper 缺少注解**：其他 Mapper 都有 `@Mapper` 注解，AiiReplyMapper 创建时漏加了
2. **Session 属性名不一致**：拦截器检查的是 `loginUser`，但 AiController 检查的是 `userId`

```java
// LoginInterceptor.java - 拦截器检查 loginUser
if (session == null || session.getAttribute("loginUser") == null) {

// AiController.java - 错误检查了 userId
Object userId = session.getAttribute("userId");  // 始终为 null
if (userId == null) {
    return Result.error("请先登录");
}
```

**解决方案**：

1. 给 AiReplyMapper 添加 `@Mapper` 注解
2. 统一使用 `loginUser` 作为 Session 属性名

```java
// AiReplyMapper.java
@Mapper  // 添加注解
public interface AiReplyMapper {

// AiController.java
Object loginUser = session.getAttribute("loginUser");  // 与拦截器一致
if (loginUser == null) {
    return Result.error("请先登录");
}
```

**涉及文件**：

- `src/main/java/com/huanghaha/treehole/mapper/AiReplyMapper.java`
- `src/main/java/com/huanghaha/treehole/controller/AiController.java`

---

## Bug 4：评论发布/删除后帖子评论数不更新

**发现时间**：2026-04-23

**错误现象**：
用户评论后，帖子的评论数没有增加；删除评论后，评论数也没有减少。

**影响范围**：

- 帖子评论数显示不准确

**根本原因**：

1. post 表缺少 `comment_count` 字段（仅有 `like_count` 和 `favorite_count`）
2. CommentServiceImpl 发布/删除评论时没有调用 PostMapper 更新评论数

**解决方案**：

1. 数据库添加字段：`ALTER TABLE post ADD COLUMN comment_count INT DEFAULT 0 COMMENT '评论数'`
2. Post 实体添加 `commentCount` 字段
3. PostMapper 添加 `incrementCommentCount()` 和 `decrementCommentCount()` 方法
4. PostMapper.xml 添加对应 SQL（使用 `GREATEST(count-1, 0)` 防负数）
5. CommentServiceImpl 在 publish 和 delete 方法中调用评论数更新

```java
// CommentServiceImpl.java
@Resource
private PostMapper postMapper;

@Override
public void publish(Long userId, Long postId, String content) {
    // ... 入库逻辑
    commentMapper.insert(comment);
    postMapper.incrementCommentCount(postId);  // 新增
}

@Override
public void delete(Long commentId, Long userId) {
    // ... 校验逻辑
    commentMapper.deleteById(commentId);
    postMapper.decrementCommentCount(comment.getPostId());  // 新增
}
```

**涉及文件**：

- `treehole.sql`
- `src/main/java/com/huanghaha/treehole/entity/Post.java`
- `src/main/java/com/huanghaha/treehole/mapper/PostMapper.java`
- `src/main/resources/mapper/PostMapper.xml`
- `src/main/java/com/huanghaha/treehole/service/impl/CommentServiceImpl.java`

---

## Bug 5：AI回复刷新界面后消失

**发现时间**：2026-04-23

**错误现象**：
生成 AI 回复后刷新页面，已生成的 AI 回复消失，需要重新生成才能显示。

**影响范围**：

- AI 回复功能（刷新后数据丢失）

**根本原因**：
`toggleComments` 函数展开帖子时只加载评论，没有加载 AI 回复。AI 回复只在生成时临时添加到前端 state，刷新页面后 state 重置，数据丢失。

```javascript
// app.js - toggleComments 函数
async function toggleComments(post) {
  const postId = post.id;
  if (state.expandedPosts[postId]) {
    state.expandedPosts[postId] = false;
  } else {
    state.expandedPosts[postId] = true;
    if (!state.postComments[postId]) {
      loadComments(postId); // 加载评论
    }
    // 缺少 loadAiReplies(postId) 调用
  }
}
```

**解决方案**：
在展开帖子时同时检查并加载 AI 回复：

```javascript
async function toggleComments(post) {
  const postId = post.id;
  if (state.expandedPosts[postId]) {
    state.expandedPosts[postId] = false;
  } else {
    state.expandedPosts[postId] = true;
    if (!state.postComments[postId]) {
      loadComments(postId);
    }
    if (!state.aiReplies[postId]) {
      // 新增
      loadAiReplies(postId);
    }
  }
}
```

**涉及文件**：

- `src/main/resources/static/js/app.js`

---

## 经验总结

1. **MyBatis 参数问题**：XML SQL 中使用的参数必须在 Mapper 接口方法中存在，否则会抛出 `BindingException`
2. **前后端字段命名**：前后端字段名应保持一致，注意驼峰命名差异（userName vs username）
3. **请求数据格式**：`@RequestBody` 用于 JSON，`@ModelAttribute` 用于表单数据，注解选择应与前端请求格式匹配
4. **Session 属性名一致性**：整个项目应统一使用同一个 Session 属性名存储登录用户，建议抽取为常量
5. **新文件遗漏检查**：创建新的 Mapper/Component 等Spring管理的类时，检查是否添加了必要注解（@Mapper/@Component/@Service等）
