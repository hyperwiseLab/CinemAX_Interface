package com.cinemax.domain.dashboard.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 차시별 성장률 분석 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "차시별 성장률 분석")
public class WeeklyGrowthAnalysisResponse {

    @Schema(description = "주차 번호", example = "1")
    private Integer weekNo;

    @Schema(description = "과제 성공률", example = "85.5")
    private BigDecimal successRate;

    @Schema(description = "평균 제출 시간 (분)", example = "45.5")
    private BigDecimal averageSubmissionTimeMinutes;

    @Schema(description = "평균 재도전 횟수", example = "2.5")
    private BigDecimal averageRetryCount;

    @Schema(description = "완료한 학생 수", example = "25")
    private Long completedStudents;

    @Schema(description = "전체 학생 수", example = "30")
    private Long totalStudents;

    @Schema(description = "시작 시간")
    private LocalDateTime startDt;

    @Schema(description = "종료 시간")
    private LocalDateTime endDt;
}
