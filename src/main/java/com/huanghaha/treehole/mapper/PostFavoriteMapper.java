package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.PostFavorite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 帖子收藏数据访问层
 * 对应 PostFavoriteMapper.xml，操作 post_favorite 表
 * uk_user_post 唯一索引保证同一用户对同一帖子只能有一条收藏记录
 */
@Mapper
public interface PostFavoriteMapper {

    /** 新增收藏记录 */
    void insert(PostFavorite postFavorite);

    /** 根据用户ID和帖子ID查询收藏记录（用于判断是否已收藏） */
    PostFavorite findByUserIdAndPostId(Long userId, Long postId);

    /** 逻辑删除收藏记录 */
    void deleteById(Long id);

    /** 根据用户ID查询其所有收藏记录 */
    List<PostFavorite> findByUserId(Long userId);
}
