package com.huanghaha.treehole.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子实体
 * 对应数据库 post 表，like_count/favorite_count 为冗余计数字段，通过 SQL 原子更新保证一致性
 */
@Data
public class Post {

    private Long id;
    private Long userId;
    private String username;
    private String content;
    /** 点赞数（冗余字段，由 post_like 表聚合维护） */
    private Integer likeCount;
    /** 收藏数（冗余字段，由 post_favorite 表聚合维护） */
    private Integer favoriteCount;
    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
    private LocalDateTime createTime;
}
