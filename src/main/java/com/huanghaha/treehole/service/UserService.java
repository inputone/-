package com.huanghaha.treehole.service;

import com.huanghaha.treehole.entity.User;

/**
 * 用户服务接口
 * 提供用户注册、登录功能
 */
public interface UserService {
    /**
     * 用户注册，注册后状态为待审核（status=0）
     *
     * @param username 用户名
     * @param password 明文密码（内部加密存储）
     */
    void register(String username, String password);

    /**
     * 用户登录，校验用户名、密码、账号状态
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 登录成功的用户实体
     */
    User login(String username, String password);
}
