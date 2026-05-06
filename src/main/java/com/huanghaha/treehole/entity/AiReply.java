package com.huanghaha.treehole.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI 回复实体
 * 对应数据库 ai_reply 表
 */
@Data
public class AiReply {
    /** 主键ID */
    private Long id;
    /** 所属帖子ID */
    private Long postId;
    /** 回复内容 */
    private String content;
    /** 使用的提示词类型（comfort/humor/rational/encourage/default） */
    private String promptType;
    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
    /** 创建时间 */
    private LocalDateTime createTime;
}
