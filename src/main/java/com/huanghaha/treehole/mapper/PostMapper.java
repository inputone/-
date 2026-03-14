package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {

    void insert(Post post);

    List<Post> findAll();

    List<Post> findByPage(Integer offset, Integer pageSize);

    Long count();

    Post findById(Long id);

    void deleteById(Long id);
}