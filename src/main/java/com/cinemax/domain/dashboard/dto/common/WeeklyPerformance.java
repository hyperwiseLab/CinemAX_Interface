package com.cinemax.domain.dashboard.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 주차별 성과 데이터
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주차별 성과 데이터")
public class WeeklyPerformance {

    @Schema(description = "주차 번호", example = "1")
    private Integer weekNumber;

    @Schema(description = "진도율 (%)", example = "80.0")
    private BigDecimal progressRate;

    @Schema(description = "제출 과제 수", example = "5")
    private Long submittedTasks;

    @Schema(description = "합격 과제 수", example = "4")
    private Long passedTasks;

    @Schema(description = "성공률 (%)", example = "80.0")
    private BigDecimal successRate;

    @Schema(description = "학습 시간 (시간)", example = "8.5")
    private BigDecimal studyHours;

    @Schema(description = "평균 점수", example = "85.5")
    private BigDecimal averageScore;
}