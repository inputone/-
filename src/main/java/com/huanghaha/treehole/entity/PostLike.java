package com.huanghaha.treehole.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子点赞关联实体
 * 对应数据库 post_like 表，uk_user_post 唯一索引防止重复点赞
 */
@Data
public class PostLike {
    /** 主键ID */
    private Long id;
    /** 点赞用户ID */
    private Long userId;
    /** 被点赞的帖子ID */
    private Long postId;
    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
    /** 创建时间 */
    private LocalDateTime createTime;
}
