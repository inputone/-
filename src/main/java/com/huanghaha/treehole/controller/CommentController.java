package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.entity.Comment;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.CommentService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 发布评论
     */
    @PostMapping
    public String publish(Long postId, String content, HttpSession session) {

        User user = (User) session.getAttribute("loginUser");

        if (user == null) {
            return "请先登录";
        }

        commentService.publish(user.getId(), postId, content);

        return "评论成功";
    }

    /**
     * 查看某个帖子的评论
     */
    @GetMapping("/post/{postId}")
    public List<Comment> list(@PathVariable Long postId) {
        return commentService.listByPost(postId);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("loginUser");

        if (user == null) {
            return "请先登录";
        }

        commentService.delete(id, user.getId());

        return "删除成功";
    }
}