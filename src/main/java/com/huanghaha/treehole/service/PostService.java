package com.huanghaha.treehole.service;

import com.huanghaha.treehole.entity.Post;

import java.util.List;
import java.util.Map;

public interface PostService {

    void publish(Long userId, String content);

    List<Post> listAll();

    Map<String, Object> listByPage(Integer page, Integer size);

    void delete(Long postId, Long userId);
}