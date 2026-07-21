package com.cinemax.domain.classes.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.task.entity.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_CLASS_SUBMIT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClassSubmit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUBMIT_ID")
    private Long submitId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "CLASS_ID")
    private Long classId;

    @Column(name = "CYCLE_ID")
    private Long cycleId;

    @Column(name = "WEEKLY_SESSION_ID")
    private Long weeklySessionId;

    @Column(name = "SUBMIT_AT", nullable = false)
    private LocalDateTime submitAt;

    @Column(name = "RESULT")
    private Boolean result;

    @Column(name = "SCORE", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "DETAIL_JSON", columnDefinition = "JSON")
    private String detailJson;

    @Column(name = "IS_FIRST_EVAL", nullable = false)
    private Boolean isFirstEval;

    @Column(name = "SUBMIT_NUM")
    private Integer submitNum;

    @Column(name = "SUBMIT_YN", nullable = false)
    private Boolean submitYn;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", insertable = false, updatable = false)
    private Task task;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASS_ID", insertable = false, updatable = false)
    private ClassEntity classEntity;

    @Builder
    public ClassSubmit(Long userId, Long taskId, Long classId, Long cycleId, Long weeklySessionId, LocalDateTime submitAt, Boolean result,
                       BigDecimal score, String detailJson, Boolean isFirstEval, Integer submitNum,
                       Boolean submitYn) {
        this.userId = userId;
        this.taskId = taskId;
        this.classId = classId;
        this.cycleId = cycleId;
        this.weeklySessionId = weeklySessionId;
        this.submitAt = submitAt;
        this.result = result;
        this.score = score;
        this.detailJson = detailJson;
        this.isFirstEval = isFirstEval;
        this.submitNum = submitNum;
        this.submitYn = submitYn;
    }

    // 정적 팩토리 메서드
    public static ClassSubmit create(Long userId, Long taskId, Long classId, Long cycleId, Long weeklySessionId,
                                     Boolean result, BigDecimal score, String detailJson,
                                     Boolean isFirstEval, Integer submitNum) {
        return ClassSubmit.builder()
                .userId(userId)
                .taskId(taskId)
                .classId(classId)
                .cycleId(cycleId)
                .weeklySessionId(weeklySessionId)
                .submitAt(LocalDateTime.now())
                .result(result)
                .score(score)
                .detailJson(detailJson)
                .isFirstEval(isFirstEval)
                .submitNum(submitNum)
                .submitYn(true)
                .build();
    }

    // 재제출 덮어쓰기: 이전 제출을 무효 처리 (이력은 행으로 보존, 성적·통계에서 제외)
    public void invalidate() {
        this.submitYn = false;
    }
}
