package com.huanghaha.treehole.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体
 * 对应数据库 comment 表，关联帖子（postId）和用户（userId）
 */
@Data
public class Comment {
    /** 主键ID */
    private Long id;
    /** 评论用户ID */
    private Long userId;
    /** 评论用户名（冗余字段，避免关联查询） */
    private String username;
    /** 评论内容 */
    private String content;
    /** 所属帖子ID */
    private Long postId;
    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
    /** 创建时间 */
    private LocalDateTime createTime;
}
