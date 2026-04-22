package com.huanghaha.treehole.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostFavoriteMapper {

    void toggleFavorite(Long userId, Long postId);

    Long findByUserIdAndPostId(Long userId, Long postId);

    List<Long> findByUserId(Long userId);

    Integer countByPostId(Long postId);
}
