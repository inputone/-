package com.huanghaha.treehole.service;

public interface AdminService {
    void updateStatus(String userName, Integer status);
    void deleteAnyPost(Long postId, Integer status);
    void deleteAnyComment(Long commentId, Integer status);
}
