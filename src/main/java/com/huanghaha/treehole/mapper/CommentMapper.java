package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    void insert(Comment comment);

    List<Comment> findByPostId(Long postId);

    Comment findById(Long id);

    void deleteById(Long id);
}