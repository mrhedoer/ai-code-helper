package com.hejunhao.aicodehelper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    // 简单的token存储（生产环境应该使用Redis或数据库）
    private final Map<String, String> validTokens = new HashMap<>();

    // 默认用户账号
    private static final String DEFAULT_USERNAME = "hejunhao";
    private static final String DEFAULT_PASSWORD = "123456";

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 添加日志用于调试
        System.out.println("=== 用户登录请求 ===");
        System.out.println("收到的用户名: [" + username + "]");
        System.out.println("收到的密码: [" + password + "]");
        System.out.println("期望的用户名: [" + DEFAULT_USERNAME + "]");
        System.out.println("期望的密码: [" + DEFAULT_PASSWORD + "]");
        System.out.println("用户名匹配: " + DEFAULT_USERNAME.equals(username));
        System.out.println("密码匹配: " + DEFAULT_PASSWORD.equals(password));
        System.out.println("====================");

        Map<String, Object> response = new HashMap<>();

        if (DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password)) {
            // 生成token
            String token = UUID.randomUUID().toString();
            validTokens.put(token, username);

            response.put("success", true);
            response.put("token", token);
            response.put("message", "登录成功");
            response.put("username", username);
            System.out.println("✅ 用户登录成功，生成token: " + token);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            System.out.println("❌ 用户登录失败");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * 验证token是否有效
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && validTokens.containsKey(token)) {
            response.put("success", true);
            response.put("username", validTokens.get(token));
            response.put("message", "Token有效");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Token无效或已过期");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && validTokens.containsKey(token)) {
            validTokens.remove(token);
            response.put("success", true);
            response.put("message", "登出成功");
            System.out.println("✅ 用户登出成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Token无效");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && validTokens.containsKey(token)) {
            String username = validTokens.get(token);
            response.put("success", true);
            response.put("username", username);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "未登录或Token无效");
            return ResponseEntity.status(401).body(response);
        }
    }
}

