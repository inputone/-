package com.huanghaha.treehole.service;

import com.huanghaha.treehole.ai.AiReply;
import java.util.List;

/**
 * AI 回复服务接口
 * 提供 AI 回复生成和查询功能，支持 5 种性格风格（安慰/吐槽/理性/鼓励/综合）
 */
public interface AiService {

    /**
     * 生成 AI 回复
     * 根据帖子内容自动分析情绪，匹配对应性格的提示词，调用 DeepSeek API 生成回复
     *
     * @param postId 帖子ID
     * @return 生成的 AI 回复实体
     */
    AiReply generateReply(Long postId);

    /**
     * 根据帖子ID查询 AI 回复列表
     *
     * @param postId 帖子ID
     * @return 该帖子的所有 AI 回复列表
     */
    List<AiReply> getRepliesByPostId(Long postId);
}