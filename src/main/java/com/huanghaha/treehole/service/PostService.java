package com.huanghaha.treehole.service;

import com.huanghaha.treehole.entity.Post;

import java.util.List;
import java.util.Map;

/**
 * 帖子服务接口
 * 提供帖子发布、查询、删除功能，分页查询支持 Redis 缓存
 */
public interface PostService {

    /**
     * 发布帖子，内容上限 500 字，发布后清除分页缓存
     *
     * @param userId  发布者ID
     * @param content 帖子内容
     */
    void publish(Long userId, String content);

    /** 查询所有帖子（排除已删除） */
    List<Post> listAll();

    /**
     * 分页查询帖子，支持 Redis 缓存（TTL 5分钟），Redis 不可用时降级为数据库查询
     *
     * @param page 页码（默认1）
     * @param size 每页条数（默认10，最大50）
     * @return 包含 list/total/page/size 的分页结果
     */
    Map<String, Object> listByPage(Integer page, Integer size);

    /**
     * 删除帖子（仅允许删除自己的帖子），逻辑删除
     *
     * @param postId 帖子ID
     * @param userId 当前用户ID
     */
    void delete(Long postId, Long userId);
}