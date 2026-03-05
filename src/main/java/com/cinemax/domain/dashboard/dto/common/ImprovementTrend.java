package com.cinemax.domain.dashboard.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "개선 추세")
public class ImprovementTrend {

    @Schema(description = "영역 이름", example = "반복문")
    private String areaName;

    @Schema(description = "이전 성공률", example = "60.0")
    private BigDecimal previousRate;

    @Schema(description = "현재 성공률", example = "75.0")
    private BigDecimal currentRate;

    @Schema(description = "개선율", example = "25.0")
    private BigDecimal improvementRate;
}