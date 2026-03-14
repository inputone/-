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
import java.util.Map;

@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Resource
    private PostService postService;

    @PostMapping("/publish")
    public Result<String> publish(@RequestParam String content, HttpSession session) {
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

    @GetMapping("/list")
    public Result<List<Post>> list() {
        List<Post> postList = postService.listAll();
        return Result.success(postList);
    }

    @GetMapping("/page")
    public Result<Map<String, Object>> page(@RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer size) {
        Map<String, Object> result = postService.listByPage(page, size);
        return Result.success(result);
    }

    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam Long id, HttpSession session) {
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
