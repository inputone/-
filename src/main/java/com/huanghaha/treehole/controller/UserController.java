package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(String username, String password) {
        try {
            userService.register(username, password);
            return "注册成功，等待审核";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/login")
    public String login(String username, String password, HttpSession session) {
        try {
            User user = userService.login(username, password);
            session.setAttribute("loginUser", user);
            return "登录成功";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/me")
    public Object me(HttpSession session) {
        return session.getAttribute("loginUser");
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "已退出";
    }
}