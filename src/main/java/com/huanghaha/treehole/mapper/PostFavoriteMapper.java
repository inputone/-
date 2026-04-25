package com.huanghaha.treehole.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 帖子收藏数据访问层
 * 对应 PostFavoriteMapper.xml，操作 post_favorite 表
 * uk_user_post 唯一索引保证同一用户对同一帖子只能有一条收藏记录
 */
@Mapper
public interface PostFavoriteMapper {

    /** 切换收藏状态（toggle 模式） */
    void toggleFavorite(Long userId, Long postId);

    /** 查询用户是否收藏某帖子 */
    Long findByUserIdAndPostId(Long userId, Long postId);

    /** 查询用户的收藏列表 */
    List<Long> findByUserId(Long userId);

    /** 统计帖子收藏数 */
    Integer countByPostId(Long postId);
}
