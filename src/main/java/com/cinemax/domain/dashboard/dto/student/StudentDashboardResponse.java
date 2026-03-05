package com.cinemax.domain.dashboard.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 학생 대시보드 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardResponse {

    // 수업 정보
    private Long totalClasses;              // 전체 수강 중인 수업 수
    private Long activeWeeklySessions;      // 진행 중인 주차별 세션 수

    // 과제 제출 현황
    private Long totalAssignments;          // 전체 과제 수
    private Long submittedAssignments;      // 제출한 과제 수
    private Long passedAssignments;         // 합격한 과제 수
    private Long failedAssignments;         // 불합격한 과제 수
    private Long pendingAssignments;        // 미제출 과제 수

    // 학습 진도
    private BigDecimal averageProgress;     // 평균 진도율 (%)

    // 알림 정보
    private Long unreadNotifications;       // 미확인 알림 수

    // 질문/답변 정보
    private Long totalQuestions;            // 내가 등록한 질문 수
    private Long answeredQuestions;         // 답변 받은 질문 수
    private Long unansweredQuestions;       // 미답변 질문 수

    // 학습 시간 통계
    private BigDecimal totalWorkHours;      // 총 학습 시간 (시간)
    private BigDecimal recentWeekWorkHours; // 최근 7일 학습 시간 (시간)

    // 과제 성공률
    private BigDecimal assignmentSuccessRate; // 과제 성공률 (%) - 수행한 과제 중 성공한 비율

    // 통계 데이터로 StudentDashboardResponse 생성
    public static StudentDashboardResponse of(
            Long totalClasses,
            Long activeWeeklySessions,
            Long totalAssignments,
            Long submittedAssignments,
            Long passedAssignments,
            Long failedAssignments,
            Long pendingAssignments,
            BigDecimal averageProgress,
            Long unreadNotifications,
            Long totalQuestions,
            Long answeredQuestions,
            Long unansweredQuestions,
            BigDecimal totalWorkHours,
            BigDecimal recentWeekWorkHours,
            BigDecimal assignmentSuccessRate) {

        return StudentDashboardResponse.builder()
                .totalClasses(totalClasses != null ? totalClasses : 0L)
                .activeWeeklySessions(activeWeeklySessions != null ? activeWeeklySessions : 0L)
                .totalAssignments(totalAssignments != null ? totalAssignments : 0L)
                .submittedAssignments(submittedAssignments != null ? submittedAssignments : 0L)
                .passedAssignments(passedAssignments != null ? passedAssignments : 0L)
                .failedAssignments(failedAssignments != null ? failedAssignments : 0L)
                .pendingAssignments(pendingAssignments != null ? pendingAssignments : 0L)
                .averageProgress(averageProgress != null ? averageProgress : BigDecimal.ZERO)
                .unreadNotifications(unreadNotifications != null ? unreadNotifications : 0L)
                .totalQuestions(totalQuestions != null ? totalQuestions : 0L)
                .answeredQuestions(answeredQuestions != null ? answeredQuestions : 0L)
                .unansweredQuestions(unansweredQuestions != null ? unansweredQuestions : 0L)
                .totalWorkHours(totalWorkHours != null ? totalWorkHours : BigDecimal.ZERO)
                .recentWeekWorkHours(recentWeekWorkHours != null ? recentWeekWorkHours : BigDecimal.ZERO)
                .assignmentSuccessRate(assignmentSuccessRate != null ? assignmentSuccessRate : BigDecimal.ZERO)
                .build();
    }
}
