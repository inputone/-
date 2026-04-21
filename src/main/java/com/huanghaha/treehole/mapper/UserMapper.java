package com.huanghaha.treehole.mapper;

import com.huanghaha.treehole.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户数据访问层
 * 对应 UserMapper.xml，操作 user 表
 */
@Mapper
public interface UserMapper {
    /** 根据用户名查询用户 */
    User findByUsername(String username);
    /** 新增用户 */
    void insert(User user);
    /** 更新用户信息（用于审核状态变更） */
    void update(User user);
    /** 根据状态查询用户列表（用于管理员审核） */
    List<User> findByStatus(Integer status);
}
