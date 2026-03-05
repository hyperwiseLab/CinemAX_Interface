package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 제출 통계
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionStatistics {
    private Long totalSubmissions;      // 전체 제출 수
    private Long passedSubmissions;     // 합격 제출 수
    private Long failedSubmissions;     // 불합격 제출 수
    private Long submittedStudents;     // 제출한 학생 수
    private Long passedStudents;        // 합격한 학생 수
    private Double passRate;            // 합격률 (%)
    private Double submissionRate;      // 제출률 (%)
}