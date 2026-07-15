package com.cinemax.domain.quiz.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_QUIZ_OPTION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class QuizOption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPTION_ID")
    private Long optionId;

    @Column(name = "QUESTION_ID")
    private Long questionId;

    // 보기 순번 (1~4). 정답 매칭 기준값.
    @Column(name = "ORDER_NO", nullable = false)
    private Integer orderNo;

    @Column(name = "CONTENT", nullable = false, length = 500)
    private String content;

    @Column(name = "IS_CORRECT", nullable = false)
    private Boolean isCorrect;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID", insertable = false, updatable = false)
    private QuizQuestion question;

    // 보기 생성 팩토리 메서드
    public static QuizOption create(Integer orderNo, String content, Boolean isCorrect) {
        return QuizOption.builder()
                .orderNo(orderNo)
                .content(content)
                .isCorrect(isCorrect)
                .build();
    }

    // 부모 문항 연결 (양방향)
    public void assignQuestion(QuizQuestion question) {
        this.question = question;
        this.questionId = question.getQuestionId();
    }
}
