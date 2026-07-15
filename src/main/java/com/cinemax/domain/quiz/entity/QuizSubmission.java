package com.cinemax.domain.quiz.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_QUIZ_SUBMISSION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizSubmission extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUBMISSION_ID")
    private Long submissionId;

    @Column(name = "QUIZ_ID")
    private Long quizId;

    @Column(name = "USER_ID")
    private Long userId;

    // 결과 집계를 반 단위로 하기 위해 함께 저장
    @Column(name = "CLASS_ID")
    private Long classId;

    // 획득 총점
    @Column(name = "TOTAL_SCORE", nullable = false)
    private Integer totalScore;

    // 만점(문항 배점 합)
    @Column(name = "MAX_SCORE", nullable = false)
    private Integer maxScore;

    // 제출 답안 스냅샷: [{questionId, selected, correct}] 형태의 JSON
    @Column(name = "ANSWERS_JSON", columnDefinition = "JSON")
    private String answersJson;

    @Column(name = "SUBMITTED_AT", nullable = false)
    private LocalDateTime submittedAt;

    // quizId / userId / classId 는 단순 참조값으로만 저장 (FK 제약 없음)

    @Builder
    public QuizSubmission(Long quizId, Long userId, Long classId, Integer totalScore,
                          Integer maxScore, String answersJson, LocalDateTime submittedAt) {
        this.quizId = quizId;
        this.userId = userId;
        this.classId = classId;
        this.totalScore = totalScore;
        this.maxScore = maxScore;
        this.answersJson = answersJson;
        this.submittedAt = submittedAt;
    }

    // 최초 제출 기록 팩토리 메서드
    public static QuizSubmission create(Long quizId, Long userId, Long classId,
                                        Integer totalScore, Integer maxScore, String answersJson) {
        return QuizSubmission.builder()
                .quizId(quizId)
                .userId(userId)
                .classId(classId)
                .totalScore(totalScore)
                .maxScore(maxScore)
                .answersJson(answersJson)
                .submittedAt(LocalDateTime.now())
                .build();
    }
}
