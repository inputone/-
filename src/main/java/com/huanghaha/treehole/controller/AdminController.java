package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.mapper.UserMapper;
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

    @Resource
    private UserMapper userMapper;
    @Resource
    private AdminService adminService;

    @Data
    public static class UserAuditDTO {
        private String userName;
        private Integer status;
    }

    @GetMapping("/waitAuditUserList")
    public Result<List<User>> getWaitAuditUserList(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || user.getStatus() != 99) {
            return Result.error("无管理员权限");
        }
        List<User> userList = userMapper.findByStatus(0);
        userList.forEach(u -> u.setPassword(null));
        return Result.success(userList);
    }

    @PostMapping("/auditUser")
    public Result<String> auditUser(@RequestBody UserAuditDTO auditDTO, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || user.getStatus() != 99) {
            return Result.error("无管理员权限");
        }
        adminService.updateStatus(auditDTO.getUserName(), auditDTO.getStatus());
        return Result.success("审核成功");
    }

    @DeleteMapping("/post/{postId}")
    public Result<String> deleteAnyPost(@PathVariable Long postId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || user.getStatus() != 99) {
            return Result.error("无管理员权限");
        }
        adminService.deleteAnyPost(postId, user.getStatus());
        return Result.success("帖子删除成功");
    }

    @DeleteMapping("/comment/{commentId}")
    public Result<String> deleteAnyComment(@PathVariable Long commentId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || user.getStatus() != 99) {
            return Result.error("无管理员权限");
        }
        adminService.deleteAnyComment(commentId, user.getStatus());
        return Result.success("评论删除成功");
    }
}
