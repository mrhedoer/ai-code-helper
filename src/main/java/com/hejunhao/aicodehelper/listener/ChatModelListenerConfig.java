package com.hejunhao.aicodehelper.listener;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ChatModelListenerConfig {
    @Bean
    ChatModelListener chatModelListener(){
        return new ChatModelListener() {
            @Override//输出请求
            public void onRequest(ChatModelRequestContext requestContext) {
                log.info("onRequest: {}", requestContext.chatRequest());
            }

            @Override//响应
            public void onResponse(ChatModelResponseContext responseContext) {
                log.info("onResponse: {}", responseContext.chatResponse());
            }

            @Override//错误信息
            public void onError(ChatModelErrorContext errorContext) {
                log.error("onError: {}", errorContext.error().getMessage());
            }
        };
    }
}
