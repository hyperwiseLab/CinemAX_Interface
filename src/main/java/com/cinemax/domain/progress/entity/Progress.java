package com.cinemax.domain.progress.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.domain.user.entity.User;
import com.cinemax.global.enums.StudentActivityStatus;
import com.cinemax.global.enums.TaskMode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_PROGRESS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Progress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROGRESS_ID")
    private Long progressId;

    @Column(name = "CLASS_ID")
    private Long classId;

    @Column(name = "WEEKLY_SESSION_ID")
    private Long weeklySessionId;

    @Column(name = "PROGRESS_PCT", nullable = false, precision = 5, scale = 2)
    private BigDecimal progressPct;

    @Column(name = "COMPLETED_DT")
    private LocalDateTime completedDt;

    @Column(name = "LAST_ACTIVITY_TIME")
    private LocalDateTime lastActivityTime;

    @Column(name = "TEST_FAIL_COUNT")
    private Integer testFailCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVITY_STATUS")
    private StudentActivityStatus activityStatus;

    @Column(name = "CYCLE_COUNT")
    private Integer cycleCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "MODE")
    private TaskMode mode;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASS_ID", insertable = false, updatable = false)
    private ClassEntity classEntity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WEEKLY_SESSION_ID", insertable = false, updatable = false)
    private WeeklySession weeklySession;

    // Progress 생성 팩토리 메서드
    @Builder
    public static Progress create(Long classId, Long weeklySessionId, User user, BigDecimal progressPct, Integer cycleCount, TaskMode mode) {
        Progress progress = new Progress();
        progress.classId = classId;
        progress.weeklySessionId = weeklySessionId;
        progress.user = user;
        progress.progressPct = progressPct != null ? progressPct : BigDecimal.ZERO;
        progress.cycleCount = cycleCount != null ? cycleCount : 1;
        progress.mode = mode;
        progress.testFailCount = 0;
        progress.lastActivityTime = LocalDateTime.now();
        progress.activityStatus = StudentActivityStatus.IDLE;
        return progress;
    }

    // 활동 시간 업데이트
    public void updateActivity() {
        this.lastActivityTime = LocalDateTime.now();
        updateActivityStatus();
    }

    // 테스트 실패 카운트 증가
    public void incrementTestFailCount() {
        this.testFailCount = (this.testFailCount != null ? this.testFailCount : 0) + 1;
        updateActivityStatus();
    }

    // 테스트 실패 카운트 초기화
    public void resetTestFailCount() {
        this.testFailCount = 0;
        updateActivityStatus();
    }

    // 진도율 업데이트
    public void updateProgress(BigDecimal progressPct) {
        this.progressPct = progressPct;
        this.lastActivityTime = LocalDateTime.now();

        // 100% 완료 시 완료 처리
        if (progressPct.compareTo(BigDecimal.valueOf(100)) >= 0) {
            this.completedDt = LocalDateTime.now();
            this.activityStatus = StudentActivityStatus.COMPLETED;
        } else {
            updateActivityStatus();
        }
    }

    // 활동 상태 자동 계산 및 업데이트
    public void updateActivityStatus() {
        // 이미 완료된 경우 상태 유지
        if (this.completedDt != null) {
            this.activityStatus = StudentActivityStatus.COMPLETED;
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // lastActivityTime이 null인 경우 IDLE
        if (this.lastActivityTime == null) {
            this.activityStatus = StudentActivityStatus.IDLE;
            return;
        }

        long minutesSinceLastActivity = java.time.Duration.between(this.lastActivityTime, now).toMinutes();

        // 테스트 3회 이상 실패 또는 5분 이상 진도 없음 → 도움필요
        if ((this.testFailCount != null && this.testFailCount >= 3) || minutesSinceLastActivity >= 5) {
            this.activityStatus = StudentActivityStatus.NEED_HELP;
        }
        // 최근 5분 이내 활동 → 활동중
        else if (minutesSinceLastActivity < 5) {
            this.activityStatus = StudentActivityStatus.ACTIVE;
        }
        // 그 외 → 대기중
        else {
            this.activityStatus = StudentActivityStatus.IDLE;
        }
    }

    // 수동으로 완료 처리
    public void markAsCompleted() {
        this.completedDt = LocalDateTime.now();
        this.activityStatus = StudentActivityStatus.COMPLETED;
        this.progressPct = BigDecimal.valueOf(100);
    }

    // 수동으로 활동 상태 설정
    public void setActivityStatus(StudentActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
        this.lastActivityTime = LocalDateTime.now();

        // COMPLETED 상태로 변경 시 완료 처리
        if (activityStatus == StudentActivityStatus.COMPLETED && this.completedDt == null) {
            this.completedDt = LocalDateTime.now();
            this.progressPct = BigDecimal.valueOf(100);
        }
    }

    // 모드 업데이트
    public void updateMode(TaskMode mode) {
        this.mode = mode;
    }
}
