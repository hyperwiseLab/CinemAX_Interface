package com.cinemax.domain.worklog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 업무일지 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogStatisticsResponse {

    private Long userId;
    private Long weeklySessionId;
    private Integer weekNo;

    // 전체 통계
    private Long totalLogCount;
    private BigDecimal totalWorkHours;
    private BigDecimal averageWorkHours;
    private BigDecimal averageDifficulty;

    // 주간 통계
    private Long weeklyLogCount;
    private BigDecimal weeklyWorkHours;

    // 최근 로그 정보
    private String latestContent;
    private String latestAchievements;
}
