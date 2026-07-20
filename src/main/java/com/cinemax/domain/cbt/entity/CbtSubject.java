package com.cinemax.domain.cbt.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CBT 과목. 반(자격증) 아래에서 배점/출제수/과락 기준을 갖는다.
 * 과목 배점 = pointPerQuestion * questionCount (전 과목 합계는 정확히 100점이어야 한다)
 */
@Entity
@Table(name = "TBL_CBT_SUBJECT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CbtSubject extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUBJECT_ID")
    private Long subjectId;

    // 단순 참조값 (FK 제약 없음)
    @Column(name = "CLASS_ID", nullable = false)
    private Long classId;

    @Column(name = "SUBJECT_NM", nullable = false, length = 100)
    private String subjectNm;

    @Column(name = "ORDER_NO", nullable = false)
    private Integer orderNo;

    // 문제당 점수 (소수점 허용, 예: 2.0 / 1.75)
    @Column(name = "POINT_PER_QUESTION", nullable = false)
    private Double pointPerQuestion;

    // 회차당 랜덤 출제 문항 수
    @Column(name = "QUESTION_COUNT", nullable = false)
    private Integer questionCount;

    // 과락 기준점 (배점 내 절대 점수)
    @Column(name = "CUT_SCORE", nullable = false)
    private Double cutScore;

    // 과락 적용 여부 (false 면 이 과목은 과락 판정에서 제외)
    @Column(name = "CUT_YN", nullable = false)
    private Boolean cutYn;

    @Column(name = "USE_YN", nullable = false)
    private Boolean useYn;

    @Builder
    public CbtSubject(Long classId, String subjectNm, Integer orderNo, Double pointPerQuestion,
                      Integer questionCount, Double cutScore, Boolean cutYn, Boolean useYn) {
        this.classId = classId;
        this.subjectNm = subjectNm;
        this.orderNo = orderNo;
        this.pointPerQuestion = pointPerQuestion;
        this.questionCount = questionCount;
        this.cutScore = cutScore;
        this.cutYn = cutYn;
        this.useYn = useYn;
    }

    // 업로드 시 미등록 과목 자동 생성용 (배점 미설정 상태 기본값)
    public static CbtSubject createDefault(Long classId, String subjectNm, Integer orderNo) {
        return CbtSubject.builder()
                .classId(classId)
                .subjectNm(subjectNm)
                .orderNo(orderNo)
                .pointPerQuestion(0.0)
                .questionCount(0)
                .cutScore(0.0)
                .cutYn(true)
                .useYn(true)
                .build();
    }

    public void updateSettings(String subjectNm, Integer orderNo, Double pointPerQuestion,
                               Integer questionCount, Double cutScore, Boolean cutYn, Boolean useYn) {
        this.subjectNm = subjectNm;
        this.orderNo = orderNo;
        this.pointPerQuestion = pointPerQuestion;
        this.questionCount = questionCount;
        this.cutScore = cutScore;
        this.cutYn = cutYn;
        this.useYn = useYn;
    }

    // 과목 배점 = 문제당 점수 x 출제수
    public double maxScore() {
        return pointPerQuestion * questionCount;
    }
}
