package com.hejunhao.aicodehelper;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;

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

    @Test
    void chat(){
        String result= aiCodeHelperService.chat("你好，我是程序员小何hjh");
        System.out.println(result);
    }
    @Test
    void chatWithMemory(){
        String result= aiCodeHelperService.chat("你好，我是程序员小何hjh");
        System.out.println(result);
        result = aiCodeHelperService.chat("你好，我是谁来着");
        System.out.println(result);
    }
}
