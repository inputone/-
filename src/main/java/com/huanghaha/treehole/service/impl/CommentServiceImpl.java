package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.Comment;
import com.huanghaha.treehole.mapper.CommentMapper;
import com.huanghaha.treehole.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优化校验 + 常量 + 日志
 */

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    // 抽取常量，便于维护
    private static final int MAX_CONTENT_LENGTH = 300;

    @Resource
    private CommentMapper commentMapper;

    @Override
    public void publish(Long userId, Long postId, String content) {
        // 二次校验（防止绕过Controller直接调用Service）
        if (userId == null || postId == null) {
            throw new RuntimeException("用户ID或帖子ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("评论不能为空");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new RuntimeException("评论不能超过" + MAX_CONTENT_LENGTH + "字");
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now()); // 手动填充时间（若无MyBatis-Plus）

        log.info("发布评论：用户ID={}, 帖子ID={}, 内容={}", userId, postId, content);
        commentMapper.insert(comment);
    }

    @Override
    public List<Comment> listByPost(Long postId) {
        if (postId == null) {
            throw new RuntimeException("帖子ID不能为空");
        }

        log.info("查询帖子评论：帖子ID={}", postId);
        return commentMapper.findByPostId(postId);
    }

    @Override
    public void delete(Long commentId, Long userId) {
        // 前置校验，避免空指针
        if (commentId == null || userId == null) {
            throw new RuntimeException("评论ID或用户ID不能为空");
        }

        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除");
        }

        log.info("删除评论：评论ID={}, 用户ID={}", commentId, userId);
        commentMapper.deleteById(commentId);
    }
}