package com.huanghaha.treehole.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子收藏关联实体
 * 对应数据库 post_favorite 表，uk_user_post 唯一索引防止重复收藏
 */
@Data
public class PostFavorite {
    private Long id;
    private Long userId;
    private Long postId;
    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
    private LocalDateTime createTime;
}
