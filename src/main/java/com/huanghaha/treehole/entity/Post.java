package com.huanghaha.treehole.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 帖子实体
 * 对应数据库 post 表，冗余计数字段（like_count/favorite_count/comment_count）通过 SQL 原子更新保证一致性
 */
@Data
public class Post implements Serializable { // ← 加上 implements Serializable
    private static final long serialVersionUID = 1L;
    /** 主键ID */
    private Long id;
    /** 发布者用户ID */
    private Long userId;
    /** 发布者用户名（冗余字段，避免关联查询） */
    private String username;
    /** 帖子内容 */
    private String content;
    /** 点赞数（冗余字段，由 post_like 表聚合维护） */
    private Integer likeCount;
    /** 收藏数（冗余字段，由 post_favorite 表聚合维护） */
    private Integer favoriteCount;
    /** 评论数（冗余字段，由 comment 表聚合维护） */
    private Integer commentCount;
    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
    /** 创建时间 */
    private LocalDateTime createTime;
}
