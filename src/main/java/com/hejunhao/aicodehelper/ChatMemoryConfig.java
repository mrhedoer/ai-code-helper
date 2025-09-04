package com.hejunhao.aicodehelper;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {
    
    private final MySqlChatMemoryStore mySqlChatMemoryStore;
    
    public ChatMemoryConfig(MySqlChatMemoryStore mySqlChatMemoryStore) {
        this.mySqlChatMemoryStore = mySqlChatMemoryStore;
    }
    
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(mySqlChatMemoryStore)
                .build();
    }
}
