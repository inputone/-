package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.common.ForbiddenWordUtil;
import com.huanghaha.treehole.entity.Comment;
import com.huanghaha.treehole.exception.BusinessException;
import com.huanghaha.treehole.mapper.CommentMapper;
import com.huanghaha.treehole.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private static final int MAX_CONTENT_LENGTH = 300;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ForbiddenWordUtil forbiddenWordUtil;

    @Override
    public void publish(Long userId, Long postId, String content) {
        if (userId == null || postId == null) {
            throw new BusinessException("用户ID或帖子ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("评论不能为空");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException("评论不能超过" + MAX_CONTENT_LENGTH + "字");
        }

        forbiddenWordUtil.check(content);

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now());

        log.info("发布评论：用户ID={}, 帖子ID={}", userId, postId);
        commentMapper.insert(comment);
    }

    @Override
    public List<Comment> listByPost(Long postId) {
        if (postId == null) {
            throw new BusinessException("帖子ID不能为空");
        }

        log.info("查询帖子评论：帖子ID={}", postId);
        return commentMapper.findByPostId(postId);
    }

    @Override
    public void delete(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            throw new BusinessException("评论ID或用户ID不能为空");
        }

        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除");
        }

        log.info("删除评论：评论ID={}, 用户ID={}", commentId, userId);
        commentMapper.deleteById(commentId);
    }
}
