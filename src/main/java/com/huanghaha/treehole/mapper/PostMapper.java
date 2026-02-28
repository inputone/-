package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {

    void insert(Post post);

    List<Post> findAll();

    Post findById(Long id);

    void deleteById(Long id);
}