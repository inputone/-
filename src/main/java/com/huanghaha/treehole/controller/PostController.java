package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/post")
public class PostController {

    @Resource
    private PostService postService;

    @PostMapping("/publish")
    public String publish(String content, HttpSession session) {

        User user = (User) session.getAttribute("loginUser");

        if (user == null) {
            return "请先登录";
        }

        try {
            postService.publish(user.getId(), content);
            return "发布成功";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/list")
    public Object list() {
        return postService.listAll();
    }

    @DeleteMapping("/delete")
    public String delete(Long id, HttpSession session) {

        User user = (User) session.getAttribute("loginUser");

        if (user == null) {
            return "请先登录";
        }

        try {
            postService.delete(id, user.getId());
            return "删除成功";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}