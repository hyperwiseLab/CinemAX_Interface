package com.cinemax.domain.dashboard.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 통합 대시보드 개요 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewResponse {

    // 학생 통계
    private Long totalStudents;
    private Long activeStudents;
    private Long completedStudents;
    private Long needHelpCount;

    // 진도 통계
    private BigDecimal averageProgress;
    private BigDecimal minProgress;
    private BigDecimal maxProgress;

    // 질문 통계
    private Long totalQuestions;
    private Long unansweredQuestions;
    private Long highUrgencyQuestions;

    // 제출 통계
    private Long totalSubmissions;
    private Long passedSubmissions;
    private Long failedSubmissions;
    private BigDecimal passRate;

    public static DashboardOverviewResponse of(
            Long totalStudents,
            Long activeStudents,
            Long completedStudents,
            Long needHelpCount,
            BigDecimal averageProgress,
            BigDecimal minProgress,
            BigDecimal maxProgress,
            Long totalQuestions,
            Long unansweredQuestions,
            Long highUrgencyQuestions,
            Long totalSubmissions,
            Long passedSubmissions,
            Long failedSubmissions,
            BigDecimal passRate) {

        return DashboardOverviewResponse.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .completedStudents(completedStudents)
                .needHelpCount(needHelpCount)
                .averageProgress(averageProgress)
                .minProgress(minProgress)
                .maxProgress(maxProgress)
                .totalQuestions(totalQuestions)
                .unansweredQuestions(unansweredQuestions)
                .highUrgencyQuestions(highUrgencyQuestions)
                .totalSubmissions(totalSubmissions)
                .passedSubmissions(passedSubmissions)
                .failedSubmissions(failedSubmissions)
                .passRate(passRate)
                .build();
    }
}
