package com.huanghaha.treehole.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostFavorite {

    private Long id;
    private Long userId;
    private Long postId;
    private Integer isDeleted;
    private LocalDateTime createTime;
}
