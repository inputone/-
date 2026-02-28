package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.mapper.UserMapper;
import com.huanghaha.treehole.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j // 新增日志注解
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    // 修复配置项语法错误（补充闭合引号）
    @Value("${treehole.forbidden-words:root}")
    private String forbiddenWordsStr;

    // 密码加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void register(String username, String password) {
        // 前置参数校验
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            throw new RuntimeException("密码至少6位");
        }

        // 敏感词校验
        checkForbidden(username);

        // 检查用户名是否已存在
        if (userMapper.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 构建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // 密码加密存储
        user.setStatus(0); // 0-待审核
        user.setIsDeleted(0); // 0-未删除
        user.setCreateTime(LocalDateTime.now()); // 填充创建时间

        log.info("用户注册：用户名={}", username);
        userMapper.insert(user);
    }

    @Override
    public User login(String username, String password) {
        // 前置校验
        if (username == null || username.trim().isEmpty() || password == null) {
            throw new RuntimeException("用户名或密码不能为空");
        }

        // 查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 密码校验（加密后对比）
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 状态校验
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号待审核");
        }
        if (user.getStatus() == 2) {
            throw new RuntimeException("账号已封禁");
        }

        log.info("用户登录：用户名={}", username);
        return user;
    }

    /**
     * 敏感词校验
     */
    private void checkForbidden(String username) {
        if (forbiddenWordsStr == null || forbiddenWordsStr.trim().isEmpty()) {
            return;
        }

        String[] words = forbiddenWordsStr.split(",");
        for (String word : words) {
            String trimWord = word.trim();
            if (!trimWord.isEmpty() && username.contains(trimWord)) {
                throw new RuntimeException("用户名包含敏感词: " + trimWord);
            }
        }
    }
}