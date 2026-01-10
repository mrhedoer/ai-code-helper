package com.hejunhao.aicodehelper;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interview_questions")
@Data
@NoArgsConstructor
public class InterviewQuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, length = 1000)
    private String question;

    public InterviewQuestionEntity(String category, String question) {
        this.category = category;
        this.question = question;
    }
}
