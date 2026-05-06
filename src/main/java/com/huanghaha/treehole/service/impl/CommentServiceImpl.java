package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.util.ForbiddenWordUtil;
import com.huanghaha.treehole.entity.Comment;
import com.huanghaha.treehole.common.BusinessException;
import com.huanghaha.treehole.mapper.CommentMapper;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论服务实现
 * 发布：参数校验 → 敏感词检查 → 入库
 * 删除：仅允许删除自己的评论，逻辑删除
 */
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    /** 评论内容最大长度 */
    private static final int MAX_CONTENT_LENGTH = 200;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private ForbiddenWordUtil forbiddenWordUtil;

    @Override
    public void publish(Long userId, Long postId, String content) {
        // 参数校验
        if (userId == null || postId == null) {
            throw new BusinessException("用户ID或帖子ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("评论不能为空");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException("评论不能超过" + MAX_CONTENT_LENGTH + "字");
        }

        // 敏感词检查
        forbiddenWordUtil.check(content);

        // 入库
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now());

        log.info("发布评论：用户ID={}, 帖子ID={}", userId, postId);
        commentMapper.insert(comment);
        postMapper.incrementCommentCount(postId);
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
        // 校验评论存在性及删除权限
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
        postMapper.decrementCommentCount(comment.getPostId());
    }
}
