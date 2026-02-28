package com.huanghaha.treehole.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {

    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime createTime;
}