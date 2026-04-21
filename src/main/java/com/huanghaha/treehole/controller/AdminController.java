package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.AdminService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private static final int ADMIN_STATUS = 99;

    @Resource
    private AdminService adminService;

    @Data
    public static class UserAuditDTO {
        private String userName;
        private Integer status;
    }

    @GetMapping("/waitAuditUserList")
    public Result<List<User>> getWaitAuditUserList(HttpSession session) {
        User admin = getAdminFromSession(session);
        if (admin == null) {
            return Result.error("无管理员权限");
        }
        List<User> userList = adminService.getWaitAuditUsers(admin.getStatus());
        return Result.success(userList);
    }

    @PostMapping("/auditUser")
    public Result<String> auditUser(@RequestBody UserAuditDTO auditDTO, HttpSession session) {
        User admin = getAdminFromSession(session);
        if (admin == null) {
            return Result.error("无管理员权限");
        }
        adminService.updateStatus(auditDTO.getUserName(), auditDTO.getStatus());
        return Result.success("审核成功");
    }

    @DeleteMapping("/post/{postId}")
    public Result<String> deleteAnyPost(@PathVariable Long postId, HttpSession session) {
        User admin = getAdminFromSession(session);
        if (admin == null) {
            return Result.error("无管理员权限");
        }
        adminService.deleteAnyPost(postId, admin.getStatus());
        return Result.success("帖子删除成功");
    }

    @DeleteMapping("/comment/{commentId}")
    public Result<String> deleteAnyComment(@PathVariable Long commentId, HttpSession session) {
        User admin = getAdminFromSession(session);
        if (admin == null) {
            return Result.error("无管理员权限");
        }
        adminService.deleteAnyComment(commentId, admin.getStatus());
        return Result.success("评论删除成功");
    }

    private User getAdminFromSession(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || user.getStatus() != ADMIN_STATUS) {
            return null;
        }
        return user;
    }
}
