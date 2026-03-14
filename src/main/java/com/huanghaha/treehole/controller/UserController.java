package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.mapper.UserMapper;
import com.huanghaha.treehole.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统一 Result + 参数校验 + 日志 + 移除 try-catch
 */
@RestController
@RequestMapping("/user")
@Slf4j // 新增日志注解
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestParam String username,
                                   @RequestParam String password) {
        // 前置参数校验
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            return Result.error("密码至少6位");
        }

        userService.register(username, password);
        return Result.success("注册成功，等待审核");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<String> login(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session) {
        // 前置参数校验
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        User user = userService.login(username, password);
        session.setAttribute("loginUser", user);
        return Result.success("登录成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public Result<User> me(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return Result.error("请先登录");
        }
        // 隐藏密码，避免敏感信息泄露
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 用户退出登录
     */
    @GetMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        log.info("用户退出登录，会话已失效");
        return Result.success("已退出");
    }
}