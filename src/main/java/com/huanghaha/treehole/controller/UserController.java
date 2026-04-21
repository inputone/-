package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestParam String username,
                                   @RequestParam String password) {
        userService.register(username, password);
        return Result.success("注册成功，等待审核");
    }

    @PostMapping("/login")
    public Result<String> login(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session) {
        User user = userService.login(username, password);
        session.setAttribute("loginUser", user);
        return Result.success("登录成功");
    }

    @GetMapping("/me")
    public Result<User> me(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return Result.error("请先登录");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @GetMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        log.info("用户退出登录，会话已失效");
        return Result.success("已退出");
    }
}
