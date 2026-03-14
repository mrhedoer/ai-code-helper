package com.hejunhao.aicodehelper;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SharedChatMemoryProvider implements ChatMemoryProvider {

    private final MySqlChatMemoryStore mySqlChatMemoryStore;
    private final Map<Object, ChatMemory> cache = new ConcurrentHashMap<>();

    public SharedChatMemoryProvider(MySqlChatMemoryStore mySqlChatMemoryStore) {
        this.mySqlChatMemoryStore = mySqlChatMemoryStore;
    }

    @Override
    public ChatMemory get(Object memoryId) {
        return cache.computeIfAbsent(memoryId, id -> MessageWindowChatMemory.builder()
                .id(id)
                .maxMessages(10)
                .chatMemoryStore(mySqlChatMemoryStore)
                .build());
    }
}
