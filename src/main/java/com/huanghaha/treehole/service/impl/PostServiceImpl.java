package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.common.ForbiddenWordUtil;
import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

    private static final int MAX_CONTENT_LENGTH = 200;
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    @Resource
    private PostMapper postMapper;

    @Resource
    private ForbiddenWordUtil forbiddenWordUtil;

    @Override
    public void publish(Long userId, String content) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("内容不能为空");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new RuntimeException("内容不能超过" + MAX_CONTENT_LENGTH + "字");
        }

        forbiddenWordUtil.check(content);

        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setCreateTime(LocalDateTime.now());

        log.info("发布帖子：用户ID={}, 内容={}", userId, content);
        postMapper.insert(post);
    }

    @Override
    public List<Post> listAll() {
        log.info("查询所有帖子");
        return postMapper.findAll();
    }

    @Override
    public Map<String, Object> listByPage(Integer page, Integer size) {
        if (page == null || page < 1) {
            page = DEFAULT_PAGE;
        }
        if (size == null || size < 1) {
            size = DEFAULT_SIZE;
        }
        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        int offset = (page - 1) * size;
        List<Post> list = postMapper.findByPage(offset, size);
        Long total = postMapper.count();

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        log.info("分页查询帖子：page={}, size={}, total={}", page, size, total);
        return result;
    }

    @Override
    public void delete(Long postId, Long userId) {
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
