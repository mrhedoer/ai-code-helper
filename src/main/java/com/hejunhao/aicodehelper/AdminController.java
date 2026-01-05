package com.hejunhao.aicodehelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    // 简单的token存储（生产环境应该使用Redis或数据库）
    private final Map<String, String> validTokens = new HashMap<>();

    // 默认管理员账号
    private static final String ADMIN_USERNAME = "hejunhao";
    private static final String ADMIN_PASSWORD = "123456";

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 添加日志用于调试
        System.out.println("=== 管理员登录请求 ===");
        System.out.println("收到的用户名: [" + username + "]");
        System.out.println("收到的密码: [" + password + "]");
        System.out.println("期望的用户名: [" + ADMIN_USERNAME + "]");
        System.out.println("期望的密码: [" + ADMIN_PASSWORD + "]");
        System.out.println("用户名匹配: " + ADMIN_USERNAME.equals(username));
        System.out.println("密码匹配: " + ADMIN_PASSWORD.equals(password));
        System.out.println("====================");

        Map<String, Object> response = new HashMap<>();

        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            // 生成token
            String token = UUID.randomUUID().toString();
            validTokens.put(token, username);

            response.put("success", true);
            response.put("token", token);
            response.put("message", "登录成功");
            System.out.println("✅ 登录成功，生成token: " + token);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            System.out.println("❌ 登录失败");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取所有消息
     */
    @GetMapping("/messages")
    public ResponseEntity<?> getAllMessages(@RequestHeader(value = "Authorization", required = false) String token) {
        if (!isValidToken(token)) {
            return ResponseEntity.status(401).body("未授权");
        }

        List<ChatMessageEntity> messages = chatMessageRepository.findAll();
        return ResponseEntity.ok(messages);
    }

    /**
     * 搜索消息
     */
    @GetMapping("/messages/search")
    public ResponseEntity<?> searchMessages(
            @RequestParam String keyword,
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (!isValidToken(token)) {
            return ResponseEntity.status(401).body("未授权");
        }

        List<ChatMessageEntity> allMessages = chatMessageRepository.findAll();
        List<ChatMessageEntity> filteredMessages = allMessages.stream()
                .filter(message ->
                        message.getConversationId().contains(keyword) ||
                        message.getMessageContent().contains(keyword))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredMessages);
    }

    /**
     * 删除单条消息
     */
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (!isValidToken(token)) {
            return ResponseEntity.status(401).body("未授权");
        }

        try {
            chatMessageRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 删除整个会话的所有消息
     */
    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<?> deleteConversation(
            @PathVariable String conversationId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (!isValidToken(token)) {
            return ResponseEntity.status(401).body("未授权");
        }

        try {
            chatMessageRepository.deleteByConversationId(conversationId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除会话成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除会话失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 验证token是否有效
     */
    private boolean isValidToken(String token) {
        return token != null && validTokens.containsKey(token);
    }

    /**
     * 登出（可选）
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null) {
            validTokens.remove(token);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登出成功");
        return ResponseEntity.ok(response);
    }
}

