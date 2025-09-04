package com.hejunhao.aicodehelper;

import com.hejunhao.aicodehelper.AiCodeHelperService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    private final AiCodeHelperService aiCodeHelperService;
    
    public ChatController(AiCodeHelperService aiCodeHelperService) {
        this.aiCodeHelperService = aiCodeHelperService;
    }
    
    @PostMapping("/{userId}")
    public String chat(
            @PathVariable String userId,
            @RequestParam String message) {
        // 使用 userId 作为 memoryId 实现用户隔离
        return aiCodeHelperService.chat(userId, message);
    }
    
    @PostMapping("/session/{sessionId}")
    public String chatWithSession(
            @PathVariable String sessionId,
            @RequestParam String message) {
        // 也可以使用会话ID作为 memoryId
        return aiCodeHelperService.chat(sessionId, message);
    }
}
