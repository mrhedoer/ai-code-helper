package com.hejunhao.aicodehelper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestionEntity, Long> {
    
    // 根据分类精确查询
    List<InterviewQuestionEntity> findByCategoryIgnoreCase(String category);

    // 模糊查询：分类或问题内容包含关键字
    List<InterviewQuestionEntity> findByCategoryContainingIgnoreCaseOrQuestionContainingIgnoreCase(String category, String question);
    
    // 获取所有不重复的分类
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT q.category FROM InterviewQuestionEntity q")
    List<String> findDistinctCategories();
}
