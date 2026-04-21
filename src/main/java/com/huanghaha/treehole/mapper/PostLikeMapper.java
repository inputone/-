package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子点赞数据访问层
 * 对应 PostLikeMapper.xml，操作 post_like 表
 * uk_user_post 唯一索引保证同一用户对同一帖子只能有一条点赞记录
 */
@Mapper
public interface PostLikeMapper {

    /** 新增点赞记录 */
    void insert(PostLike postLike);

    /** 根据用户ID和帖子ID查询点赞记录（用于判断是否已点赞） */
    PostLike findByUserIdAndPostId(Long userId, Long postId);

    /** 逻辑删除点赞记录 */
    void deleteById(Long id);
}
