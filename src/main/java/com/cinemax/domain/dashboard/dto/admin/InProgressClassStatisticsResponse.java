package com.cinemax.domain.dashboard.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * "진행 중" 상태의 수업들에 대한 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InProgressClassStatisticsResponse {

    // 평균 출석률 (진행 중 수업의 모든 학생에 대한 개인별 출석률 평균)
    private BigDecimal averageAttendanceRate;

    // 평균 성공률 (진행 중 수업의 모든 학생에 대한 개인별 과제 성공률 평균)
    private BigDecimal averageSuccessRate;

    // 진행 중인 수업 수
    private Long inProgressClassCount;

    // 총 수강생 수
    private Long totalStudentCount;

    // 총 차시 수
    private Long totalWeeklySessionCount;

    // 평균 출석률과 평균 성공률을 계산하여 응답 생성
    public static InProgressClassStatisticsResponse of(
            BigDecimal averageAttendanceRate,
            BigDecimal averageSuccessRate,
            Long inProgressClassCount,
            Long totalStudentCount,
            Long totalWeeklySessionCount) {

        return InProgressClassStatisticsResponse.builder()
                .averageAttendanceRate(averageAttendanceRate != null
                    ? averageAttendanceRate.setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO)
                .averageSuccessRate(averageSuccessRate != null
                    ? averageSuccessRate.setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO)
                .inProgressClassCount(inProgressClassCount)
                .totalStudentCount(totalStudentCount)
                .totalWeeklySessionCount(totalWeeklySessionCount)
                .build();
    }
}