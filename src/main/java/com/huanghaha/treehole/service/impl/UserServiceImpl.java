package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.common.ForbiddenWordUtil;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.exception.BusinessException;
import com.huanghaha.treehole.mapper.UserMapper;
import com.huanghaha.treehole.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 * 注册：校验参数 → 敏感词检查 → 用户名唯一性 → BCrypt 加密存储（默认待审核）
 * 登录：校验参数 → 用户存在 → 密码匹配 → 账号状态检查（待审核/封禁）
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ForbiddenWordUtil forbiddenWordUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(String username, String password) {
        // 参数校验
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            throw new BusinessException("密码至少6位");
        }

        // 敏感词检查
        forbiddenWordUtil.check(username);

        // 用户名唯一性检查
        if (userMapper.findByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        // BCrypt 加密存储，默认待审核状态
        user.setStatus(0);
        user.setIsDeleted(0);
        user.setCreateTime(LocalDateTime.now());

        log.info("用户注册：用户名={}", username);
        userMapper.insert(user);
    }

    @Override
    public User login(String username, String password) {
        // 参数校验
        if (username == null || username.trim().isEmpty() || password == null) {
            throw new BusinessException("用户名或密码不能为空");
        }

        // 依次校验：用户存在 → 密码正确 → 账号非待审核 → 账号非封禁
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号待审核");
        }
        if (user.getStatus() == 2) {
            throw new BusinessException("账号已封禁");
        }

        log.info("用户登录：用户名={}", username);
        return user;
    }
}
