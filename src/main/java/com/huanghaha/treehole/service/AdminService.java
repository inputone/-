package com.huanghaha.treehole.service;

import com.huanghaha.treehole.entity.User;
import java.util.List;

public interface AdminService {
    void updateStatus(String userName, Integer status);
    void deleteAnyPost(Long postId, Integer status);
    void deleteAnyComment(Long commentId, Integer status);
    List<User> getWaitAuditUsers(Integer adminStatus);
}
