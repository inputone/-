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

/**
 * 点赞服务实现
 * toggle 模式：查询是否已点赞 → 已点赞则取消（删除记录+计数-1），未点赞则新增（插入记录+计数+1）
 * 使用 @Transactional 保证关联表操作和冗余计数字段更新的一致性
 */
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

        // 校验帖子存在性
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        PostLike existing = postLikeMapper.findByUserIdAndPostId(userId, postId);
        if (existing != null) {
            // 已点赞 → 取消点赞：删除记录 + 计数-1
            log.info("取消点赞：用户ID={}, 帖子ID={}", userId, postId);
            postLikeMapper.deleteById(existing.getId());
            postMapper.decrementLikeCount(postId);
        } else {
            // 未点赞 → 新增点赞：插入记录 + 计数+1
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
