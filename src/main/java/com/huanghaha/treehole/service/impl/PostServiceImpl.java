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

/**
 * 帖子服务实现
 * 核心逻辑：
 * - 发布：参数校验 → 敏感词检查 → 入库 → 清除分页缓存
 * - 分页查询：优先从 Redis 缓存读取，缓存未命中则查数据库并写入缓存（TTL 5分钟）
 * - Redis 容错：所有 Redis 操作 try-catch 包裹，连接失败时降级为数据库查询
 * - 删除：仅允许删除自己的帖子，逻辑删除后清除分页缓存
 */
@Service
@Slf4j
public class PostServiceImpl implements PostService {

    /** 帖子内容最大长度 */
    private static final int MAX_CONTENT_LENGTH = 200;
    /** 默认页码 */
    private static final int DEFAULT_PAGE = 1;
    /** 默认每页条数 */
    private static final int DEFAULT_SIZE = 10;
    /** 每页最大条数 */
    private static final int MAX_SIZE = 50;
    /** 缓存过期时间（分钟） */
    private static final long CACHE_TTL_MINUTES = 5;
    /** 缓存 Key 前缀，格式：post:page:{page}:{size} */
    private static final String CACHE_KEY_PREFIX = "post:page:";

    @Resource
    private PostMapper postMapper;

    @Resource
    private ForbiddenWordUtil forbiddenWordUtil;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(Long userId, String content) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("内容不能为空");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException("内容不能超过" + MAX_CONTENT_LENGTH + "字");
        }

        // 敏感词检查
        forbiddenWordUtil.check(content);

        // 入库并清除分页缓存
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
        // 分页参数校验与默认值处理
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

        // 尝试从 Redis 缓存读取
        try {
            Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("命中缓存：page={}, size={}", page, size);
                return cached;
            }
        } catch (Exception e) {
            // Redis 连接失败，降级为数据库查询
            log.warn("Redis连接失败，跳过缓存读取，降级为数据库查询：{}", e.getMessage());
        }

        // 缓存未命中，查询数据库
        int offset = (page - 1) * size;
        List<Post> list = postMapper.findByPage(offset, size);
        Long total = postMapper.count();

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        // 将查询结果写入 Redis 缓存
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
        // 校验帖子存在性及删除权限
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

    /**
     * 清除所有帖子分页缓存
     * 发布/删除帖子后调用，Redis 不可用时静默跳过
     */
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
