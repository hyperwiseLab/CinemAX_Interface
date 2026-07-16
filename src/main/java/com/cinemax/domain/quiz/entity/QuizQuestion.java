package com.cinemax.domain.quiz.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.global.enums.QuizQuestionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBL_QUIZ_QUESTION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class QuizQuestion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_ID")
    private Long questionId;

    // MULTIPLE(객관식) / OX
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private QuizQuestionType type;

    @Column(name = "ORDER_NO", nullable = false)
    private Integer orderNo;

    // 문제 지문
    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    // 정답. OX면 "O"/"X", MULTIPLE이면 정답 보기의 orderNo(문자열). 채점의 기준값.
    @Column(name = "ANSWER", nullable = false, length = 500)
    private String answer;

    // 해설
    @Column(name = "EXPLANATION", length = 1000)
    private String explanation;

    // 배점
    @Column(name = "SCORE", nullable = false)
    private Integer score;

    // FK 는 이 연관관계가 관리한다. (스칼라 quizId 를 따로 두면
    // 부모 저장 전 id 가 null 이라 문항이 고아로 저장된다)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUIZ_ID")
    private Quiz quiz;

    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuizOption> options = new ArrayList<>();

    // 객관식 문항 생성
    public static QuizQuestion createMultiple(Integer orderNo, String content, String answer,
                                              String explanation, Integer score) {
        return QuizQuestion.builder()
                .type(QuizQuestionType.MULTIPLE)
                .orderNo(orderNo)
                .content(content)
                .answer(answer)
                .explanation(explanation)
                .score(score)
                .build();
    }

    // O/X 문항 생성
    public static QuizQuestion createOx(Integer orderNo, String content, String answer,
                                        String explanation, Integer score) {
        return QuizQuestion.builder()
                .type(QuizQuestionType.OX)
                .orderNo(orderNo)
                .content(content)
                .answer(answer)
                .explanation(explanation)
                .score(score)
                .build();
    }

    // 부모 Quiz 연결 (양방향)
    public void assignQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    // 보기 추가 (객관식, 양방향)
    public void addOption(QuizOption option) {
        this.options.add(option);
        option.assignQuestion(this);
    }
}
