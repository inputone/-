package com.huanghaha.treehole.service;

import com.huanghaha.treehole.entity.User;
import java.util.List;

/**
 * 管理员服务接口
 * 提供用户审核、帖子/评论管理功能，所有方法均需校验管理员权限（status=99）
 */
public interface AdminService {
    /**
     * 审核用户（通过/封禁）
     *
     * @param userName 待审核用户名
     * @param status   目标状态：1-通过，2-封禁
     */
    void updateStatus(String userName, Integer status);

    /**
     * 管理员删除任意帖子
     *
     * @param postId 帖子ID
     * @param status 当前管理员状态（用于权限校验）
     */
    void deleteAnyPost(Long postId, Integer status);

    /**
     * 管理员删除任意评论
     *
     * @param commentId 评论ID
     * @param status    当前管理员状态（用于权限校验）
     */
    void deleteAnyComment(Long commentId, Integer status);

    /**
     * 查询待审核用户列表
     *
     * @param adminStatus 当前管理员状态（用于权限校验）
     * @return 待审核用户列表（密码已脱敏）
     */
    List<User> getWaitAuditUsers(Integer adminStatus);
}
