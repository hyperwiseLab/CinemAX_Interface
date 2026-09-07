package com.cinemax.domain.cbt.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * CBT 문제 은행 문항 (객관식). 반 + 과목에 소속되며 회차마다 랜덤 출제 대상이 된다.
 */
@Entity
@Table(name = "TBL_CBT_QUESTION", indexes = {
        @Index(name = "IDX_CBT_Q_WEEK", columnList = "CLASS_ID, WEEK_NO, USE_YN")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CbtQuestion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_ID")
    private Long questionId;

    // 단순 참조값 (FK 제약 없음)
    @Column(name = "CLASS_ID", nullable = false)
    private Long classId;

    @Column(name = "SUBJECT_ID", nullable = false)
    private Long subjectId;

    // 커리큘럼 주차 (1~N). null 이면 주차 미지정으로 주차별 CBT 출제 대상에서 제외되고
    // 전체 모의고사에는 그대로 출제된다. (기존 문항 호환을 위해 nullable)
    @Column(name = "WEEK_NO")
    private Integer weekNo;

    // 지문 (코드 블록 포함 가능)
    @Column(name = "CONTENT", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "EXPLANATION", length = 1000)
    private String explanation;

    @Column(name = "USE_YN", nullable = false)
    private Boolean useYn;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CbtQuestionOption> options = new ArrayList<>();

    public static CbtQuestion create(Long classId, Long subjectId, Integer weekNo, String content,
                                     String explanation, Long createdBy) {
        return CbtQuestion.builder()
                .classId(classId)
                .subjectId(subjectId)
                .weekNo(weekNo)
                .content(content)
                .explanation(explanation)
                .useYn(true)
                .createdBy(createdBy)
                .build();
    }

    public void addOption(CbtQuestionOption option) {
        this.options.add(option);
        option.assignQuestion(this);
    }

    public void updateInfo(Long subjectId, Integer weekNo, String content, String explanation, Boolean useYn) {
        this.subjectId = subjectId;
        this.weekNo = weekNo;
        this.content = content;
        this.explanation = explanation;
        this.useYn = useYn;
    }

    public void replaceOptions(List<CbtQuestionOption> newOptions) {
        this.options.clear();
        if (newOptions != null) {
            for (CbtQuestionOption option : newOptions) {
                addOption(option);
            }
        }
    }

    // 정답 보기의 orderNo (채점 기준값)
    public Integer correctOrderNo() {
        return options.stream()
                .filter(CbtQuestionOption::getCorrectYn)
                .map(CbtQuestionOption::getOrderNo)
                .findFirst()
                .orElse(null);
    }
}
