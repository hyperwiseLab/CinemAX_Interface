package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 과제 제출 분석
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentAnalysis {
    private Long totalAssignments;      // 전체 과제 수
    private Long submittedAssignments;  // 제출한 과제 수
    private Long passedAssignments;     // 합격한 과제 수
    private Long failedAssignments;     // 불합격한 과제 수
    private Double passRate;            // 합격률 (%)
    private Double submissionRate;      // 제출률 (%)
    private Integer averageAttempts;    // 평균 제출 시도 횟수
}