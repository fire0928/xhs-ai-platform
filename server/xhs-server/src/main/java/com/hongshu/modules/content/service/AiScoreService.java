package com.hongshu.modules.content.service;

import com.hongshu.modules.content.entity.AiContent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * AI 内容评分服务
 * 目前使用模拟评分算法，后续可接入真实 AI 评分模型
 */
@Service
public class AiScoreService {

    private final Random random = new Random();

    /**
     * 基于内容特征生成 AI 评分
     * 评分维度：吸引力、可读性、标签匹配、平台适配
     */
    public Map<String, Integer> scoreContent(AiContent content) {
        Map<String, Integer> score = new HashMap<>();

        // 基于内容长度和特征计算各项得分（60-98 分范围）
        String text = content.getContent() != null ? content.getContent() : "";
        String title = content.getTitle() != null ? content.getTitle() : "";
        String tags = content.getTags() != null ? content.getTags() : "";

        int textLen = text.length();
        int titleLen = title.length();
        int tagCount = tags.isEmpty() ? 0 : tags.split(",").length;

        // 吸引力：基于标题长度、是否有emoji/特殊符号、内容开头是否有钩子
        int attractiveness = calculateAttractiveness(title, text);

        // 可读性：基于段落结构、句子长度、标点使用
        int readability = calculateReadability(text);

        // 标签匹配：基于标签数量、标签与内容相关性
        int tagMatch = calculateTagMatch(text, tags, tagCount);

        // 平台适配：基于内容长度、图片数量、格式是否符合小红书风格
        int platformFit = calculatePlatformFit(text, content.getImageUrls(), textLen);

        score.put("attractiveness", attractiveness);
        score.put("readability", readability);
        score.put("tagMatch", tagMatch);
        score.put("platformFit", platformFit);

        return score;
    }

    private int calculateAttractiveness(String title, String text) {
        int base = 65;
        // 标题长度适中 (8-20字) 加分
        if (title.length() >= 8 && title.length() <= 20) base += 8;
        else if (title.length() > 5) base += 4;

        // 包含 emoji 或特殊符号加分
        if (title.matches(".*[\\uD83C-\\uD83E\\u2600-\\u26FF].*")) base += 5;
        if (title.contains("！") || title.contains("?") || title.contains("？")) base += 3;

        // 内容开头有数字或"谁懂"等钩子加分
        if (text.startsWith("谁懂") || text.startsWith("家人们") || text.matches("^\\d+.*")) base += 5;

        // 添加少量随机波动
        base += random.nextInt(8);
        return Math.min(98, Math.max(60, base));
    }

    private int calculateReadability(String text) {
        int base = 68;
        if (text.isEmpty()) return 60;

        // 段落数适中加分
        String[] paragraphs = text.split("\\n+");
        if (paragraphs.length >= 3 && paragraphs.length <= 8) base += 8;
        else if (paragraphs.length >= 2) base += 4;

        // 句子长度适中（不过长）加分
        String[] sentences = text.split("[。！？\\n]+");
        int shortSentences = 0;
        for (String s : sentences) {
            if (s.length() > 5 && s.length() < 80) shortSentences++;
        }
        if (sentences.length > 0 && (double) shortSentences / sentences.length > 0.7) {
            base += 6;
        }

        // 包含 emoji 加分（增加可读性）
        if (text.matches(".*[\\uD83C-\\uD83E\\u2600-\\u26FF].*")) base += 4;

        base += random.nextInt(6);
        return Math.min(98, Math.max(60, base));
    }

    private int calculateTagMatch(String text, String tags, int tagCount) {
        int base = 62;
        if (text.isEmpty() || tags.isEmpty()) return 60;

        // 标签数量适中 (2-5个) 加分
        if (tagCount >= 2 && tagCount <= 5) base += 10;
        else if (tagCount >= 1) base += 5;

        // 标签内容是否出现在正文中
        String[] tagArr = tags.split(",");
        int matched = 0;
        for (String tag : tagArr) {
            if (!tag.trim().isEmpty() && text.contains(tag.trim())) matched++;
        }
        if (tagArr.length > 0 && (double) matched / tagArr.length > 0.5) {
            base += 10;
        }

        base += random.nextInt(8);
        return Math.min(98, Math.max(60, base));
    }

    private int calculatePlatformFit(String text, String imageUrls, int textLen) {
        int base = 65;

        // 小红书适合 300-800 字的笔记
        if (textLen >= 200 && textLen <= 1000) base += 10;
        else if (textLen >= 100) base += 5;

        // 有配图加分
        if (imageUrls != null && !imageUrls.isEmpty()) {
            int imgCount = imageUrls.split(",").length;
            if (imgCount >= 3 && imgCount <= 9) base += 10; // 小红书推荐 3-9 张图
            else if (imgCount >= 1) base += 5;
        }

        // 包含小红书常用元素加分
        if (text.contains("#") || text.contains("@")) base += 3;
        if (text.contains("点赞") || text.contains("收藏") || text.contains("关注")) base += 2;

        base += random.nextInt(6);
        return Math.min(98, Math.max(60, base));
    }
}
