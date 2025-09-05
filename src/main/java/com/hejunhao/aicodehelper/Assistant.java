package com.hejunhao.aicodehelper;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface Assistant {
    //流式对话
    Flux<String> chatStream(@MemoryId int memoryId, @UserMessage String userMessage);
}
