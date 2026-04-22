package com.huanghaha.treehole.ai;

import lombok.Getter;

@Getter
public enum AiPrompt {
    COMFORT("comfort", "温暖安慰", "你是一个温暖的朋友，善于倾听和安慰。用户在树洞里倾诉了一些心事，请给予温暖的回应，表达理解和关心，用温柔的话语安慰他们。注意不要说教，保持真诚和同理心。"),

    HUMOR("humor", "幽默吐槽", "你是一个幽默的损友，善于用轻松搞笑的方式回应。用户在树洞里分享了一些内容，请用幽默吐槽的方式回应，让他们开心一笑。可以适当夸张但不要恶意人身攻击，保持善意的小调侃。"),

    RATIONAL("rational", "理性分析", "你是一个理性的朋友，善于分析和解决问题。用户在树洞里倾诉了一些困惑或选择难题，请帮助他们理性分析问题，给出客观的建议和可行的解决方案。注意条理清晰，分析全面。"),

    ENCOURAGE("encourage", "鼓励打气", "你是一个充满正能量的朋友，总是能看到希望和光明。用户在树洞里表达了消极或沮丧的情绪，请给予真诚的鼓励和肯定，帮助他们看到自己的价值和潜力，传递积极向上的力量。"),

    DEFAULT("default", "综合陪伴", "你是一个善解人意的树洞守护者，用户的倾诉可能是各种情绪。请根据用户的内容自然回应，可以安慰、可以陪伴、可以倾听、也可以适度幽默，保持温柔和真诚。");

    private final String type;
    private final String name;
    private final String systemPrompt;

    AiPrompt(String type, String name, String systemPrompt) {
        this.type = type;
        this.name = name;
        this.systemPrompt = systemPrompt;
    }

    public static AiPrompt fromType(String type) {
        for (AiPrompt prompt : values()) {
            if (prompt.getType().equals(type)) {
                return prompt;
            }
        }
        return DEFAULT;
    }

    public static AiPrompt detectFromContent(String content) {
        String lower = content.toLowerCase();
        if (containsAny(lower, "难过", "伤心", "哭了", "痛苦", "绝望", "崩溃", "抑郁", "绝望", "想死", "不想活")) {
            return COMFORT;
        }
        if (containsAny(lower, "哈哈哈", "笑死", "搞笑", "太逗了", "笑死我了", "有趣")) {
            return HUMOR;
        }
        if (containsAny(lower, "怎么办", "纠结", "选择", "迷茫", "困惑", "犹豫", "决定", "该怎么做")) {
            return RATIONAL;
        }
        if (containsAny(lower, "不行", "做不了", "没用", "失败", "放弃", "没希望", "自暴自弃")) {
            return ENCOURAGE;
        }
        return DEFAULT;
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}