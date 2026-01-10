package com.hejunhao.aicodehelper;

import com.hejunhao.aicodehelper.tools.InterviewQuestionTool;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeHelperServiceFactory {
    @Resource
    private ChatModel qwenChatModel;

    @Resource
    private MySqlChatMemoryStore mySqlChatMemoryStore;

    @Resource
    private ContentRetriever contentRetriever;

    @Resource
    private McpToolProvider mcpToolProvider;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Resource
    private InterviewQuestionTool interviewQuestionTool;

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10) // 限制每个会话最多保存10条消息
                .chatMemoryStore(mySqlChatMemoryStore)
                .build();
    }

    @Bean
    public AiCodeHelperService aiCodeHelperService() {
        return AiServices.builder(AiCodeHelperService.class)
                .chatModel(qwenChatModel)  // 修正为正确的方法名
                .streamingChatModel(qwenStreamingChatModel) //流式输出
                .chatMemoryProvider(chatMemoryProvider()) // 指定ChatMemoryProvider
                .contentRetriever(contentRetriever)// RAG 检索增强生成
                .tools(interviewQuestionTool)// 工具调用
                .toolProvider(mcpToolProvider)// MCP 工具调用
                .build();
    }



}
