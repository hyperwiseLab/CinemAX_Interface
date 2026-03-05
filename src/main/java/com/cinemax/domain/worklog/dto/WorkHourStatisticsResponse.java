package com.cinemax.domain.worklog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 학습 시간 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkHourStatisticsResponse {

    private BigDecimal totalWorkHours;     // 전체 학습 시간 (시간)
    private BigDecimal averageWorkHours;   // 평균 학습 시간 (시간)
    private BigDecimal maxWorkHours;       // 최대 학습 시간 (시간)
    private BigDecimal minWorkHours;       // 최소 학습 시간 (시간)

    /**
     * 통계 데이터로부터 DTO 생성
     */
    public static WorkHourStatisticsResponse of(BigDecimal total, BigDecimal average,
                                                BigDecimal max, BigDecimal min) {
        return WorkHourStatisticsResponse.builder()
                .totalWorkHours(total != null ? total : BigDecimal.ZERO)
                .averageWorkHours(average != null ? average : BigDecimal.ZERO)
                .maxWorkHours(max != null ? max : BigDecimal.ZERO)
                .minWorkHours(min != null ? min : BigDecimal.ZERO)
                .build();
    }
}
