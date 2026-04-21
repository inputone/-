package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 评论数据访问层
 * 对应 CommentMapper.xml，操作 comment 表
 */
@Mapper
public interface CommentMapper {

    /** 新增评论 */
    void insert(Comment comment);

    /** 根据帖子ID查询评论列表 */
    List<Comment> findByPostId(Long postId);

    /** 根据ID查询评论 */
    Comment findById(Long id);

    /** 逻辑删除评论 */
    void deleteById(Long id);
}