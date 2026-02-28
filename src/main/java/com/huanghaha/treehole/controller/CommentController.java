package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.Comment;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.CommentService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 发布评论
     */
    @PostMapping
    public Result<String> publish(@RequestParam Long postId,
                                  @RequestParam String content,
                                  HttpSession session) {
        // 前置参数校验
        if (postId == null) {
            return Result.error("帖子ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }

        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        commentService.publish(user.getId(), postId, content);
        return Result.success("评论成功");
    }

    /**
     * 查看某个帖子的评论
     */
    @GetMapping("/post/{postId}")
    public Result<List<Comment>> list(@PathVariable Long postId) {
        List<Comment> commentList = commentService.listByPost(postId);
        return Result.success(commentList);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpSession session) {
        // 前置参数校验
        if (id == null) {
            return Result.error("评论ID不能为空");
        }

        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return Result.error("请先登录");
        }

        commentService.delete(id, user.getId());
        return Result.success("删除成功");
    }
}