package com.huanghaha.treehole.ai;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AiReply {
    private Long id;
    private Long postId;
    private String content;
    private String promptType;
    private Integer isDeleted;
    private LocalDateTime createTime;
}