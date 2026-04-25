package com.huanghaha.treehole.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子点赞数据访问层
 * 对应 PostLikeMapper.xml，操作 post_like 表
 * uk_user_post 唯一索引保证同一用户对同一帖子只能有一条点赞记录
 */
@Mapper
public interface PostLikeMapper {

    /** 切换点赞状态（toggle 模式） */
    void toggleLike(Long userId, Long postId);

    /** 查询用户是否点赞某帖子 */
    Long findByUserIdAndPostId(Long userId, Long postId);

    /** 统计帖子点赞数 */
    Integer countByPostId(Long postId);
}
