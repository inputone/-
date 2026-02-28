package com.huanghaha.treehole.service;

import com.huanghaha.treehole.entity.Post;

import java.util.List;

public interface PostService {

    void publish(Long userId, String content);

    List<Post> listAll();

    void delete(Long postId, Long userId);
}