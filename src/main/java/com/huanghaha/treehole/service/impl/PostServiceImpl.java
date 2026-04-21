package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.common.ForbiddenWordUtil;
import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.exception.BusinessException;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

    private static final int MAX_CONTENT_LENGTH = 200;
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;
    private static final long CACHE_TTL_MINUTES = 5;
    private static final String CACHE_KEY_PREFIX = "post:page:";

    @Resource
    private PostMapper postMapper;

    @Resource
    private ForbiddenWordUtil forbiddenWordUtil;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(Long userId, String content) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("内容不能为空");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException("内容不能超过" + MAX_CONTENT_LENGTH + "字");
        }

        forbiddenWordUtil.check(content);

        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setCreateTime(LocalDateTime.now());

        log.info("发布帖子：用户ID={}", userId);
        postMapper.insert(post);
        clearPageCache();
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

        String cacheKey = CACHE_KEY_PREFIX + page + ":" + size;

        try {
            Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("命中缓存：page={}, size={}", page, size);
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis连接失败，跳过缓存读取，降级为数据库查询：{}", e.getMessage());
        }

        int offset = (page - 1) * size;
        List<Post> list = postMapper.findByPage(offset, size);
        Long total = postMapper.count();

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        try {
            redisTemplate.opsForValue().set(cacheKey, result, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis连接失败，跳过缓存写入：{}", e.getMessage());
        }
        log.info("分页查询帖子：page={}, size={}, total={}", page, size, total);
        return result;
    }

    @Override
    public void delete(Long postId, Long userId) {
        if (postId == null || userId == null) {
            throw new BusinessException("帖子ID或用户ID不能为空");
        }

        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除");
        }

        log.info("删除帖子：帖子ID={}, 用户ID={}", postId, userId);
        postMapper.deleteById(postId);
        clearPageCache();
    }

    private void clearPageCache() {
        try {
            Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("清除帖子分页缓存，共{}个key", keys.size());
            }
        } catch (Exception e) {
            log.warn("Redis连接失败，跳过缓存清除：{}", e.getMessage());
        }
    }
}
