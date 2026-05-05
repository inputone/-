package com.huanghaha.treehole.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 * 对应数据库 user 表，采用逻辑删除（is_deleted）
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 主键ID */
    private Long id;
    /** 用户名（唯一） */
    private String username;
    /** 密码（BCrypt 加密存储） */
    private String password;
    /** 账号状态：0-待审核，1-正常，2-封禁，99-管理员 */
    private Integer status;
    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
    /** 创建时间 */
    private LocalDateTime createTime;
}