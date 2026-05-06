package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.enums.AiPrompt;
import com.huanghaha.treehole.entity.AiReply;
import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.service.AiService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 回复控制器
 * 提供 AI 回复生成、查询、性格类型列表接口
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * 获取可用的 AI 性格类型列表
     * 5种性格：安慰型、吐槽型、理性型、鼓励型、综合型
     */
    @GetMapping("/prompt-types")
    public Result<List<AiPromptVO>> getPromptTypes() {
        List<AiPromptVO> list = Arrays.stream(AiPrompt.values())
                .map(p -> new AiPromptVO(p.getType(), p.getName()))
                .collect(Collectors.toList());
        return Result.success(list);
    }

    /**
     * 生成 AI 回复（需登录）
     * 根据帖子内容自动分析情绪并生成对应风格的回复
     */
    @PostMapping("/reply/{postId}")
    public Result<AiReply> generateReply(@PathVariable Long postId, HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }
        AiReply reply = aiService.generateReply(postId);
        return Result.success(reply);
    }

    /**
     * 获取帖子的 AI 回复列表
     */
    @GetMapping("/reply/{postId}")
    public Result<List<AiReply>> getReplies(@PathVariable Long postId) {
        List<AiReply> replies = aiService.getRepliesByPostId(postId);
        return Result.success(replies);
    }

    /** AI 性格类型 VO */
    private record AiPromptVO(String type, String name) {
    }
}