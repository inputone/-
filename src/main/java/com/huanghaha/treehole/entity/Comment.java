package com.huanghaha.treehole.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体
 * 对应数据库 comment 表，关联帖子（postId）和用户（userId）
 */
@Data
public class Comment {
    private Long id;
    private Long userId;
    private String username;
    private String content;
    private Long postId;
    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
    private LocalDateTime createTime;
}
