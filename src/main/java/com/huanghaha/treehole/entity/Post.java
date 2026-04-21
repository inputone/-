package com.huanghaha.treehole.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {

    private Long id;
    private Long userId;
    private String content;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer isDeleted;
    private LocalDateTime createTime;
}
