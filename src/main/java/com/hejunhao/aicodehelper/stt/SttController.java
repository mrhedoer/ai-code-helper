package com.hejunhao.aicodehelper.stt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stt")
public class SttController {

    private final DeepgramSttService deepgramSttService;

    public SttController(DeepgramSttService deepgramSttService) {
        this.deepgramSttService = deepgramSttService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> transcribe(@RequestParam("audio") MultipartFile audio) {
        try {
            String transcript = deepgramSttService.transcribeAudio(audio);
            Map<String, String> response = new HashMap<>();
            response.put("transcript", transcript);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
