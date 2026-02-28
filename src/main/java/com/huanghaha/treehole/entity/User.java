package com.huanghaha.treehole.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    // 改为包装类型，避免空值问题
    private Long id;
    private String username;
    private String password;
    private Integer status;      // 0-待审核，1-正常，2-封禁
    private Integer isDeleted;   // 0-未删除，1-已删除
    private LocalDateTime createTime;
}