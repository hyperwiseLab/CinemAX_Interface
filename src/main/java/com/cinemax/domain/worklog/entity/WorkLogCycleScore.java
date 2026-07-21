package com.cinemax.domain.worklog.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 업무일지의 사이클별 평가 점수
 *
 * 주차 단위 점수(WorkLog.difficultyLevel / proficiencyLevel)는 이 값들의 평균으로 파생된다.
 * 원본을 여기에 남겨야 "어느 사이클을 어려워했는지" 사후 분석이 가능하다.
 */
@Entity
@Table(
        name = "TBL_WORK_LOG_CYCLE_SCORE",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_WORK_LOG_CYCLE",
                columnNames = {"WORK_LOG_ID", "CYCLE_ID"}
        )
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WorkLogCycleScore extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORK_LOG_CYCLE_SCORE_ID")
    private Long workLogCycleScoreId;

    // FK 는 이 연관관계가 관리한다. (스칼라 workLogId 를 따로 두면
    // 부모 저장 전 id 가 null 이라 점수가 고아로 저장된다)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_LOG_ID")
    private WorkLog workLog;

    // 사이클은 단순 참조값으로만 저장한다. 읽기전용 @ManyToOne 연관관계를 두면
    // FK 제약이 생겨 커리큘럼 개편 시 사이클 삭제를 막으므로 두지 않는다.
    @Column(name = "CYCLE_ID", nullable = false)
    private Long cycleId;

    // 개념 이해도 1~5
    @Column(name = "CONCEPT_SCORE", nullable = false)
    private Integer conceptScore;

    // 코드 활용도 1~5
    @Column(name = "APPLICATION_SCORE", nullable = false)
    private Integer applicationScore;

    // 화면 표시 순서
    @Column(name = "ORDER_NO", nullable = false)
    private Integer orderNo;

    // ===== 비즈니스 메서드 =====

    public static WorkLogCycleScore create(Long cycleId, Integer conceptScore,
                                           Integer applicationScore, Integer orderNo) {
        validateScore(conceptScore, "개념 이해도");
        validateScore(applicationScore, "코드 활용도");

        return WorkLogCycleScore.builder()
                .cycleId(cycleId)
                .conceptScore(conceptScore)
                .applicationScore(applicationScore)
                .orderNo(orderNo)
                .build();
    }

    // 부모 WorkLog 연결 (양방향)
    public void assignWorkLog(WorkLog workLog) {
        this.workLog = workLog;
    }

    private static void validateScore(Integer score, String label) {
        if (score == null || score < 1 || score > 5) {
            throw new IllegalArgumentException(label + "는 1~5 사이의 값이어야 합니다.");
        }
    }
}
