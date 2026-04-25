package com.huanghaha.treehole.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * AI 配置
 * 配置 RestTemplate Bean，用于调用 DeepSeek AI API
 */
@Configuration
public class AiConfig {

    /** RestTemplate 实例，用于 HTTP 请求（调用 AI 接口） */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}