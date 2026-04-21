package com.huanghaha.treehole.service;

import com.huanghaha.treehole.entity.Post;

import java.util.List;

/**
 * 收藏服务接口
 * 采用 toggle 模式：已收藏则取消，未收藏则收藏
 */
public interface FavoriteService {

    /**
     * 切换收藏状态（toggle 模式）
     *
     * @param userId 用户ID
     * @param postId 帖子ID
     */
    void toggleFavorite(Long userId, Long postId);

    /**
     * 查询用户是否已收藏某帖子
     *
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true-已收藏，false-未收藏
     */
    boolean isFavorited(Long userId, Long postId);

    /** 查询用户收藏的帖子列表 */
    List<Post> listByUserId(Long userId);
}
