package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    void insert(User user);
    void update(User user);
    List<User> findByStatus(Integer status);
}
