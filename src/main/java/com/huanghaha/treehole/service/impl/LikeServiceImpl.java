package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.entity.PostLike;
import com.huanghaha.treehole.exception.BusinessException;
import com.huanghaha.treehole.mapper.PostLikeMapper;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

@Service
@Slf4j
public class LikeServiceImpl implements LikeService {

    @Resource
    private PostLikeMapper postLikeMapper;

    @Resource
    private PostMapper postMapper;

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

        PostLike existing = postLikeMapper.findByUserIdAndPostId(userId, postId);
        if (existing != null) {
            log.info("取消点赞：用户ID={}, 帖子ID={}", userId, postId);
            postLikeMapper.deleteById(existing.getId());
            postMapper.decrementLikeCount(postId);
        } else {
            log.info("点赞：用户ID={}, 帖子ID={}", userId, postId);
            PostLike postLike = new PostLike();
            postLike.setUserId(userId);
            postLike.setPostId(postId);
            postLike.setCreateTime(LocalDateTime.now());
            postLikeMapper.insert(postLike);
            postMapper.incrementLikeCount(postId);
        }
    }

    @Override
    public boolean isLiked(Long userId, Long postId) {
        if (userId == null || postId == null) {
            return false;
        }
        return postLikeMapper.findByUserIdAndPostId(userId, postId) != null;
    }
}
