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
@Schema(description = "기술 영역")
public class SkillArea {

    @Schema(description = "영역 이름", example = "변수 선언")
    private String areaName;

    @Schema(description = "성공률", example = "95.5")
    private BigDecimal successRate;

    @Schema(description = "평균 시도 횟수", example = "1.5")
    private BigDecimal averageAttempts;

    @Schema(description = "강점/약점 설명")
    private String description;
}