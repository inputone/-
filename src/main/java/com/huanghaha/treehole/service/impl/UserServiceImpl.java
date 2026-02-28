package com.huanghaha.treehole.service.impl;

import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.mapper.UserMapper;
import com.huanghaha.treehole.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Value("${treehole.forbidden-words:root")
    private String forbiddenWordsStr;

    @Override
    public void register(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }

        if (password == null || password.length() < 6) {
            throw new RuntimeException("密码至少6位");
        }

        checkForbidden(username);

        if (userMapper.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);  // 目前明文
        user.setStatus(0);

        userMapper.insert(user);
    }

    @Override
    public User login(String username, String password) {

        User user = userMapper.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号待审核");
        }

        if (user.getStatus() == 2) {
            throw new RuntimeException("账号已封禁");
        }

        return user;
    }

    private void checkForbidden(String username) {

        if (forbiddenWordsStr == null || forbiddenWordsStr.isEmpty()) {
            return;
        }

        String[] words = forbiddenWordsStr.split(",");

        for (String word : words) {
            if (username.contains(word.trim())) {
                throw new RuntimeException("用户名包含敏感词: " + word);
            }
        }
    }
}