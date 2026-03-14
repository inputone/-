package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.Comment;
import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.mapper.CommentMapper;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.mapper.UserMapper;
import com.huanghaha.treehole.service.AdminService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public void updateStatus(String userName, Integer status) {
        log.info("开始执行用户审核操作，待审核用户名：{}，目标状态：{}", userName, status);

        if (userName == null || userName.trim().isEmpty()) {
            log.error("用户审核失败：用户名不能为空");
            throw new RuntimeException("用户名不能为空");
        }

        if (!status.equals(1) && !status.equals(2)) {
            log.error("用户审核失败：状态[{}]不合法，仅支持1（通过）/2（封禁）", status);
            throw new RuntimeException("审核状态仅支持1（通过）/2（封禁）");
        }

        User waitAuditUser = userMapper.findByUsername(userName);
        if (waitAuditUser == null) {
            log.error("用户审核失败：用户名[{}]不存在", userName);
            throw new RuntimeException("待审核用户不存在");
        }

        Integer oldStatus = waitAuditUser.getStatus();
        waitAuditUser.setStatus(status);
        userMapper.update(waitAuditUser);

        log.info("用户审核成功，用户名：{}，原状态：{}，新状态：{}", userName, oldStatus, status);
    }

    @Override
    public void deleteAnyPost(Long postId, Integer status) {
        validateAdmin(status);

        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        postMapper.deleteById(postId);
        log.info("管理员删除帖子：帖子ID={}", postId);
    }

    @Override
    public void deleteAnyComment(Long commentId, Integer status) {
        validateAdmin(status);

        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        commentMapper.deleteById(commentId);
        log.info("管理员删除评论：评论ID={}", commentId);
    }

    private void validateAdmin(Integer status) {
        if (status == null || status != 99) {
            log.error("管理员权限校验失败：状态值[{}]非管理员（需为99）", status);
            throw new RuntimeException("无管理员权限");
        }
    }
}
