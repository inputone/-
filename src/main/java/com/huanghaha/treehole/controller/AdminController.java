package com.huanghaha.treehole.controller;

import org.springframework.beans.factory.annotation.Autowired;
import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 * 提供用户审核、删帖、删评论等管理功能
 * 所有接口需登录且用户 status=99 为管理员（由 LoginInterceptor + getAdminFromSession 校验）
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    /** 管理员状态码 */
    private static final int ADMIN_STATUS = 99;

    @Autowired
    private AdminService adminService;

    /** 用户审核请求参数 */
    @Data
    public static class UserAuditDTO {
        private String userName;
        private Integer status;
    }

    /** 获取待审核用户列表 */
    @GetMapping("/waitAuditUserList")
    public Result<List<User>> getWaitAuditUserList(HttpSession session) {
        User admin = getAdminFromSession(session);
        if (admin == null) {
            return Result.error("无管理员权限");
        }
        List<User> userList = adminService.getWaitAuditUsers(admin.getStatus());
        return Result.success(userList);
    }

    /** 审核用户（通过/封禁） */
    @PostMapping("/auditUser")
    public Result<String> auditUser(@RequestBody UserAuditDTO auditDTO, HttpSession session) {
        User admin = getAdminFromSession(session);
        if (admin == null) {
            return Result.error("无管理员权限");
        }
        adminService.updateStatus(auditDTO.getUserName(), auditDTO.getStatus());
        return Result.success("审核成功");
    }

    /** 管理员删除任意帖子 */
    @DeleteMapping("/post/{postId}")
    public Result<String> deleteAnyPost(@PathVariable Long postId, HttpSession session) {
        User admin = getAdminFromSession(session);
        if (admin == null) {
            return Result.error("无管理员权限");
        }
        adminService.deleteAnyPost(postId, admin.getStatus());
        return Result.success("帖子删除成功");
    }

    /** 管理员删除任意评论 */
    @DeleteMapping("/comment/{commentId}")
    public Result<String> deleteAnyComment(@PathVariable Long commentId, HttpSession session) {
        User admin = getAdminFromSession(session);
        if (admin == null) {
            return Result.error("无管理员权限");
        }
        adminService.deleteAnyComment(commentId, admin.getStatus());
        return Result.success("评论删除成功");
    }

    /**
     * 从 Session 中获取管理员用户
     * 校验用户已登录且 status=99，非管理员返回 null
     */
    private User getAdminFromSession(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || user.getStatus() != ADMIN_STATUS) {
            return null;
        }
        return user;
    }
}
