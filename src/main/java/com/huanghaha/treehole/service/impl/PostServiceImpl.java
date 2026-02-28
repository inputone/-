package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.service.PostService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Override
    public void publish(Long userId,String content) {

        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("内容不能为空");
        }

        if (content.length() > 200) {
            throw new RuntimeException("内容不能超过200字");
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);

        postMapper.insert(post);
    }

    @Override
    public List<Post> listAll() {
        return postMapper.findAll();
    }

    @Override
    public void delete(Long postId, Long userId) {

        Post post = postMapper.findById(postId);

        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除");
        }

        postMapper.deleteById(postId);
    }
}