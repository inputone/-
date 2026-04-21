package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostLikeMapper {

    void insert(PostLike postLike);

    PostLike findByUserIdAndPostId(Long userId, Long postId);

    void deleteById(Long id);
}
