package com.huanghaha.treehole;

import com.huanghaha.treehole.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TreeholeApplicationTests {
    @Resource
    private UserService userService;

    @Test
    public void testRegisterSuccess() {
        userService.register("abc123", "123456");
    }
}
