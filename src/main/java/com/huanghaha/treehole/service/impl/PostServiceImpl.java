package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j // 新增日志注解
public class PostServiceImpl implements PostService {

    // 抽取常量，便于维护
    private static final int MAX_CONTENT_LENGTH = 200;

    @Resource
    private PostMapper postMapper;

    @Override
    public void publish(Long userId, String content) {
        // 二次校验（防止绕过Controller调用）
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("内容不能为空");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new RuntimeException("内容不能超过" + MAX_CONTENT_LENGTH + "字");
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setCreateTime(LocalDateTime.now()); // 手动填充创建时间

        log.info("发布帖子：用户ID={}, 内容={}", userId, content);
        postMapper.insert(post);
    }

    @Override
    public List<Post> listAll() {
        log.info("查询所有帖子");
        return postMapper.findAll();
    }

    @Override
    public void delete(Long postId, Long userId) {
        // 前置校验，避免空指针
        if (postId == null || userId == null) {
            throw new RuntimeException("帖子ID或用户ID不能为空");
        }

        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除");
        }

        log.info("删除帖子：帖子ID={}, 用户ID={}", postId, userId);
        postMapper.deleteById(postId);
    }
}