package com.huanghaha.treehole;

import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    /**
     * 测试注册成功
     */
    @Test
    public void testRegisterSuccess() {
        String username = "testUser1";
        String password = "123456";

        assertDoesNotThrow(() -> {
            userService.register(username, password);
        });
    }

    /**
     * 测试用户名为空
     */
    @Test
    public void testRegisterEmptyUsername() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.register("", "123456");
        });

        assertEquals("用户名不能为空", exception.getMessage());
    }

    /**
     * 测试密码过短
     */
    @Test
    public void testRegisterShortPassword() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.register("user2", "123");
        });

        assertEquals("密码至少6位", exception.getMessage());
    }

    /**
     * 测试登录失败：用户不存在
     */
    @Test
    public void testLoginUserNotExist() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login("notExistUser", "123456");
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试登录成功（需要数据库中已有 status=1 的用户）
     */
    @Test
    public void testLoginSuccess() {

        // 先注册
        String username = "loginUser1";
        String password = "123456";

        try {
            userService.register(username, password);
        } catch (Exception ignored) {}

        // 手动把数据库 status 改成 1
        // 你可以提前在数据库执行：
        // UPDATE user SET status = 1 WHERE username = 'loginUser1';

        User user = userService.login(username, password);

        assertNotNull(user);
        assertEquals(username, user.getUsername());
    }
}