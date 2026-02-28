package com.huanghaha.treehole.service;
import com.huanghaha.treehole.entity.Comment;
import java.util.List;

public interface CommentService {

    void publish(Long userId, Long postId, String content);

    List<Comment> listByPost(Long postId);

    void delete(Long commentId, Long userId);
}