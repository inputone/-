package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 帖子数据访问层
 * 对应 PostMapper.xml，操作 post 表
 * 增删改查 + 点赞/收藏计数的原子更新
 */
@Mapper
public interface PostMapper {

    /** 新增帖子 */
    void insert(Post post);

    /** 查询所有帖子（排除已删除） */
    List<Post> findAll();

    /** 分页查询帖子（基于 offset） */
    List<Post> findByPage(Integer offset, Integer pageSize);

    /** 查询帖子总数（排除已删除） */
    Long count();

    /** 根据ID查询帖子 */
    Post findById(Long id);

    /** 逻辑删除帖子 */
    void deleteById(Long id);

    /** 点赞数 +1 */
    void incrementLikeCount(Long id);

    /** 点赞数 -1（GREATEST(count-1, 0) 防止负数） */
    void decrementLikeCount(Long id);

    /** 收藏数 +1 */
    void incrementFavoriteCount(Long id);

    /** 收藏数 -1（GREATEST(count-1, 0) 防止负数） */
    void decrementFavoriteCount(Long id);

    /** 评论数 +1 */
    void incrementCommentCount(Long id);

    /** 评论数 -1（GREATEST(count-1, 0) 防止负数） */
    void decrementCommentCount(Long id);
}