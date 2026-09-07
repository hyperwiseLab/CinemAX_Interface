package com.cinemax.domain.cbt.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주차별 CBT 설정. 반 + 주차 단위로 출제 문항 수와 합격 점수를 관리자가 지정한다.
 * 전체 모의고사(TBL_CBT_SUBJECT 의 과목 배점 100점 체계)와 달리
 * 주차별 CBT 는 과목 구분 없이 "해당 주차 문항 중 N개 랜덤 출제 → 정답률(%)" 로 채점한다.
 */
@Entity
@Table(name = "TBL_CBT_WEEK_CONFIG",
        uniqueConstraints = @UniqueConstraint(name = "UK_CBT_WEEK", columnNames = {"CLASS_ID", "WEEK_NO"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CbtWeekConfig extends BaseTimeEntity {

    // 미설정 주차에 적용되는 기본값
    public static final int DEFAULT_QUESTION_COUNT = 20;
    public static final double DEFAULT_PASS_SCORE = 60.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WEEK_CONFIG_ID")
    private Long weekConfigId;

    // 단순 참조값 (FK 제약 없음)
    @Column(name = "CLASS_ID", nullable = false)
    private Long classId;

    @Column(name = "WEEK_NO", nullable = false)
    private Integer weekNo;

    // 해당 주차에서 랜덤 출제할 문항 수 (보유 문항이 부족하면 보유분 전체로 축소 출제)
    @Column(name = "QUESTION_COUNT", nullable = false)
    private Integer questionCount;

    // 합격 기준 정답률 (0~100)
    @Column(name = "PASS_SCORE", nullable = false)
    private Double passScore;

    @Column(name = "USE_YN", nullable = false)
    private Boolean useYn;

    @Builder
    public CbtWeekConfig(Long classId, Integer weekNo, Integer questionCount,
                         Double passScore, Boolean useYn) {
        this.classId = classId;
        this.weekNo = weekNo;
        this.questionCount = questionCount;
        this.passScore = passScore;
        this.useYn = useYn;
    }

    public static CbtWeekConfig createDefault(Long classId, Integer weekNo) {
        return CbtWeekConfig.builder()
                .classId(classId)
                .weekNo(weekNo)
                .questionCount(DEFAULT_QUESTION_COUNT)
                .passScore(DEFAULT_PASS_SCORE)
                .useYn(true)
                .build();
    }

    public void updateSettings(Integer questionCount, Double passScore, Boolean useYn) {
        this.questionCount = questionCount;
        this.passScore = passScore;
        this.useYn = useYn;
    }
}
