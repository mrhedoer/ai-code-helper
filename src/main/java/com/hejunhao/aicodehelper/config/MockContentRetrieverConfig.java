package com.hejunhao.aicodehelper.config;

import dev.langchain4j.rag.content.retriever.ContentRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collections;

@Configuration
public class MockContentRetrieverConfig {
    //创建简单的Mock实现，测试用
    @Bean
    @Primary
    public ContentRetriever mockContentRetriever() {
        return query -> Collections.emptyList();
    }
}
