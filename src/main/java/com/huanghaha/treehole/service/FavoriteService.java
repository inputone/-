package com.huanghaha.treehole.service;

import com.huanghaha.treehole.entity.Post;

import java.util.List;

public interface FavoriteService {

    void toggleFavorite(Long userId, Long postId);

    boolean isFavorited(Long userId, Long postId);

    List<Post> listByUserId(Long userId);
}
