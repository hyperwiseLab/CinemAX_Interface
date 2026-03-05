package com.cinemax.domain.dashboard.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.dashboard.dto.admin.*;
import com.cinemax.domain.dashboard.dto.student.*;
import com.cinemax.domain.dashboard.service.DashboardService;
import com.cinemax.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * 통합 대시보드 API
 */
@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "통합 대시보드 API")
public class DashboardController extends BaseController {

    private final DashboardService dashboardService;

    // 주차별 세션 통합 대시보드 개요 조회
    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}/overview")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "통합 대시보드 개요 조회", description = "주차별 세션의 학생 활동, 진도, 질문, 제출 통계를 통합하여 조회합니다. 학생 통계(총 학생 수, 활동 중, 완료, 도움 필요), 진도 통계(평균, 최소, 최대), 질문 통계(총 질문, 미답변, 긴급), 제출 통계(총 제출, 합격, 불합격, 합격률)를 제공합니다.")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getDashboardOverview(@Parameter(description = "주차 세션 ID") @PathVariable Long weeklySessionId,
                                                                                       @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        DashboardOverviewResponse response = dashboardService.getDashboardOverview(weeklySessionId, inviteId);

        return success(response, "대시보드 개요 조회 성공");
    }

    // 주차별 세션 과제 제출 통계 조회
    @GetMapping("/weekly-session/{weeklySessionId}/submit-statistics")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "과제 제출 통계 조회", description = "주차별 세션의 과제 제출 현황 통계를 조회합니다. 전체 제출 수, 합격/불합격 제출 수, 제출 학생 수, 합격 학생 수, 합격률을 제공합니다.")
    public ResponseEntity<ApiResponse<SubmitStatisticsResponse>> getSubmitStatistics(@Parameter(description = "주차 세션 ID") @PathVariable Long weeklySessionId) {

        SubmitStatisticsResponse response = dashboardService.getSubmitStatistics(weeklySessionId);

        return success(response, "과제 제출 통계 조회 성공");
    }

    // 학생 대시보드 조회
    @GetMapping("/student/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "내 학생 대시보드 조회", description = "현재 로그인한 학생의 대시보드 정보를 조회합니다. 수강 중인 수업, 과제 제출 현황, 학습 진도, 알림, 질문/답변, 학습 시간 통계를 제공합니다.")
    public ResponseEntity<ApiResponse<StudentDashboardResponse>> getMyStudentDashboard(@AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {

        StudentDashboardResponse response = dashboardService.getStudentDashboard(userDetails.getUserId());

        return success(response, "학생 대시보드 조회 성공");
    }

    // 특정 학생의 대시보드 조회 (교수/관리자용)
    @GetMapping("/student/{userId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "학생 대시보드 조회",
            description = "특정 학생의 대시보드 정보를 조회합니다. (교수/관리자 전용) " +
                    "수강 중인 수업, 과제 제출 현황, 학습 진도, 알림, 질문/답변, 학습 시간 통계를 제공합니다."
    )
    public ResponseEntity<ApiResponse<StudentDashboardResponse>> getStudentDashboard(@Parameter(description = "학생 ID") @PathVariable Long userId) {

        StudentDashboardResponse response = dashboardService.getStudentDashboard(userId);

        return success(response, "학생 대시보드 조회 성공");
    }

    // 학생별 상세 분석 리포트 조회
    @GetMapping("/student/{userId}/weekly-session/{weeklySessionId}/analysis")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "학생별 상세 분석 리포트", description = "특정 학생의 주차별 상세 분석 리포트를 조회합니다. 과제 제출, 학습 시간, 진도, 활동, 질문/답변 등을 종합 분석합니다.")
    public ResponseEntity<ApiResponse<StudentDetailAnalysisResponse>> getStudentDetailAnalysis(@Parameter(description = "학생 ID") @PathVariable Long userId,
                                                                                               @Parameter(description = "주차 세션 ID") @PathVariable Long weeklySessionId) {

        StudentDetailAnalysisResponse response = dashboardService.getStudentDetailAnalysis(userId, weeklySessionId);

        return success(response, "학생별 상세 분석 조회 성공");
    }

    // 주차별 종합 리포트 조회
    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}/report")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "주차별 종합 리포트", description = "주차별 전체 학생의 종합 리포트를 조회합니다. 학생 통계, 제출 통계, 학습 시간, 질문, 진도 등 전체 현황을 제공합니다.")
    public ResponseEntity<ApiResponse<WeeklyReportResponse>> getWeeklyReport(@Parameter(description = "주차 세션 ID") @PathVariable Long weeklySessionId,
                                                                             @Parameter(description = "초대 ID") @PathVariable Long inviteId,
                                                                             @Parameter(description = "시작 날짜") @RequestParam(required = false)
                                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                             @Parameter(description = "종료 날짜") @RequestParam(required = false)
                                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        WeeklyReportResponse response = (startDate != null && endDate != null)
                ? dashboardService.getWeeklyReport(weeklySessionId, inviteId, startDate, endDate)
                : dashboardService.getWeeklyReport(weeklySessionId, inviteId);

        return success(response, "주차별 종합 리포트 조회 성공");
    }

    // 실시간 도움 필요 학생 목록 조회
    @GetMapping("/weekly-session/{weeklySessionId}/students-needing-help")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "도움 필요 학생 목록", description = "실시간으로 도움이 필요한 학생 목록을 조회합니다. 테스트 실패 또는 5분 이상 진도 없음 학생들의 상세 분석을 제공합니다."
    )
    public ResponseEntity<ApiResponse<List<StudentDetailAnalysisResponse>>> getStudentsNeedingHelp(@Parameter(description = "주차 세션 ID") @PathVariable Long weeklySessionId) {

        List<StudentDetailAnalysisResponse> responses = dashboardService.getStudentsNeedingHelp(weeklySessionId);

        return success(responses, "도움 필요 학생 목록 조회 성공");
    }

    // Polling 전용 - 경량 대시보드 개요 조회 (10초 간격 호출용)
    @GetMapping("/weekly-session/{weeklySessionId}/poll")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "대시보드 Polling", description = "10초 간격으로 호출하기 위한 경량화된 대시보드 개요입니다. 학생 수, 활동 상태, 진도율, 제출 통계 등 핵심 정보만 반환합니다."
    )
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> pollDashboard(@Parameter(description = "주차 세션 ID") @PathVariable Long weeklySessionId,
                                                                                @Parameter(description = "초대 코드 ID") @RequestParam Long inviteId) {

        DashboardOverviewResponse response = dashboardService.getDashboardOverview(weeklySessionId, inviteId);

        return success(response, "대시보드 Polling 조회 성공");
    }

    // 차시별 성장률 분석
    @GetMapping("/class/{classId}/weekly-growth")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "차시별 성장률 분석", description = "차시별 과제 성공률, 평균 제출 시간, 재도전 횟수를 분석합니다.")
    public ResponseEntity<ApiResponse<List<WeeklyGrowthAnalysisResponse>>> getWeeklyGrowthAnalysis(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        List<WeeklyGrowthAnalysisResponse> responses = dashboardService.getWeeklyGrowthAnalysis(classId);

        return success(responses, "차시별 성장률 분석 완료");
    }

    // 어려운 과제 분석
    @GetMapping("/class/{classId}/difficult-tasks")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "어려운 과제 분석", description = "가장 어려웠던 과제 순위, 평균 시도 횟수, 성공률을 분석합니다.")
    public ResponseEntity<ApiResponse<List<DifficultTaskAnalysisResponse>>> getDifficultTaskAnalysis(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        List<DifficultTaskAnalysisResponse> responses = dashboardService.getDifficultTaskAnalysis(classId);

        return success(responses, "어려운 과제 분석 완료");
    }

    // 학습 패턴 분석
    @GetMapping("/student/{userId}/learning-pattern")
    @Operation(summary = "학습 패턴 분석", description = "학생의 학습 시간대, 일관성, 문제 해결 속도 추세를 분석합니다.")
    public ResponseEntity<ApiResponse<LearningPatternAnalysisResponse>> getLearningPatternAnalysis(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        LearningPatternAnalysisResponse response = dashboardService.getLearningPatternAnalysis(userId);

        return success(response, "학습 패턴 분석 완료");
    }

    // 강점/약점 분석
    @GetMapping("/student/{userId}/strength-weakness")
    @Operation(summary = "강점/약점 분석", description = "학생의 강점 영역, 약점 영역, 개선 추세를 분석합니다.")
    public ResponseEntity<ApiResponse<StrengthWeaknessAnalysisResponse>> getStrengthWeaknessAnalysis(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        StrengthWeaknessAnalysisResponse response = dashboardService.getStrengthWeaknessAnalysis(userId);

        return success(response, "강점/약점 분석 완료");
    }

    // 관리자 시스템 전체 대시보드 조회
    @GetMapping("/admin/system-overview")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자 시스템 전체 대시보드",
            description = "시스템 전체의 사용자, 수업, 커리큘럼, 활동 통계를 조회합니다. 전체 사용자 수, 역할별 사용자 수, 신규 가입자, 전체 수업 수, 활성 수업, 커리큘럼 통계, 코드 제출, 질문, 알림 등 시스템 전반의 주요 지표를 제공합니다.")
    public ResponseEntity<ApiResponse<AdminSystemOverviewResponse>> getAdminSystemOverview() {

        AdminSystemOverviewResponse response = dashboardService.getAdminSystemOverview();

        return success(response, "관리자 시스템 대시보드 조회 성공");
    }

    // 학생 성과 대시보드 조회
    @GetMapping("/student/{userId}/class/{classId}/performance")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "학생 성과 대시보드 조회", description = "학생의 성과 대시보드를 조회합니다. 시각화된 성과 그래프, 주차별 비교, 개선 추세, 목표 대비 달성률을 제공합니다.")
    public ResponseEntity<ApiResponse<StudentPerformanceDashboardResponse>> getStudentPerformanceDashboard(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                                           @Parameter(description = "수업 ID") @PathVariable Long classId) {

        StudentPerformanceDashboardResponse response = dashboardService.getStudentPerformanceDashboard(userId, classId);

        return success(response, "학생 성과 대시보드 조회 성공");
    }

    // 내 성과 대시보드 조회 (학생 본인)
    @GetMapping("/student/my/class/{classId}/performance")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "내 성과 대시보드 조회", description = "현재 로그인한 학생의 성과 대시보드를 조회합니다. 시각화된 성과 그래프, 주차별 비교, 개선 추세, 목표 대비 달성률을 제공합니다.")
    public ResponseEntity<ApiResponse<StudentPerformanceDashboardResponse>> getMyPerformanceDashboard(@Parameter(description = "수업 ID") @PathVariable Long classId,
                                                                                                      @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {

        StudentPerformanceDashboardResponse response = dashboardService.getStudentPerformanceDashboard(userDetails.getUserId(), classId);

        return success(response, "내 성과 대시보드 조회 성공");
    }

    // 스킬 레벨 분석 조회
    @GetMapping("/student/{userId}/skill-level")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "스킬 레벨 분석 조회", description = "학생의 스킬 레벨을 분석합니다. 언어별 숙련도, 개념별 이해도, 문제 해결 능력을 레이더 차트 형태로 제공합니다.")
    public ResponseEntity<ApiResponse<SkillLevelAnalysisResponse>> getSkillLevelAnalysis(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        SkillLevelAnalysisResponse response = dashboardService.getSkillLevelAnalysis(userId);

        return success(response, "스킬 레벨 분석 완료");
    }

    // 내 스킬 레벨 분석 조회 (학생 본인)
    @GetMapping("/student/my/skill-level")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "내 스킬 레벨 분석 조회", description = "현재 로그인한 학생의 스킬 레벨을 분석합니다. 언어별 숙련도, 개념별 이해도, 문제 해결 능력을 레이더 차트 형태로 제공합니다.")
    public ResponseEntity<ApiResponse<SkillLevelAnalysisResponse>> getMySkillLevelAnalysis(@AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {

        SkillLevelAnalysisResponse response = dashboardService.getSkillLevelAnalysis(userDetails.getUserId());

        return success(response, "내 스킬 레벨 분석 완료");
    }

    // "진행 중" 수업들의 평균 출석률 및 평균 성공률 조회
    @GetMapping("/in-progress-class-statistics")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "진행 중 수업 통계 조회",
        description = "진행 중인 수업들의 평균 출석률과 평균 성공률을 조회합니다. " +
                      "평균 출석률: 진행 중 수업의 모든 학생에 대한 개인별 출석률(완료한 차시 수 / 전체 차시 수) 계산 후 평균. " +
                      "평균 성공률: 진행 중 수업의 모든 학생에 대한 개인별 과제 성공률(성공한 과제 수 / 전체 과제 수) 계산 후 평균. " +
                      "예: A수업(수강생 10명)이 3차시까지 진행, B수업(수강생 20명)이 2차시까지 진행한 경우, " +
                      "총 30명의 학생에 대한 개인별 출석률과 성공률을 계산하여 평균을 반환합니다."
    )
    public ResponseEntity<ApiResponse<InProgressClassStatisticsResponse>> getInProgressClassStatistics() {

        InProgressClassStatisticsResponse response = dashboardService.getInProgressClassStatistics();

        return success(response, "진행 중 수업 통계 조회 성공");
    }
}
