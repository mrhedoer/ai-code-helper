package com.hejunhao.aicodehelper;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;


public interface AiCodeHelperService {
    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);
}
