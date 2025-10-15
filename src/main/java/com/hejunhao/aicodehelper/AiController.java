package com.hejunhao.aicodehelper;

import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")

public class AiController {
    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(String memoryId, String message) {
        return aiCodeHelperService.chatStream(memoryId, message)
            .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build())
            .concatWith(Flux.just(ServerSentEvent.<String>builder().data("[DONE]").build()));
            }
}
