package com.hejunhao.aicodehelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 简单的token存储（生产环境应该使用Redis或数据库）
    private final Map<String, String> validTokens = new HashMap<>();

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String email = registerRequest.get("email");

        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByUsername(username).isPresent()) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }

        if (email != null && !email.isEmpty() && userRepository.findByEmail(email).isPresent()) {
            response.put("success", false);
            response.put("message", "邮箱已被注册");
            return ResponseEntity.badRequest().body(response);
        }

        UserEntity newUser = new UserEntity(username, password, email);
        userRepository.save(newUser);

        response.put("success", true);
        response.put("message", "注册成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 忘记密码 (重置密码)
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> resetRequest) {
        String username = resetRequest.get("username");
        String newPassword = resetRequest.get("newPassword");
        // 实际应用中应该验证邮箱或手机号，这里简化为直接根据用户名重置

        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setPassword(newPassword);
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "密码重置成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Map<String, Object> response = new HashMap<>();

        Optional<UserEntity> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
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
