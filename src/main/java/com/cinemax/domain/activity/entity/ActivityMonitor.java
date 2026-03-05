package com.cinemax.domain.activity.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.global.enums.ActivityAction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_ACTIVITY_MONITOR")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ActivityMonitor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MONITOR_ID")
    private Long monitorId;

    @Column(name = "WEEKLY_SESSION_ID")
    private Long weeklySessionId;

    @Column(name = "INVITE_ID")
    private Long inviteId;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "LAST_SEEN_DT", nullable = false)
    private LocalDateTime lastSeenDt;

    @Enumerated(EnumType.STRING)
    @Column(name = "LAST_ACTION", nullable = false)
    private ActivityAction lastAction;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WEEKLY_SESSION_ID", insertable = false, updatable = false)
    private WeeklySession weeklySession;

    // ===== 비즈니스 메서드 =====

    // 활동 로그 생성
    public static ActivityMonitor create(Long weeklySessionId, Long inviteId, Long taskId, ActivityAction action) {
        return ActivityMonitor.builder()
                .weeklySessionId(weeklySessionId)
                .inviteId(inviteId)
                .taskId(taskId)
                .lastSeenDt(LocalDateTime.now())
                .lastAction(action)
                .build();
    }

    // 활동 로그 생성 (WeeklySession 포함)
    public static ActivityMonitor create(Long weeklySessionId, Long inviteId, Long taskId, ActivityAction action, WeeklySession weeklySession) {
        return ActivityMonitor.builder()
                .weeklySessionId(weeklySessionId)
                .inviteId(inviteId)
                .taskId(taskId)
                .lastSeenDt(LocalDateTime.now())
                .lastAction(action)
                .weeklySession(weeklySession)
                .build();
    }

    // 활동 기록 업데이트
    public void recordActivity(ActivityAction action) {
        this.lastSeenDt = LocalDateTime.now();
        this.lastAction = action;
    }

    // 코드 저장 활동 기록
    public void recordCodeSave() {
        recordActivity(ActivityAction.CODE_SAVE);
    }

    // 코드 실행 활동 기록
    public void recordCodeRun() {
        recordActivity(ActivityAction.CODE_RUN);
    }

    // 테스트 실행 활동 기록
    public void recordTestRun() {
        recordActivity(ActivityAction.TEST_RUN);
    }

    // 테스트 통과 활동 기록
    public void recordTestPass() {
        recordActivity(ActivityAction.TEST_PASS);
    }

    // 테스트 실패 활동 기록
    public void recordTestFail() {
        recordActivity(ActivityAction.TEST_FAIL);
    }

    // 페이지 조회 활동 기록
    public void recordPageView() {
        recordActivity(ActivityAction.PAGE_VIEW);
    }

    // 강의 조회 활동 기록
    public void recordLectureView() {
        recordActivity(ActivityAction.LECTURE_VIEW);
    }

    // 힌트 조회 활동 기록
    public void recordHintView() {
        recordActivity(ActivityAction.HINT_VIEW);
    }

    // 질문 제출 활동 기록
    public void recordQuestionSubmit() {
        recordActivity(ActivityAction.QUESTION_SUBMIT);
    }

    // 마지막 활동 시간으로부터 경과 시간 계산 (분)
    public long getMinutesSinceLastActivity() {
        return java.time.Duration.between(this.lastSeenDt, LocalDateTime.now()).toMinutes();
    }

    // 특정 시간 이내에 활동했는지 확인
    public boolean isActiveWithinMinutes(int minutes) {
        return getMinutesSinceLastActivity() < minutes;
    }
}
