package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.common.ForbiddenWordUtil;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.mapper.UserMapper;
import com.huanghaha.treehole.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ForbiddenWordUtil forbiddenWordUtil;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void register(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            throw new RuntimeException("密码至少6位");
        }

        forbiddenWordUtil.check(username);

        if (userMapper.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(0);
        user.setIsDeleted(0);
        user.setCreateTime(LocalDateTime.now());

        log.info("用户注册：用户名={}", username);
        userMapper.insert(user);
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null) {
            throw new RuntimeException("用户名或密码不能为空");
        }

        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号待审核");
        }
        if (user.getStatus() == 2) {
            throw new RuntimeException("账号已封禁");
        }

        log.info("用户登录：用户名={}", username);
        return user;
    }
}
