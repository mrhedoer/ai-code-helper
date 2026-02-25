package com.hejunhao.aicodehelper.stt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class DeepgramSttService {

    private static final Logger log = LoggerFactory.getLogger(DeepgramSttService.class);

    @Value("${deepgram.api-key:}")
    private String deepgramApiKey;

    @Value("${deepgram.proxy.host:}")
    private String proxyHost;

    @Value("${deepgram.proxy.port:0}")
    private int proxyPort;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient;

    @jakarta.annotation.PostConstruct
    public void init() {
        HttpClient.Builder builder = HttpClient.newBuilder();
        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
            log.info("Configuring Deepgram HTTP Client with proxy {}:{}", proxyHost, proxyPort);
            builder.proxy(java.net.ProxySelector.of(new java.net.InetSocketAddress(proxyHost, proxyPort)));
        }
        this.httpClient = builder.build();
    }

    public String transcribeAudio(MultipartFile audioFile) {
        if (deepgramApiKey == null || deepgramApiKey.trim().isEmpty()) {
            throw new RuntimeException(
                    "Deepgram API Key is not configured. Please set deepgram.api-key in configuration.");
        }

        try {
            byte[] fileBytes = audioFile.getBytes();

            // Build the HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.deepgram.com/v1/listen?model=nova-2&smart_format=true&language=zh"))
                    .header("Authorization", "Token " + deepgramApiKey)
                    .header("Content-Type",
                            audioFile.getContentType() != null ? audioFile.getContentType() : "audio/webm")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                    .build();

            // Send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse JSON response
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode resultsNode = rootNode.path("results").path("channels").get(0).path("alternatives").get(0);
                return resultsNode.path("transcript").asText();
            } else {
                log.error("Deepgram API error: HTTP {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Failed to transcribe audio: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error communicating with Deepgram API", e);
            throw new RuntimeException("Error communicating with Deepgram API: " + e.getMessage(), e);
        }
    }
}
