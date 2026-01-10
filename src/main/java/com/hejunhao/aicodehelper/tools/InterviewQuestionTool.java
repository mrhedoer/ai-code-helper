package com.hejunhao.aicodehelper.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class InterviewQuestionTool {

    // 本地面试题库
    private static final Map<String, List<String>> QUESTION_BANK = new HashMap<>();

    static {
        // Java相关
        QUESTION_BANK.put("java", Arrays.asList(
                "1. 什么是面向对象编程？请说明其三大特性。",
                "2. Java中的==和equals()有什么区别？",
                "3. 什么是Java中的多态？请举例说明。",
                "4. String、StringBuilder和StringBuffer的区别是什么？",
                "5. Java中的final关键字有哪些用法？",
                "6. 什么是Java的垃圾回收机制？",
                "7. ArrayList和LinkedList的区别是什么？",
                "8. HashMap的底层实现原理是什么？",
                "9. 什么是线程安全？如何实现线程安全？",
                "10. synchronized和Lock的区别是什么？"
        ));

        // Redis相关
        QUESTION_BANK.put("redis", Arrays.asList(
                "1. Redis支持哪些数据类型？",
                "2. Redis的持久化机制有哪些？RDB和AOF的区别是什么？",
                "3. 什么是Redis的缓存穿透、缓存击穿和缓存雪崩？如何解决？",
                "4. Redis如何实现分布式锁？",
                "5. Redis的过期策略有哪些？",
                "6. Redis集群模式有哪些？主从复制的原理是什么？",
                "7. Redis的单线程模型为什么还能支持高并发？",
                "8. Redis的事务机制是怎样的？",
                "9. 如何保证Redis和数据库的数据一致性？",
                "10. Redis的哨兵模式是什么？有什么作用？"
        ));

        // MySQL相关
        QUESTION_BANK.put("mysql", Arrays.asList(
                "1. 什么是索引？MySQL有哪些类型的索引？",
                "2. 什么是B+树？为什么MySQL使用B+树作为索引？",
                "3. 什么是事务？ACID特性分别是什么？",
                "4. MySQL的隔离级别有哪些？分别解决了什么问题？",
                "5. 什么是死锁？如何避免死锁？",
                "6. explain关键字有什么作用？如何使用？",
                "7. 什么是慢查询？如何优化慢查询？",
                "8. InnoDB和MyISAM的区别是什么？",
                "9. 什么是主从复制？复制的原理是什么？",
                "10. 如何进行分库分表？"
        ));

        // Spring相关
        QUESTION_BANK.put("spring", Arrays.asList(
                "1. 什么是IoC和DI？它们有什么区别？",
                "2. Spring Bean的生命周期是怎样的？",
                "3. Spring中有哪些Bean的作用域？",
                "4. 什么是AOP？AOP的应用场景有哪些？",
                "5. Spring的事务管理是如何实现的？",
                "6. @Autowired和@Resource的区别是什么？",
                "7. Spring Boot的自动配置原理是什么？",
                "8. 什么是循环依赖？Spring如何解决循环依赖？",
                "9. Spring MVC的工作流程是怎样的？",
                "10. Spring中的设计模式有哪些？"
        ));

        // 多线程相关
        QUESTION_BANK.put("多线程", Arrays.asList(
                "1. 进程和线程的区别是什么？",
                "2. 如何创建线程？有哪些方式？",
                "3. 什么是线程池？线程池的核心参数有哪些？",
                "4. volatile关键字的作用是什么？",
                "5. 什么是happens-before原则？",
                "6. CAS是什么？有什么优缺点？",
                "7. ThreadLocal是什么？有什么应用场景？",
                "8. 什么是死锁？如何避免死锁？",
                "9. CountDownLatch、CyclicBarrier和Semaphore的区别是什么？",
                "10. Java并发包中的常用类有哪些？"
        ));

        // JVM相关
        QUESTION_BANK.put("jvm", Arrays.asList(
                "1. JVM的内存结构是怎样的？",
                "2. 什么是类加载机制？类加载的过程是怎样的？",
                "3. 什么是双亲委派模型？为什么要使用双亲委派模型？",
                "4. 垃圾回收算法有哪些？",
                "5. 常见的垃圾收集器有哪些？它们的特点是什么？",
                "6. 什么是内存泄漏和内存溢出？有什么区别？",
                "7. 如何进行JVM调优？",
                "8. Full GC什么时候会触发？",
                "9. 什么是逃逸分析？",
                "10. JVM的参数有哪些？如何配置？"
        ));
    }

    @Tool(name = "interviewQuestionSearch", value = "Retrieves relevant interview questions from local question bank.\n" +
            "Use this tool when the user asks for interview questions about specific technologies,\n" +
            "programming concepts, or job-related topics. Supported topics include: Java, Redis, MySQL, Spring, 多线程, JVM.")
    public String searchInterviewQuestions(@P(value = "the keyword to search") String keyword) {
        try {
            String normalizedKeyword = keyword.toLowerCase().trim();

            // 精确匹配
            if (QUESTION_BANK.containsKey(normalizedKeyword)) {
                List<String> questions = QUESTION_BANK.get(normalizedKeyword);
                return String.format("关于【%s】的常见面试题：\n\n%s",
                        keyword, String.join("\n\n", questions));
            }

            // 模糊匹配
            List<String> allQuestions = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : QUESTION_BANK.entrySet()) {
                if (entry.getKey().contains(normalizedKeyword) ||
                        normalizedKeyword.contains(entry.getKey())) {
                    allQuestions.addAll(entry.getValue());
                }
            }

            if (!allQuestions.isEmpty()) {
                return String.format("关于【%s】的相关面试题：\n\n%s",
                        keyword, String.join("\n\n", allQuestions));
            }

            // 未找到相关题目
            return String.format("暂未找到关于【%s】的面试题。\n\n当前支持的主题包括：%s",
                    keyword, String.join(", ", QUESTION_BANK.keySet()));

        } catch (Exception e) {
            log.error("获取面试题失败", e);
            return "获取面试题失败: " + e.getMessage();
        }
    }
}
