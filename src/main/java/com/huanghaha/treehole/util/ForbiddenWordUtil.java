package com.huanghaha.treehole.util;

import com.huanghaha.treehole.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 敏感词过滤工具
 * 从 application.yml 的 treehole.forbidden-words 读取敏感词列表（逗号分隔），
 * 检测内容是否包含敏感词，包含则抛出 BusinessException
 */
@Component
public class ForbiddenWordUtil {

    /** 敏感词配置，从 application.yml 注入，逗号分隔 */
    @Value("${treehole.forbidden-words:root}")
    private String forbiddenWordsStr;

    /**
     * 检查内容是否包含敏感词
     *
     * @param content 待检查的内容
     * @throws BusinessException 内容包含敏感词时抛出
     */
    public void check(String content) {
        if (forbiddenWordsStr == null || forbiddenWordsStr.trim().isEmpty()) {
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        String[] words = forbiddenWordsStr.split(",");
        for (String word : words) {
            String trimWord = word.trim();
            if (!trimWord.isEmpty() && content.contains(trimWord)) {
                throw new BusinessException("内容包含敏感词: " + trimWord);
            }
        }
    }
}
