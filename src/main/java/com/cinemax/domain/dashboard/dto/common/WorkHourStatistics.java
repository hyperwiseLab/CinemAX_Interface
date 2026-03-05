package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 학습 시간 통계
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkHourStatistics {
    private BigDecimal totalWorkHours;      // 전체 학습 시간
    private BigDecimal averageWorkHours;    // 평균 학습 시간
    private BigDecimal maxWorkHours;        // 최대 학습 시간
    private BigDecimal minWorkHours;        // 최소 학습 시간
}