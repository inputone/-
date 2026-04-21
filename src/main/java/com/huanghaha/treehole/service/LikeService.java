package com.huanghaha.treehole.service;

/**
 * 点赞服务接口
 * 采用 toggle 模式：已点赞则取消，未点赞则点赞
 */
public interface LikeService {

    /**
     * 切换点赞状态（toggle 模式）
     *
     * @param userId 用户ID
     * @param postId 帖子ID
     */
    void toggleLike(Long userId, Long postId);

    /**
     * 查询用户是否已点赞某帖子
     *
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true-已点赞，false-未点赞
     */
    boolean isLiked(Long userId, Long postId);
}
