package com.huanghaha.treehole.service;

import com.huanghaha.treehole.entity.User;

public interface UserService {
    void register(String username,String password);
    User login(String username, String password);
}
