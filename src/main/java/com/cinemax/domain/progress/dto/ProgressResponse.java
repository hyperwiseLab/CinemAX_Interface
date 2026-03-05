package com.cinemax.domain.progress.dto;

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
 * 진도 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "진도 응답")
public class ProgressResponse {

    @Schema(description = "진도 ID", example = "1")
    private Long progressId;

    @Schema(description = "수업 ID", example = "1")
    private Long classId;

    @Schema(description = "주차별 수업 ID", example = "1")
    private Long weeklySessionId;

    @Schema(description = "주차 번호", example = "1")
    private Integer weekNo;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "사용자 이메일", example = "student@example.com")
    private String userEmail;

    @Schema(description = "학생 번호", example = "2021001")
    private String studentNum;

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

    @Schema(description = "학습 반복 횟수", example = "1")
    private Integer cycleCount;

    @Schema(description = "문제 모드", example = "ADVANCE")
    private TaskMode mode;

    @Schema(description = "완료 일시")
    private LocalDateTime completedDt;

    @Schema(description = "생성 일시")
    private LocalDateTime createDt;

    @Schema(description = "수정 일시")
    private LocalDateTime updateDt;

    // Progress Entity로부터 DTO 생성
    public static ProgressResponse from(Progress progress) {
        return ProgressResponse.builder()
                .progressId(progress.getProgressId())
                .classId(progress.getClassId())
                .weeklySessionId(progress.getWeeklySessionId())
                .weekNo(progress.getWeeklySession() != null ? progress.getWeeklySession().getWeekNo() : null)
                .userId(progress.getUser().getUserId())
                .userName(progress.getUser().getName())
                .userEmail(progress.getUser().getEmail())
                .studentNum(progress.getUser().getStudentNum())
                .progressPct(progress.getProgressPct())
                .activityStatus(progress.getActivityStatus())
                .activityStatusDisplay(progress.getActivityStatus() != null
                        ? progress.getActivityStatus().getDisplayName()
                        : "상태 없음")
                .lastActivityTime(progress.getLastActivityTime())
                .testFailCount(progress.getTestFailCount())
                .cycleCount(progress.getCycleCount())
                .mode(progress.getMode())
                .completedDt(progress.getCompletedDt())
                .createDt(progress.getCreateDt())
                .updateDt(progress.getUpdateDt())
                .build();
    }
}
