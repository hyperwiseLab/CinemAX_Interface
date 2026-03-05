package com.cinemax.domain.activity.dto;

import com.cinemax.domain.progress.entity.Progress;
import com.cinemax.global.enums.StudentActivityStatus;
import com.cinemax.global.enums.TaskMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 학생 활동 상태 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "학생 활동 상태 응답")
public class StudentActivityResponse {

    @Schema(description = "학생 ID", example = "1")
    private Long userId;

    @Schema(description = "학생 이름", example = "홍길동")
    private String userName;

    @Schema(description = "학생 이메일", example = "student@example.com")
    private String userEmail;

    @Schema(description = "학생 번호", example = "2021001")
    private String studentNum;

    @Schema(description = "주차별 수업 ID", example = "1")
    private Long weeklySessionId;

    @Schema(description = "초대 ID", example = "1")
    private Long inviteId;

    @Schema(description = "주차 번호", example = "1")
    private Integer weekNo;

    @Schema(description = "진도율 (%)", example = "75.50")
    private BigDecimal progressPct;

    @Schema(description = "활동 상태", example = "ACTIVE")
    private StudentActivityStatus activityStatus;

    @Schema(description = "활동 상태 표시명", example = "🟢 활동중")
    private String activityStatusDisplay;

    @Schema(description = "마지막 활동 시간")
    private LocalDateTime lastActivityTime;

    @Schema(description = "테스트 실패 횟수", example = "0")
    private Integer testFailCount;

    @Schema(description = "완료 일시")
    private LocalDateTime completedDt;

    @Schema(description = "마지막 활동으로부터 경과 시간 (분)", example = "3")
    private Long minutesSinceLastActivity;

    @Schema(description = "활동 모니터 생성 일시")
    private LocalDateTime activityMonitorCreateDt;

    @Schema(description = "사이클 횟수", example = "1")
    private Integer cycleCount;

    @Schema(description = "과제 모드", example = "NORMAL")
    private TaskMode mode;

    // Progress Entity로부터 DTO 생성
    public static StudentActivityResponse from(Progress progress) {
        LocalDateTime now = LocalDateTime.now();
        Long minutesSince = progress.getLastActivityTime() != null
                ? java.time.Duration.between(progress.getLastActivityTime(), now).toMinutes()
                : null;

        return StudentActivityResponse.builder()
                .userId(progress.getUser().getUserId())
                .userName(progress.getUser().getName())
                .userEmail(progress.getUser().getEmail())
                .studentNum(progress.getUser().getStudentNum())
                .weeklySessionId(progress.getWeeklySessionId())
                .inviteId(progress.getWeeklySession() != null ? progress.getWeeklySession().getInviteId() : null)
                .weekNo(progress.getWeeklySession() != null ? progress.getWeeklySession().getWeekNo() : null)
                .progressPct(progress.getProgressPct())
                .activityStatus(progress.getActivityStatus())
                .activityStatusDisplay(progress.getActivityStatus() != null ? progress.getActivityStatus().getDisplayName() : "상태 없음")
                .lastActivityTime(progress.getLastActivityTime())
                .testFailCount(progress.getTestFailCount())
                .completedDt(progress.getCompletedDt())
                .minutesSinceLastActivity(minutesSince)
                .activityMonitorCreateDt(progress.getCreateDt())
                .cycleCount(progress.getCycleCount())
                .mode(progress.getMode())
                .build();
    }
}
