package com.cinemax.domain.dashboard.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 레이더 차트 데이터
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "레이더 차트 데이터")
public class RadarChartData {

    @Schema(description = "구문/문법 이해도 (0-100)", example = "85.0")
    private BigDecimal syntaxUnderstanding;

    @Schema(description = "알고리즘 설계 능력 (0-100)", example = "75.0")
    private BigDecimal algorithmDesign;

    @Schema(description = "디버깅 능력 (0-100)", example = "80.0")
    private BigDecimal debuggingSkill;

    @Schema(description = "코드 품질 (0-100)", example = "70.0")
    private BigDecimal codeQuality;

    @Schema(description = "문제 해결 속도 (0-100)", example = "78.0")
    private BigDecimal solvingSpeed;

    @Schema(description = "코드 효율성 (0-100)", example = "72.0")
    private BigDecimal codeEfficiency;
}