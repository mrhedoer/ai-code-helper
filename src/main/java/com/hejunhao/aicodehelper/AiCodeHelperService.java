package com.hejunhao.aicodehelper;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.List;


public interface AiCodeHelperService {
    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(@MemoryId String memoryId, @UserMessage String userMessage);
    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String userMessage);

    //学习报告
    record Report(String name, List<String> suggestionList){}
}