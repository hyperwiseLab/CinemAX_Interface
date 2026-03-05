package com.cinemax.domain.dashboard.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 언어별 숙련도
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "언어별 숙련도")
public class LanguageProficiency {

    @Schema(description = "프로그래밍 언어", example = "Java")
    private String language;

    @Schema(description = "숙련도 점수 (0-100)", example = "85.5")
    private BigDecimal proficiencyScore;

    @Schema(description = "완료한 문제 수", example = "25")
    private Long completedProblems;

    @Schema(description = "성공률 (%)", example = "80.0")
    private BigDecimal successRate;

    @Schema(description = "레벨", example = "INTERMEDIATE")
    private String level; // BEGINNER, ADVANCED
}