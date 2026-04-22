package com.huanghaha.treehole.service;

import com.huanghaha.treehole.entity.Comment;
import java.util.List;

/**
 * 评论服务接口
 * 提供评论发布、查询、删除功能
 */
public interface CommentService {

    /**
     * 发布评论，内容上限 500 字
     *
     * @param userId  评论者ID
     * @param postId  帖子ID
     * @param content 评论内容
     */
    void publish(Long userId, Long postId, String content);

    /** 根据帖子ID查询评论列表 */
    List<Comment> listByPost(Long postId);

    /**
     * 删除评论（仅允许删除自己的评论），逻辑删除
     *
     * @param commentId 评论ID
     * @param userId    当前用户ID
     */
    void delete(Long commentId, Long userId);
}