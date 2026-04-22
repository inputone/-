package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.ai.AiReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiReplyMapper {
    void insert(AiReply aiReply);
    List<AiReply> selectByPostId(@Param("postId") Long postId);
    AiReply selectById(@Param("id") Long id);
    void deleteById(@Param("id") Long id);
}