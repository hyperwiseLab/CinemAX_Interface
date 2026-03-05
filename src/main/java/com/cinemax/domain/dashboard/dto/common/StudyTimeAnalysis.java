package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 학습 시간 분석
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyTimeAnalysis {
    private BigDecimal totalWorkHours;      // 총 학습 시간
    private BigDecimal averageDailyHours;   // 일평균 학습 시간
    private BigDecimal recentWeekHours;     // 최근 7일 학습 시간
    private List<DailyWorkHour> dailyWorkHours; // 일별 학습 시간
}