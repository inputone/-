package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 提供注册、登录、获取当前用户信息、退出登录接口
 * 所有接口无需登录拦截（由 WebConfig 放行）
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /** 注册，注册后默认待审核状态 */
    @PostMapping("/register")
    public Result<String> register(@RequestParam String username,
            @RequestParam String password) {
        userService.register(username, password);
        return Result.success("注册成功，等待审核");
    }

    /** 登录，校验通过后将用户信息存入 Session */
    @PostMapping("/login")
    public Result<String> login(@RequestParam String username,
            @RequestParam String password,
            HttpSession session) {
        User user = userService.login(username, password);
        session.setAttribute("loginUser", user);
        return Result.success("登录成功");
    }

    /** 获取当前登录用户信息，返回前脱敏密码 */
    @GetMapping("/me")
    public Result<User> me(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return Result.error("请先登录");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /** 退出登录，销毁 Session */
    @GetMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        log.info("用户退出登录，会话已失效");
        return Result.success("已退出");
    }
}
