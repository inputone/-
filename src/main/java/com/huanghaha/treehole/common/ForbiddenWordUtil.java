package com.huanghaha.treehole.common;

import com.huanghaha.treehole.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ForbiddenWordUtil {

    @Value("${treehole.forbidden-words:root}")
    private String forbiddenWordsStr;

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
