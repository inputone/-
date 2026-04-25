package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.exception.BusinessException;
import com.huanghaha.treehole.mapper.PostFavoriteMapper;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏服务实现
 * 采用 toggle 模式：已收藏则取消，未收藏则收藏
 * 同时维护 Post 表的冗余计数字段 favorite_count
 */
@Service
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    private PostFavoriteMapper postFavoriteMapper;

    @Resource
    private PostMapper postMapper;

    /**
     * 切换收藏状态
     * 流程：检查是否已收藏 → toggle → 更新帖子收藏计数（+1 或 -1）
     */
    @Override
    @Transactional
    public void toggleFavorite(Long userId, Long postId) {
        if (userId == null || postId == null) {
            throw new BusinessException("用户ID或帖子ID不能为空");
        }

        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        boolean wasFavorited = postFavoriteMapper.findByUserIdAndPostId(userId, postId) != null;

        postFavoriteMapper.toggleFavorite(userId, postId);

        if (wasFavorited) {
            postMapper.decrementFavoriteCount(postId);
            log.info("取消收藏：用户ID={}, 帖子ID={}", userId, postId);
        } else {
            postMapper.incrementFavoriteCount(postId);
            log.info("收藏：用户ID={}, 帖子ID={}", userId, postId);
        }
    }

    /** 查询用户是否已收藏某帖子 */
    @Override
    public boolean isFavorited(Long userId, Long postId) {
        if (userId == null || postId == null) {
            return false;
        }
        return postFavoriteMapper.findByUserIdAndPostId(userId, postId) != null;
    }

    /** 查询用户收藏的帖子列表 */
    @Override
    public List<Post> listByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        List<Long> postIds = postFavoriteMapper.findByUserId(userId);
        List<Post> posts = new ArrayList<>();
        for (Long postId : postIds) {
            Post post = postMapper.findById(postId);
            if (post != null) {
                posts.add(post);
            }
        }

        log.info("查询用户收藏列表：用户ID={}, 数量={}", userId, posts.size());
        return posts;
    }
}