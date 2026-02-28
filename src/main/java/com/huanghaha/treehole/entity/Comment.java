package com.huanghaha.treehole.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {

    private Long id;
    private String content;
    private Long userId;
    private Long postId;
    private LocalDateTime createTime;
}