package com.huanghaha.treehole.service;

import com.huanghaha.treehole.ai.AiReply;
import java.util.List;

public interface AiService {
    AiReply generateReply(Long postId);
    List<AiReply> getRepliesByPostId(Long postId);
}