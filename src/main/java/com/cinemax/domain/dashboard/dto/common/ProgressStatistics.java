package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 진도 통계
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressStatistics {
    private BigDecimal averageProgress;     // 평균 진도율 (%)
    private BigDecimal maxProgress;         // 최대 진도율 (%)
    private BigDecimal minProgress;         // 최소 진도율 (%)
}