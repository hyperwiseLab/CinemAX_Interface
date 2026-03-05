package com.cinemax.domain.dashboard.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 활동 통계 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "활동 통계")
public class ActivityStatistics {

    @Schema(description = "총 코드 제출 수")
    private Long totalSubmissions;

    @Schema(description = "오늘 코드 제출 수")
    private Long todaySubmissions;

    @Schema(description = "총 질문 수")
    private Long totalQuestions;

    @Schema(description = "미답변 질문 수")
    private Long unansweredQuestions;

    @Schema(description = "총 알림 발송 수")
    private Long totalNotifications;

    @Schema(description = "평균 과제 합격률 (%)")
    private BigDecimal averagePassRate;

    @Schema(description = "활성 학생 비율 (%)")
    private BigDecimal activeStudentRate;
}
