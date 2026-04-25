package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.ai.AiReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * AI 回复数据访问层
 * 对应 AiReplyMapper.xml，操作 ai_reply 表
 */
@Mapper
public interface AiReplyMapper {

    /** 新增 AI 回复 */
    void insert(AiReply aiReply);

    /** 根据帖子ID查询 AI 回复列表 */
    List<AiReply> selectByPostId(@Param("postId") Long postId);

    /** 根据ID查询 AI 回复 */
    AiReply selectById(@Param("id") Long id);

    /** 逻辑删除 AI 回复 */
    void deleteById(@Param("id") Long id);
}