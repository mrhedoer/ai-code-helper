package com.hejunhao.aicodehelper;

import com.hejunhao.aicodehelper.guardrail.SafeInputGuardrail;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import reactor.core.publisher.Flux;

import java.util.List;

@InputGuardrails({SafeInputGuardrail.class})
public interface AiCodeHelperService {
    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(@MemoryId String memoryId, @UserMessage String userMessage);
    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String userMessage);
    // 添加流式聊天方法
    @SystemMessage(fromResource = "system-prompt.txt")
    Flux<String> chatStream(@MemoryId String memoryId,@UserMessage String userMessage);

    //学习报告
    record Report(String name, List<String> suggestionList){}
}