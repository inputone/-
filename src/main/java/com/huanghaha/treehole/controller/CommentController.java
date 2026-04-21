package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.Comment;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 * 提供评论发布、按帖子查询评论、删除评论接口
 * 发布和删除需登录（由 LoginInterceptor 拦截）
 */
@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    /** 发布评论（需登录） */
    @PostMapping
    public Result<String> publish(@RequestParam Long postId,
            @RequestParam String content,
            HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        commentService.publish(user.getId(), postId, content);
        return Result.success("评论成功");
    }

    /** 根据帖子ID查询评论列表（无需登录） */
    @GetMapping("/post/{postId}")
    public Result<List<Comment>> list(@PathVariable Long postId) {
        List<Comment> commentList = commentService.listByPost(postId);
        return Result.success(commentList);
    }

    /** 删除自己的评论（需登录） */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        commentService.delete(id, user.getId());
        return Result.success("删除成功");
    }
}
