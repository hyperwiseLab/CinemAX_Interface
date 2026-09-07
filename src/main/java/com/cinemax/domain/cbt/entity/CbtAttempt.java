package com.cinemax.domain.cbt.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CBT 회차별 응시 기록. 학생별로 무제한 반복 응시 가능하며 매 회차가 한 행으로 남는다.
 * 문항/선택/정오답은 answersJson 에 자기완결 스냅샷으로 저장되어
 * 이후 문제가 수정/삭제되어도 과거 회차 복기가 깨지지 않는다.
 */
@Entity
@Table(name = "TBL_CBT_ATTEMPT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CbtAttempt extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTEMPT_ID")
    private Long attemptId;

    // 단순 참조값 (FK 제약 없음)
    @Column(name = "CLASS_ID", nullable = false)
    private Long classId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    // 학생별 회차 (1, 2, 3, ...) - 전체 모의고사/주차별 각각 독립 채번
    @Column(name = "ROUND_NO", nullable = false)
    private Integer roundNo;

    // null = 전체 모의고사, 값 있음 = 해당 주차 CBT
    @Column(name = "WEEK_NO")
    private Integer weekNo;

    // 총점 (100점 만점)
    @Column(name = "TOTAL_SCORE", nullable = false)
    private Double totalScore;

    // 최종 합격 여부 (과락 없음 + 총점 >= 합격점)
    @Column(name = "PASS_YN", nullable = false)
    private Boolean passYn;

    // 과목별 점수/과락 스냅샷: [{subjectId, subjectNm, score, maxScore, cutScore, cutYn, failed, correctCount, questionCount}]
    @Column(name = "SUBJECT_SCORES_JSON", columnDefinition = "JSON")
    private String subjectScoresJson;

    // 문항별 스냅샷: [{questionId, subjectId, subjectNm, content, explanation, options, selectedOrderNo, correctOrderNo, correct}]
    @Column(name = "ANSWERS_JSON", columnDefinition = "JSON")
    private String answersJson;

    @Column(name = "SUBMITTED_AT", nullable = false)
    private LocalDateTime submittedAt;

    @Builder
    public CbtAttempt(Long classId, Long userId, Integer roundNo, Integer weekNo, Double totalScore,
                      Boolean passYn, String subjectScoresJson, String answersJson,
                      LocalDateTime submittedAt) {
        this.classId = classId;
        this.userId = userId;
        this.roundNo = roundNo;
        this.weekNo = weekNo;
        this.totalScore = totalScore;
        this.passYn = passYn;
        this.subjectScoresJson = subjectScoresJson;
        this.answersJson = answersJson;
        this.submittedAt = submittedAt;
    }

    public static CbtAttempt create(Long classId, Long userId, Integer roundNo, Double totalScore,
                                    Boolean passYn, String subjectScoresJson, String answersJson) {
        return create(classId, userId, roundNo, null, totalScore, passYn, subjectScoresJson, answersJson);
    }

    // 주차별 CBT 응시 기록 (weekNo != null)
    public static CbtAttempt create(Long classId, Long userId, Integer roundNo, Integer weekNo,
                                    Double totalScore, Boolean passYn, String subjectScoresJson,
                                    String answersJson) {
        return CbtAttempt.builder()
                .classId(classId)
                .userId(userId)
                .roundNo(roundNo)
                .weekNo(weekNo)
                .totalScore(totalScore)
                .passYn(passYn)
                .subjectScoresJson(subjectScoresJson)
                .answersJson(answersJson)
                .submittedAt(LocalDateTime.now())
                .build();
    }
}
