package com.cinemax.domain.activity.dto;

import com.cinemax.domain.progress.entity.Progress;
import com.cinemax.global.enums.StudentActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * WebSocket 활동 상태 변경 메시지 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "활동 상태 변경 WebSocket 메시지")
public class ActivityStatusMessage {

    @Schema(description = "학생 ID", example = "1")
    private Long userId;

    @Schema(description = "학생 이름", example = "홍길동")
    private String userName;

    @Schema(description = "주차별 수업 ID", example = "1")
    private Long weeklySessionId;

    @Schema(description = "이전 상태", example = "ACTIVE")
    private StudentActivityStatus previousStatus;

    @Schema(description = "현재 상태", example = "NEED_HELP")
    private StudentActivityStatus currentStatus;

    @Schema(description = "현재 상태 표시명", example = "🔴 도움필요")
    private String currentStatusDisplay;

    @Schema(description = "진도율 (%)", example = "75.50")
    private BigDecimal progressPct;

    @Schema(description = "테스트 실패 횟수", example = "3")
    private Integer testFailCount;

    @Schema(description = "메시지 전송 시각")
    private LocalDateTime timestamp;

    // Progress와 이전 상태로부터 메시지 생성
    public static ActivityStatusMessage from(Progress progress, StudentActivityStatus previousStatus) {
        return ActivityStatusMessage.builder()
                .userId(progress.getUser().getUserId())
                .userName(progress.getUser().getName())
                .weeklySessionId(progress.getWeeklySessionId())
                .previousStatus(previousStatus)
                .currentStatus(progress.getActivityStatus())
                .currentStatusDisplay(progress.getActivityStatus() != null ? progress.getActivityStatus().getDisplayName() : "상태 없음")
                .progressPct(progress.getProgressPct())
                .testFailCount(progress.getTestFailCount())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
