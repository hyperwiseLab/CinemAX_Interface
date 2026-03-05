package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 학생 요약
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSummary {
    private Long userId;
    private String userName;
    private BigDecimal progressPct;         // 진도율
    private BigDecimal workHours;           // 학습 시간
    private Long submissionCount;           // 제출 횟수
    private Long passedCount;               // 합격 횟수
    private String activityStatus;          // 활동 상태
}