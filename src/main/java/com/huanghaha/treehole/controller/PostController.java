package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.PostService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统一 Result + 参数校验 + 日志 + 移除手动 try-catch
 */
@RestController
@RequestMapping("/post")
@Slf4j // 新增日志注解
public class PostController {

    @Resource
    private PostService postService;

    /**
     * 发布帖子
     */
    @PostMapping("/publish")
    public Result<String> publish(@RequestParam String content, HttpSession session) {
        // 前置参数校验
        if (content == null || content.trim().isEmpty()) {
            return Result.error("帖子内容不能为空");
        }

        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        postService.publish(user.getId(), content);
        return Result.success("发布成功");
    }

    /**
     * 查询所有帖子
     */
    @GetMapping("/list")
    public Result<List<Post>> list() {
        List<Post> postList = postService.listAll();
        return Result.success(postList);
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam Long id, HttpSession session) {
        // 前置参数校验
        if (id == null) {
            return Result.error("帖子ID不能为空");
        }

        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        postService.delete(id, user.getId());
        return Result.success("删除成功");
    }
}