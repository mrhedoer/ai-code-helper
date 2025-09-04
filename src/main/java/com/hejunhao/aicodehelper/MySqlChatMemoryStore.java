package com.hejunhao.aicodehelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MySqlChatMemoryStore implements ChatMemoryStore {

    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    // 内存缓存，提高访问速度
    private final ConcurrentHashMap<String, List<ChatMessage>> cache = new ConcurrentHashMap<>();

    public MySqlChatMemoryStore(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        // 可以在这里执行初始化操作
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String conversationId = memoryId.toString();

        // 先从缓存中获取
        if (cache.containsKey(conversationId)) {
            return cache.get(conversationId);
        }

        // 从数据库加载
        List<ChatMessageEntity> entities = chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        List<ChatMessage> messages = new ArrayList<>();

        for (ChatMessageEntity entity : entities) {
            try {
                ChatMessage message = ChatMessageDeserializer.messageFromJson(entity.getMessageContent());
                messages.add(message);
            } catch (Exception e) {
                // 处理反序列化错误
                e.printStackTrace();
            }
        }

        // 放入缓存
        cache.put(conversationId, messages);
        return messages;
    }

    @Override
    @Transactional
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String conversationId = memoryId.toString();

        // 删除旧记录
        chatMessageRepository.deleteByConversationId(conversationId);

        // 保存新记录
        for (ChatMessage message : messages) {
            try {
                String json = ChatMessageSerializer.messageToJson(message);
                ChatMessageEntity entity = new ChatMessageEntity(
                    conversationId,
                    json,
                    message.type().toString()
                );
                chatMessageRepository.save(entity);
            } catch (Exception e) {
                // 处理序列化错误
                e.printStackTrace();
            }
        }

        // 更新缓存
        cache.put(conversationId, new ArrayList<>(messages));
    }

    @Override
    @Transactional
    public void deleteMessages(Object memoryId) {
        String conversationId = memoryId.toString();

        // 删除数据库记录
        chatMessageRepository.deleteByConversationId(conversationId);

        // 清除缓存
        cache.remove(conversationId);
    }
}
