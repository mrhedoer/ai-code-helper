package com.hejunhao.aicodehelper.tools;

import com.hejunhao.aicodehelper.InterviewQuestionEntity;
import com.hejunhao.aicodehelper.InterviewQuestionRepository;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewQuestionTool {

    private final InterviewQuestionRepository repository;

    @Tool(name = "interviewQuestionSearch", value = "Retrieves relevant interview questions from database question bank.\n" +
            "Use this tool when the user asks for interview questions about specific technologies,\n" +
            "programming concepts, or job-related topics. Supported topics include: Java, Redis, MySQL, Spring, 多线程, JVM.")
    public String searchInterviewQuestions(@P(value = "the keyword to search") String keyword) {
        try {
            String normalizedKeyword = keyword.toLowerCase().trim();

            // 1. 尝试精确匹配分类
            List<InterviewQuestionEntity> byCategory = repository.findByCategoryIgnoreCase(normalizedKeyword);
            if (!byCategory.isEmpty()) {
                String questionsStr = byCategory.stream()
                        .map(InterviewQuestionEntity::getQuestion)
                        .collect(Collectors.joining("\n\n"));
                return String.format("关于【%s】的常见面试题：\n\n%s", keyword, questionsStr);
            }

            // 2. 模糊匹配（分类或问题内容）
            List<InterviewQuestionEntity> fuzzyMatches = repository.findByCategoryContainingIgnoreCaseOrQuestionContainingIgnoreCase(normalizedKeyword, normalizedKeyword);

            if (!fuzzyMatches.isEmpty()) {
                String questionsStr = fuzzyMatches.stream()
                        .map(InterviewQuestionEntity::getQuestion)
                        .collect(Collectors.joining("\n\n"));
                return String.format("关于【%s】的相关面试题：\n\n%s", keyword, questionsStr);
            }

            // 3. 未找到，返回所有可用分类
            List<String> categories = repository.findDistinctCategories();
            return String.format("暂未找到关于【%s】的面试题。\n\n当前支持的主题包括：%s",
                    keyword, String.join(", ", categories));

        } catch (Exception e) {
            log.error("获取面试题失败", e);
            return "获取面试题失败: " + e.getMessage();
        }
    }
}
