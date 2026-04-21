package com.hejunhao.aicodehelper;

import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/ai")

public class AiController {
    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Resource
    private SharedChatMemoryProvider chatMemoryProvider;

    @PostMapping("/upload")
    public Map<String, Object> uploadDocument(@RequestParam("file") MultipartFile file,
            @RequestParam("memoryId") String memoryId) {
        Map<String, Object> result = new HashMap<>();
        try {
            DocumentParser parser = new ApacheTikaDocumentParser();
            try (InputStream is = file.getInputStream()) {
                Document document = parser.parse(is);
                String textToInject = "用户上传了文件：" + file.getOriginalFilename() + "\n内容如下：\n" + document.text();

                ChatMemory memory = chatMemoryProvider.get(memoryId);
                memory.add(UserMessage.from(textToInject));
                // Add an AI confirmation message to prevent the LLM from ignoring consecutive
                // UserMessages
                memory.add(dev.langchain4j.data.message.AiMessage.from("我已读取并了解了该文件的内容。"));

                result.put("success", true);
                result.put("message", "文件解析并注入成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "解析文件失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(String memoryId, String message) {
        // [隐私脱敏] 拦截并替换 11 位手机号和 18 位身份证号码（图中的安全机制实现）
        String safeMessage = message != null ? message.replaceAll("(1[3-9]\\d{9})|(\\d{17}[\\dXx])", "***") : "";
        
        try {
            return aiCodeHelperService.chatStream(memoryId, safeMessage)
                    .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build())
                    .concatWith(Flux.just(ServerSentEvent.<String>builder().data("[DONE]").build()))
                    .onErrorResume(e -> Flux.just(
                            ServerSentEvent.<String>builder().data("【提示】" + e.getMessage()).build(),
                            ServerSentEvent.<String>builder().data("[DONE]").build()
                    ));
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "系统发生未知异常";
            return Flux.just(
                    ServerSentEvent.<String>builder().data("【安全拦截】" + errorMsg).build(),
                    ServerSentEvent.<String>builder().data("[DONE]").build()
            );
        }
    }
}
