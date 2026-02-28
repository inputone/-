package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.Comment;
import com.huanghaha.treehole.mapper.CommentMapper;
import com.huanghaha.treehole.service.CommentService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Override
    public void publish(Long userId, Long postId, String content) {

        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("评论不能为空");
        }

        if (content.length() > 300) {
            throw new RuntimeException("评论不能超过300字");
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setContent(content);

        commentMapper.insert(comment);
    }

    @Override
    public List<Comment> listByPost(Long postId) {

        if (postId == null) {
            throw new RuntimeException("帖子ID不能为空");
        }

        return commentMapper.findByPostId(postId);
    }

    @Override
    public void delete(Long commentId, Long userId) {

        Comment comment = commentMapper.findById(commentId);

        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除");
        }

        commentMapper.deleteById(commentId);
    }

    }