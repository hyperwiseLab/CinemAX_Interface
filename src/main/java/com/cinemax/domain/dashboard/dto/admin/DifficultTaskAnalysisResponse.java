package com.cinemax.domain.dashboard.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 어려운 과제 분석 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "어려운 과제 분석")
public class DifficultTaskAnalysisResponse {

    @Schema(description = "과제 ID", example = "1")
    private Long taskId;

    @Schema(description = "난이도 순위", example = "1")
    private Integer difficultyRank;

    @Schema(description = "과제 이름", example = "변수 선언 실습")
    private String taskName;

    @Schema(description = "성공률", example = "45.5")
    private BigDecimal successRate;

    @Schema(description = "평균 시도 횟수", example = "5.5")
    private BigDecimal averageAttempts;

    @Schema(description = "평균 소요 시간 (분)", example = "75.5")
    private BigDecimal averageTimeMinutes;

    @Schema(description = "도움 요청 횟수", example = "15")
    private Long helpRequestCount;
}
