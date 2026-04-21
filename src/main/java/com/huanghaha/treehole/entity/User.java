package com.huanghaha.treehole.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 * 对应数据库 user 表，采用逻辑删除（is_deleted）
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    /** 账号状态：0-待审核，1-正常，2-封禁，99-管理员 */
    private Integer status;
    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
    private LocalDateTime createTime;
}