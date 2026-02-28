package com.huanghaha.treehole.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private long id;
    private String username;
    private String password;
    private Integer status;
    private Integer isDeleted;
    private LocalDateTime createTime;


}
