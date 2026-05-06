package com.huanghaha.treehole.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huanghaha.treehole.enums.AiPrompt;
import com.huanghaha.treehole.entity.AiReply;
import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.common.BusinessException;
import com.huanghaha.treehole.mapper.AiReplyMapper;
import com.huanghaha.treehole.mapper.PostMapper;
import com.huanghaha.treehole.service.AiService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 回复服务实现
 * 调用 DeepSeek API 生成回复，支持 5 种性格风格自动匹配
 */
@Slf4j
@Service
public class AiServiceImpl implements AiService {

    /** DeepSeek API 地址 */
    @Value("${treehole.ai.api-url}")
    private String apiUrl;

    /** DeepSeek API 密钥 */
    @Value("${treehole.ai.api-key}")
    private String apiKey;

    /** 使用的模型名称 */
    @Value("${treehole.ai.model}")
    private String model;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private AiReplyMapper aiReplyMapper;

    @Autowired
    private RestTemplate restTemplate;

    /** JSON 解析器 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成 AI 回复
     * 流程：查询帖子 → 分析情绪匹配提示词 → 调用 AI API → 保存回复
     */
    @Override
    public AiReply generateReply(Long postId) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException(500, "帖子不存在");
        }

        AiPrompt prompt = AiPrompt.detectFromContent(post.getContent());

        String userPrompt = "用户发布了以下内容：\n" + post.getContent() + "\n\n请根据上面的内容，用" + prompt.getName() + "的风格回复用户。";

        String aiResponse = callAiApi(prompt.getSystemPrompt(), userPrompt);

        AiReply aiReply = new AiReply();
        aiReply.setPostId(postId);
        aiReply.setContent(aiResponse);
        aiReply.setPromptType(prompt.getType());
        aiReplyMapper.insert(aiReply);

        return aiReply;
    }

    /** 根据帖子ID查询 AI 回复列表 */
    @Override
    public List<AiReply> getRepliesByPostId(Long postId) {
        return aiReplyMapper.selectByPostId(postId);
    }

    /**
     * 调用 DeepSeek AI API 生成回复
     *
     * @param systemPrompt 系统提示词（定义 AI 性格）
     * @param userPrompt   用户输入（帖子内容）
     * @return AI 生成的回复内容
     */
    private String callAiApi(String systemPrompt, String userPrompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", userPrompt);

            Map<String, Object> system = new HashMap<>();
            system.put("role", "system");
            system.put("content", systemPrompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", new Object[] { system, message });

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI API 调用失败: {}", e.getMessage());
            throw new BusinessException(500, "AI回复生成失败，请稍后重试");
        }
    }
}