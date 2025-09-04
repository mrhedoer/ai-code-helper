package com.hejunhao.aicodehelper;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
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
                .chatMemoryProvider(chatMemoryProvider()) // 指定ChatMemoryProvider
                .build();
    }
}
