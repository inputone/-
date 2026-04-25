package com.huanghaha.treehole.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子收藏关联实体
 * 对应数据库 post_favorite 表，uk_user_post 唯一索引防止重复收藏
 */
@Data
public class PostFavorite {
    /** 主键ID */
    private Long id;
    /** 收藏用户ID */
    private Long userId;
    /** 被收藏的帖子ID */
    private Long postId;
    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
    /** 创建时间 */
    private LocalDateTime createTime;
}
