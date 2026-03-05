package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 학습 진도 분석
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressAnalysis {
    private BigDecimal currentProgress;     // 현재 진도율 (%)
    private BigDecimal averageProgress;     // 평균 진도율 (%)
    private String activityStatus;          // 활동 상태 (ACTIVE/IDLE/NEED_HELP/COMPLETED)
    private LocalDateTime lastActivityTime; // 마지막 활동 시간
    private Integer testFailCount;          // 테스트 실패 횟수
}