package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 일별 학습 시간
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyWorkHour {
    private String date;
    private BigDecimal hours;
}