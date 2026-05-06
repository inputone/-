package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.common.BusinessException;
import com.huanghaha.treehole.mapper.PostLikeMapper;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * 点赞服务实现
 * 采用 toggle 模式：已点赞则取消，未点赞则点赞
 * 同时维护 Post 表的冗余计数字段 like_count
 */
@Service
@Slf4j
public class LikeServiceImpl implements LikeService {

    @Resource
    private PostLikeMapper postLikeMapper;

    @Resource
    private PostMapper postMapper;

    /**
     * 切换点赞状态
     * 流程：检查是否已点赞 → toggle → 更新帖子点赞计数（+1 或 -1）
     */
    @Override
    @Transactional
    public void toggleLike(Long userId, Long postId) {
        if (userId == null || postId == null) {
            throw new BusinessException("用户ID或帖子ID不能为空");
        }

        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        boolean wasLiked = postLikeMapper.findByUserIdAndPostId(userId, postId) != null;

        postLikeMapper.toggleLike(userId, postId);

        if (wasLiked) {
            postMapper.decrementLikeCount(postId);
            log.info("取消点赞：用户ID={}, 帖子ID={}", userId, postId);
        } else {
            postMapper.incrementLikeCount(postId);
            log.info("点赞：用户ID={}, 帖子ID={}", userId, postId);
        }
    }

    /** 查询用户是否已点赞某帖子 */
    @Override
    public boolean isLiked(Long userId, Long postId) {
        if (userId == null || postId == null) {
            return false;
        }
        return postLikeMapper.findByUserIdAndPostId(userId, postId) != null;
    }
}