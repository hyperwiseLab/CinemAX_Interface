package com.cinemax.domain.dashboard.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 개념별 이해도
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "개념별 이해도")
public class ConceptUnderstanding {

    @Schema(description = "개념명", example = "반복문")
    private String concept;

    @Schema(description = "이해도 점수 (0-100)", example = "90.0")
    private BigDecimal understandingScore;

    @Schema(description = "완료한 문제 수", example = "10")
    private Long completedProblems;

    @Schema(description = "평균 점수", example = "85.5")
    private BigDecimal averageScore;

    @Schema(description = "강점 여부", example = "true")
    private Boolean isStrength;
}