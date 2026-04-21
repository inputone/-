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

    @PostMapping
    public Result<String> publish(@RequestParam Long postId,
                                  @RequestParam String content,
                                  HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        commentService.publish(user.getId(), postId, content);
        return Result.success("评论成功");
    }

    @GetMapping("/post/{postId}")
    public Result<List<Comment>> list(@PathVariable Long postId) {
        List<Comment> commentList = commentService.listByPost(postId);
        return Result.success(commentList);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        commentService.delete(id, user.getId());
        return Result.success("删除成功");
    }
}
