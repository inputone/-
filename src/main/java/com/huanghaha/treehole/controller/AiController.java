package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.ai.AiPrompt;
import com.huanghaha.treehole.ai.AiReply;
import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.service.AiService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @GetMapping("/prompt-types")
    public Result<List<AiPromptVO>> getPromptTypes() {
        List<AiPromptVO> list = Arrays.stream(AiPrompt.values())
                .map(p -> new AiPromptVO(p.getType(), p.getName()))
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @PostMapping("/reply/{postId}")
    public Result<AiReply> generateReply(@PathVariable Long postId, HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }
        AiReply reply = aiService.generateReply(postId);
        return Result.success(reply);
    }

    @GetMapping("/reply/{postId}")
    public Result<List<AiReply>> getReplies(@PathVariable Long postId) {
        List<AiReply> replies = aiService.getRepliesByPostId(postId);
        return Result.success(replies);
    }

    private record AiPromptVO(String type, String name) {
    }
}