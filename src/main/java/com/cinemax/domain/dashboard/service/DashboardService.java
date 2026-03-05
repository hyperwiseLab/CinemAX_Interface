package com.cinemax.domain.dashboard.service;

import com.cinemax.domain.dashboard.dto.admin.*;
import com.cinemax.domain.dashboard.dto.student.*;
import com.cinemax.domain.dashboard.dto.common.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 통합 대시보드 서비스 인터페이스
 */
public interface DashboardService {

    // 주차별 세션의 통합 대시보드 개요 조회
    DashboardOverviewResponse getDashboardOverview(Long weeklySessionId, Long inviteId);

    // 주차별 세션의 과제 제출 통계 조회
    SubmitStatisticsResponse getSubmitStatistics(Long weeklySessionId);

    // 학생 대시보드 조회
    StudentDashboardResponse getStudentDashboard(Long userId);

    // 학생별 상세 분석 리포트 조회
    StudentDetailAnalysisResponse getStudentDetailAnalysis(Long userId, Long weeklySessionId);

    // 주차별 종합 리포트 조회
    WeeklyReportResponse getWeeklyReport(Long weeklySessionId, Long inviteId);

    // 기간별 주차 종합 리포트 조회
    WeeklyReportResponse getWeeklyReport(Long weeklySessionId, Long inviteId, LocalDate startDate, LocalDate endDate);

    // 실시간 도움 필요 학생 목록 조회
    List<StudentDetailAnalysisResponse> getStudentsNeedingHelp(Long weeklySessionId);

    // 차시별 성장률 분석
    List<WeeklyGrowthAnalysisResponse> getWeeklyGrowthAnalysis(Long classId);

    // 어려운 과제 분석
    List<DifficultTaskAnalysisResponse> getDifficultTaskAnalysis(Long classId);

    // 학습 패턴 분석
    LearningPatternAnalysisResponse getLearningPatternAnalysis(Long userId);

    // 강점/약점 분석
    StrengthWeaknessAnalysisResponse getStrengthWeaknessAnalysis(Long userId);

    // 관리자 시스템 전체 대시보드 조회
    AdminSystemOverviewResponse getAdminSystemOverview();

    // 학생 성과 대시보드 조회 (시각화된 성과 그래프, 주차별 비교, 개선 추세)
    StudentPerformanceDashboardResponse getStudentPerformanceDashboard(Long userId, Long classId);

    // 스킬 레벨 분석 (언어별 숙련도, 개념별 이해도, 문제 해결 능력, 레이더 차트)
    SkillLevelAnalysisResponse getSkillLevelAnalysis(Long userId);

    // "진행 중" 수업들의 평균 출석률 및 평균 성공률 조회
    InProgressClassStatisticsResponse getInProgressClassStatistics();
}
