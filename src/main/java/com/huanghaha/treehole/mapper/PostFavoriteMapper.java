package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.PostFavorite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostFavoriteMapper {

    void insert(PostFavorite postFavorite);

    PostFavorite findByUserIdAndPostId(Long userId, Long postId);

    void deleteById(Long id);

    List<PostFavorite> findByUserId(Long userId);
}
