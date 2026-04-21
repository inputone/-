package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.entity.PostFavorite;
import com.huanghaha.treehole.exception.BusinessException;
import com.huanghaha.treehole.mapper.PostFavoriteMapper;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏服务实现
 * toggle 模式：查询是否已收藏 → 已收藏则取消（删除记录+计数-1），未收藏则新增（插入记录+计数+1）
 * 使用 @Transactional 保证关联表操作和冗余计数字段更新的一致性
 */
@Service
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    private PostFavoriteMapper postFavoriteMapper;

    @Resource
    private PostMapper postMapper;

    @Override
    @Transactional
    public void toggleFavorite(Long userId, Long postId) {
        if (userId == null || postId == null) {
            throw new BusinessException("用户ID或帖子ID不能为空");
        }

        // 校验帖子存在性
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        PostFavorite existing = postFavoriteMapper.findByUserIdAndPostId(userId, postId);
        if (existing != null) {
            // 已收藏 → 取消收藏：删除记录 + 计数-1
            log.info("取消收藏：用户ID={}, 帖子ID={}", userId, postId);
            postFavoriteMapper.deleteById(existing.getId());
            postMapper.decrementFavoriteCount(postId);
        } else {
            // 未收藏 → 新增收藏：插入记录 + 计数+1
            log.info("收藏：用户ID={}, 帖子ID={}", userId, postId);
            PostFavorite postFavorite = new PostFavorite();
            postFavorite.setUserId(userId);
            postFavorite.setPostId(postId);
            postFavorite.setCreateTime(LocalDateTime.now());
            postFavoriteMapper.insert(postFavorite);
            postMapper.incrementFavoriteCount(postId);
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long postId) {
        if (userId == null || postId == null) {
            return false;
        }
        return postFavoriteMapper.findByUserIdAndPostId(userId, postId) != null;
    }

    @Override
    public List<Post> listByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 查询用户收藏记录，再逐条查询关联帖子
        List<PostFavorite> favorites = postFavoriteMapper.findByUserId(userId);
        List<Post> posts = new ArrayList<>();
        for (PostFavorite favorite : favorites) {
            Post post = postMapper.findById(favorite.getPostId());
            if (post != null) {
                posts.add(post);
            }
        }

        log.info("查询用户收藏列表：用户ID={}, 数量={}", userId, posts.size());
        return posts;
    }
}
