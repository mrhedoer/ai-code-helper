package com.hejunhao.aicodehelper;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeHelperApplicationTests {
    @Resource
    private AiCodeHelper aiCodeHelper;
    @Resource
    private AiCodeHelperService aiCodeHelperService;
    @Test//测试时注意lombok
    void contextLoads() {
        aiCodeHelper.chat("你好，我是程序员小何hjh");
    }

    @Test
    void chatWithMessage() {
        UserMessage userMessage=UserMessage.from(
                TextContent.from("描述图片"),
                ImageContent.from("https://www.codefather.cn/logo.png")
        );
        aiCodeHelper.chatWithMessage(userMessage);//qwen-max 模型无法直接查看或分析图片
    }

//    @Test
//    void chat(){
//        String result= aiCodeHelperService.chat("你好，我是程序员小何hjh");
//        System.out.println(result);
//    }
//    @Test
//    void chatWithMemory(){
//        String result= aiCodeHelperService.chat("你好，我是程序员小何hjh");
//        System.out.println(result);
//        result = aiCodeHelperService.chat("你好，我是谁来着");
//        System.out.println(result);
//    }
    @Test
    void chatWithAiService() {
        // 为不同用户使用不同的 memoryId
        String user1MemoryId = "user_1001";
        String user2MemoryId = "user_1002";

        // 用户1的对话
        String result1 = aiCodeHelperService.chat(user1MemoryId, "你好，我是用户1");
        System.out.println("用户1结果: " + result1);

        // 用户2的对话
        String result2 = aiCodeHelperService.chat(user2MemoryId, "你好，我是用户2");
        System.out.println("用户2结果: " + result2);

        // 用户1继续对话（保持上下文）
        String result3 = aiCodeHelperService.chat(user1MemoryId, "我刚才说了什么？");
        System.out.println("用户1继续对话: " + result3);
    }

    @Test
    void multiUserConversationTest() {
        String user1Id = "user_2001";
        String user2Id = "user_2002";

        // 用户1开始对话
        String response1 = aiCodeHelperService.chat(user1Id, "我喜欢Java编程");
        System.out.println("用户1: " + response1);

        // 用户2开始对话
        String response2 = aiCodeHelperService.chat(user2Id, "我喜欢Python编程");
        System.out.println("用户2: " + response2);

        // 用户1继续对话，应该记住自己的上下文
        String response3 = aiCodeHelperService.chat(user1Id, "我刚才提到我喜欢什么编程语言？");
        System.out.println("用户1上下文测试: " + response3);
    }

    @Test
    void chatForReport(){
        String userMessage="你好，我是程序员小何hjh，26年应届生，请帮我制定学习报告";
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport(userMessage);
        System.out.println(report);
    }
    @Test
    void chatWithTools() {
        String memoryId = "interview_question_user";
        String result = aiCodeHelperService.chat(memoryId, "有哪些常见的计算机网络面试题？");
        System.out.println("面试题查询结果：");
        System.out.println(result);

        // 可以继续对话测试上下文记忆
        String followUpResult = aiCodeHelperService.chat(memoryId, "请详细解释TCP三次握手过程");
        System.out.println("后续问题结果：");
        System.out.println(followUpResult);
    }
    @Test
    void chatWithMcp(){
        String memoryId = "mcp_user";
        String result = aiCodeHelperService.chat(memoryId,"什么是程序员小何的编程导航？");
        System.out.println(result);
    }
}