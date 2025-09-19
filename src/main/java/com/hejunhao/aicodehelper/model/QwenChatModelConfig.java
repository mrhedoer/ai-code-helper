package com.hejunhao.aicodehelper.model;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Data
public class QwenChatModelConfig {
    @Value("${langchain4j.community.dashscope.chat-model.model-name}")
    private String modelName;
    @Value("${langchain4j.community.dashscope.chat-model.api-key}")
    private String apiKey;
    @Resource
    private ChatModelListener chatModelListener;
    @Bean
    public ChatModel myQwenChatModel(){
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .listeners(List.of(chatModelListener))
                .build();
    }
}
