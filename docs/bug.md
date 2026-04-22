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
     const data = await api("POST", "/admin/auditUser", `username=${username}&status=${status}`);
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

## 经验总结

1. **MyBatis 参数问题**：XML SQL 中使用的参数必须在 Mapper 接口方法中存在，否则会抛出 `BindingException`
2. **前后端字段命名**：前后端字段名应保持一致，注意驼峰命名差异（userName vs username）
3. **请求数据格式**：`@RequestBody` 用于 JSON，`@ModelAttribute` 用于表单数据，注解选择应与前端请求格式匹配
