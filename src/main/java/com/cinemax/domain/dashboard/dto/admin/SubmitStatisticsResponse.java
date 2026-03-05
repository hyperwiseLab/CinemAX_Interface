package com.cinemax.domain.dashboard.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 제출 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitStatisticsResponse {

    private Long totalSubmissions;        // 전체 제출 수
    private Long passedSubmissions;       // 합격 제출 수
    private Long failedSubmissions;       // 불합격 제출 수
    private Long submittedStudents;       // 제출한 학생 수
    private Long passedStudents;          // 합격한 학생 수
    private Double passRate;              // 합격률 (%)

    // 통계 데이터로부터 DTO 생성
    public static SubmitStatisticsResponse of(Long totalSubmissions, Long passedSubmissions,
                                               Long failedSubmissions, Long submittedStudents,
                                               Long passedStudents) {
        double passRate = 0.0;
        if (totalSubmissions != null && totalSubmissions > 0) {
            passRate = (passedSubmissions.doubleValue() / totalSubmissions.doubleValue()) * 100;
        }

        return SubmitStatisticsResponse.builder()
                .totalSubmissions(totalSubmissions != null ? totalSubmissions : 0L)
                .passedSubmissions(passedSubmissions != null ? passedSubmissions : 0L)
                .failedSubmissions(failedSubmissions != null ? failedSubmissions : 0L)
                .submittedStudents(submittedStudents != null ? submittedStudents : 0L)
                .passedStudents(passedStudents != null ? passedStudents : 0L)
                .passRate(Math.round(passRate * 100.0) / 100.0)  // 소수점 2자리
                .build();
    }
}
