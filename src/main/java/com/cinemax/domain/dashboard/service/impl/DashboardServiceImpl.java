package com.cinemax.domain.dashboard.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.activity.repository.ActivityMonitorRepository;
import com.cinemax.domain.classes.entity.ClassSubmit;
import com.cinemax.domain.classes.repository.ClassSubmitRepository;
import com.cinemax.domain.dashboard.dto.admin.*;
import com.cinemax.domain.dashboard.dto.student.*;
import com.cinemax.domain.dashboard.dto.common.*;
import com.cinemax.domain.dashboard.service.DashboardService;
import com.cinemax.domain.notification.repository.NotificationRepository;
import com.cinemax.domain.progress.entity.Progress;
import com.cinemax.domain.progress.repository.ProgressRepository;
import com.cinemax.domain.qna.repository.QuestionRepository;
import com.cinemax.domain.syntax.entity.Syntax;
import com.cinemax.domain.syntax.repository.SyntaxRepository;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.domain.weeklySession.repository.WeeklySessionRepository;
import com.cinemax.domain.worklog.entity.WorkLog;
import com.cinemax.domain.worklog.repository.WorkLogRepository;
import com.cinemax.global.enums.QuestionStatus;
import com.cinemax.global.enums.RoleType;
import com.cinemax.global.enums.StudentActivityStatus;
import com.cinemax.global.enums.WeeklySessionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 통합 대시보드 서비스 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final WeeklySessionRepository weeklySessionRepository;
    private final ProgressRepository progressRepository;
    private final ActivityMonitorRepository activityMonitorRepository;
    private final QuestionRepository questionRepository;
    private final ClassSubmitRepository classSubmitRepository;
    private final NotificationRepository notificationRepository;
    private final WorkLogRepository workLogRepository;
    private final UserRepository userRepository;
    private final SyntaxRepository syntaxRepository;

    // 주차별 세션의 통합 대시보드 개요 조회
    @Override
    public DashboardOverviewResponse getDashboardOverview(Long weeklySessionId, Long inviteId) {

        // 주차 세션 존재 여부 확인
        WeeklySession weeklySession = weeklySessionRepository.findById(weeklySessionId)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklySession", "weeklySessionId", weeklySessionId));

        // 학생 통계 조회
        List<Progress> allProgress = progressRepository.findAllByWeeklySessionId(weeklySessionId);
        Long totalStudents = (long) allProgress.size();
        Long activeStudents = progressRepository.findActiveStudents(weeklySessionId).stream().count();
        Long completedStudents = progressRepository.findCompletedStudents(weeklySessionId).stream().count();
        Long needHelpCount = progressRepository.findStudentsNeedingHelp(weeklySessionId).stream().count();

        // 진도 통계 계산
        BigDecimal averageProgress = BigDecimal.ZERO;
        BigDecimal minProgress = BigDecimal.ZERO;
        BigDecimal maxProgress = BigDecimal.ZERO;

        if (!allProgress.isEmpty()) {
            BigDecimal totalProgress = allProgress.stream()
                    .map(Progress::getProgressPct)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            averageProgress = totalProgress.divide(
                    BigDecimal.valueOf(allProgress.size()),
                    2,
                    RoundingMode.HALF_UP
            );

            minProgress = allProgress.stream()
                    .map(Progress::getProgressPct)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            maxProgress = allProgress.stream()
                    .map(Progress::getProgressPct)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }

        // 질문 통계 조회
        Long totalQuestions = questionRepository.countByWeeklySession(weeklySessionId);
        Long unansweredQuestions = questionRepository.countUnansweredQuestions(weeklySessionId);
        Long highUrgencyQuestions = (long) questionRepository.findHighUrgencyQuestions(weeklySessionId).size();

        // 4. 제출 통계 조회
        Long totalSubmissions = classSubmitRepository.countByWeeklySession(weeklySessionId);
        Long passedSubmissions = classSubmitRepository.countPassedByWeeklySession(weeklySessionId);
        Long failedSubmissions = classSubmitRepository.countFailedByWeeklySession(weeklySessionId);

        // 5. 평균 성공률 계산 (각 학생별 성공률의 평균)
        BigDecimal passRate = BigDecimal.ZERO;

        if (!allProgress.isEmpty()) {
            // 각 학생별 성공률을 계산
            List<BigDecimal> studentSuccessRates = new ArrayList<>();

            for (Progress progress : allProgress) {
                // 해당 학생의 이번 주차 제출 내역 조회
                List<ClassSubmit> studentSubmits = classSubmitRepository.findByWeeklySessionId(weeklySessionId)
                    .stream()
                    .filter(cs -> cs.getClassId().equals(progress.getClassId()))
                    .filter(cs -> cs.getSubmitYn() != null && cs.getSubmitYn())
                    .collect(Collectors.toList());

                if (!studentSubmits.isEmpty()) {
                    long studentTotalSubmits = studentSubmits.size();
                    long studentPassedSubmits = studentSubmits.stream()
                        .filter(cs -> Boolean.TRUE.equals(cs.getResult()))
                        .count();

                    // 해당 학생의 성공률 계산
                    BigDecimal studentSuccessRate = BigDecimal.valueOf(studentPassedSubmits)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(studentTotalSubmits), 2, RoundingMode.HALF_UP);

                    studentSuccessRates.add(studentSuccessRate);
                }
            }

            // 모든 학생의 성공률 평균 계산
            if (!studentSuccessRates.isEmpty()) {
                BigDecimal totalSuccessRate = studentSuccessRates.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                passRate = totalSuccessRate.divide(
                    BigDecimal.valueOf(studentSuccessRates.size()),
                    2,
                    RoundingMode.HALF_UP
                );
            }
        }

        DashboardOverviewResponse dashboardOverviewResponse = DashboardOverviewResponse.of(
                totalStudents,
                activeStudents,
                completedStudents,
                needHelpCount,
                averageProgress,
                minProgress,
                maxProgress,
                totalQuestions,
                unansweredQuestions,
                highUrgencyQuestions,
                totalSubmissions,
                passedSubmissions,
                failedSubmissions,
                passRate
        );

        return dashboardOverviewResponse;
    }

    /**
     * 주차별 세션의 과제 제출 통계 조회
     */
    @Override
    public SubmitStatisticsResponse getSubmitStatistics(Long weeklySessionId) {

        // 통계 데이터 수집
        Long totalSubmissions = classSubmitRepository.countByWeeklySession(weeklySessionId);
        Long passedSubmissions = classSubmitRepository.countPassedByWeeklySession(weeklySessionId);
        Long failedSubmissions = classSubmitRepository.countFailedByWeeklySession(weeklySessionId);
        Long submittedStudents = classSubmitRepository.countDistinctStudentsByWeeklySession(weeklySessionId);
        Long passedStudents = classSubmitRepository.countDistinctPassedStudentsByWeeklySession(weeklySessionId);

        SubmitStatisticsResponse submitStatisticsResponse = SubmitStatisticsResponse.of(
                totalSubmissions,
                passedSubmissions,
                failedSubmissions,
                submittedStudents,
                passedStudents
        );

        return submitStatisticsResponse;
    }

    // 학생 대시보드 조회
    @Override
    public StudentDashboardResponse getStudentDashboard(Long userId) {

        // 1. 수업 정보 조회
        List<Progress> allProgress = progressRepository.findAllByUserId(userId);
        Long totalClasses = allProgress.stream()
                .map(p -> p.getClassId())
                .distinct()
                .count();
        Long activeWeeklySessions = allProgress.stream()
                .filter(p -> p.getActivityStatus() == StudentActivityStatus.ACTIVE)
                .count();

        // 2. 과제 제출 현황 조회
        Long submittedAssignments = classSubmitRepository.countSubmittedAssignments();
        Long passedAssignments = classSubmitRepository.countPassedAssignments();
        Long failedAssignments = classSubmitRepository.countFailedAssignments();

        // 전체 과제 수 계산 (제출한 과제 + 실패한 과제를 기준으로 계산)
        Long totalAssignments = submittedAssignments;
        Long pendingAssignments = 0L; // 실제로는 Task에서 조회해야 하나, 단순화

        // 3. 학습 진도 조회
        BigDecimal averageProgress = progressRepository.getAverageProgressByUserId(userId);

        // 4. 알림 정보 조회
        Long unreadNotifications = notificationRepository.countUnreadByUserId(userId);

        // 5. 질문/답변 정보 조회
        Long totalQuestions = questionRepository.countByUserId(userId);
        Long answeredQuestions = questionRepository.countAnsweredByUserId(userId);
        Long unansweredQuestions = questionRepository.countUnansweredByUserId(userId);

        // 6. 학습 시간 통계 조회
        BigDecimal totalWorkHours = workLogRepository.sumTotalWorkHoursByUserId(userId);
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        BigDecimal recentWeekWorkHours = workLogRepository.sumRecentWorkHoursByUserId(userId, weekAgo);

        // 7. 과제 성공률 계산 (수행한 과제 중 성공한 비율)
        BigDecimal assignmentSuccessRate = BigDecimal.ZERO;
        if (submittedAssignments != null && submittedAssignments > 0) {
            assignmentSuccessRate = BigDecimal.valueOf(passedAssignments)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(submittedAssignments), 2, RoundingMode.HALF_UP);
        }

        StudentDashboardResponse response = StudentDashboardResponse.of(
                totalClasses,
                activeWeeklySessions,
                totalAssignments,
                submittedAssignments,
                passedAssignments,
                failedAssignments,
                pendingAssignments,
                averageProgress,
                unreadNotifications,
                totalQuestions,
                answeredQuestions,
                unansweredQuestions,
                totalWorkHours,
                recentWeekWorkHours,
                assignmentSuccessRate
        );

        return response;
    }

    // 학생별 상세 분석 리포트 조회
    @Override
    public StudentDetailAnalysisResponse getStudentDetailAnalysis(Long userId, Long weeklySessionId) {

        // 파라미터 추출
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId).orElse(null);
        Long submittedCount = classSubmitRepository.countSubmittedAssignments();
        Long passedCount = classSubmitRepository.countPassedAssignments();
        Long failedCount = classSubmitRepository.countFailedAssignments();
        Double passRate = submittedCount > 0 ? (passedCount.doubleValue() / submittedCount.doubleValue()) * 100 : 0.0;

        AssignmentAnalysis assignmentAnalysis = AssignmentAnalysis.builder()
                .totalAssignments(submittedCount)
                .submittedAssignments(submittedCount)
                .passedAssignments(passedCount)
                .failedAssignments(failedCount)
                .passRate(Math.round(passRate * 100.0) / 100.0)
                .submissionRate(100.0)
                .averageAttempts(1)
                .build();

        // 학습 시간 분석
        BigDecimal totalHours = workLogRepository.sumWorkHoursByUserIdAndWeeklySessionId(userId, weeklySessionId);
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        BigDecimal recentHours = workLogRepository.sumRecentWorkHoursByUserId(userId, weekAgo);

        List<WorkLog> workLogs = workLogRepository.findByUserIdAndWeeklySessionId(userId, weeklySessionId);
        List<DailyWorkHour> dailyHours = workLogs.stream()
                .map(wl -> DailyWorkHour.builder()
                        .date(wl.getLogDate().toString())
                        .hours(wl.getWorkHours())
                        .build())
                .collect(Collectors.toList());

        StudyTimeAnalysis studyTimeAnalysis =
                StudyTimeAnalysis.builder()
                        .totalWorkHours(totalHours)
                        .averageDailyHours(workLogs.isEmpty() ? BigDecimal.ZERO :
                                totalHours.divide(BigDecimal.valueOf(workLogs.size()), 2, RoundingMode.HALF_UP))
                        .recentWeekHours(recentHours)
                        .dailyWorkHours(dailyHours)
                        .build();

        // 진도 분석
        ProgressAnalysis progressAnalysis =
                ProgressAnalysis.builder()
                        .currentProgress(progress != null ? progress.getProgressPct() : BigDecimal.ZERO)
                        .averageProgress(progressRepository.getAverageProgressByUserId(userId))
                        .activityStatus(progress != null ? progress.getActivityStatus().name() : "IDLE")
                        .lastActivityTime(progress != null ? progress.getLastActivityTime() : null)
                        .testFailCount(progress != null ? progress.getTestFailCount() : 0)
                        .build();

        // 활동 분석
        Long activityCount = activityMonitorRepository.countByWeeklySessionIdAndInviteId(weeklySessionId, userId);

        ActivityAnalysis activityAnalysis =
                ActivityAnalysis.builder()
                        .totalActivities(activityCount)
                        .codeSubmissions(submittedCount)
                        .codePeeks(0L)
                        .snapshotSaves(activityCount)
                        .firstActivity(null)
                        .lastActivity(progress != null ? progress.getLastActivityTime() : null)
                        .build();

        // 질문 분석
        Long totalQuestions = questionRepository.countByUserId(userId);
        Long answeredQuestions = questionRepository.countAnsweredByUserId(userId);
        Long unansweredQuestions = questionRepository.countUnansweredByUserId(userId);
        Double responseRate = totalQuestions > 0 ? (answeredQuestions.doubleValue() / totalQuestions.doubleValue()) * 100 : 0.0;

        QnAAnalysis qnaAnalysis =
                QnAAnalysis.builder()
                        .totalQuestions(totalQuestions)
                        .answeredQuestions(answeredQuestions)
                        .unansweredQuestions(unansweredQuestions)
                        .highUrgencyQuestions(0L)
                        .responseRate(Math.round(responseRate * 100.0) / 100.0)
                        .build();

        StudentDetailAnalysisResponse response = StudentDetailAnalysisResponse.of(
                userId, user.getName(), user.getEmail(),
                weeklySessionId, null, null,
                assignmentAnalysis, studyTimeAnalysis, progressAnalysis,
                activityAnalysis, qnaAnalysis
        );

        return response;
    }

    // 주차별 종합 리포트 조회
    @Override
    public WeeklyReportResponse getWeeklyReport(Long weeklySessionId, Long inviteId) {

        // 주차 정보 조회
        WeeklySession session = weeklySessionRepository.findById(weeklySessionId)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklySession", "weeklySessionId", weeklySessionId));

        // 학생 통계
        List<Progress> allProgress = progressRepository.findAllByWeeklySessionId(weeklySessionId);
        Long totalStudents = (long) allProgress.size();
        Long activeStudents = allProgress.stream().filter(p -> p.getActivityStatus() == StudentActivityStatus.ACTIVE).count();
        Long completedStudents = allProgress.stream().filter(p -> p.getActivityStatus() == StudentActivityStatus.COMPLETED).count();
        Long needHelpStudents = allProgress.stream().filter(p -> p.getActivityStatus() == StudentActivityStatus.NEED_HELP).count();
        Long idleStudents = totalStudents - activeStudents - completedStudents - needHelpStudents;

        StudentStatistics studentStats = StudentStatistics.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .completedStudents(completedStudents)
                .idleStudents(idleStudents)
                .needHelpStudents(needHelpStudents)
                .build();

        // 제출 통계
        Long totalSubs = classSubmitRepository.countByWeeklySession(weeklySessionId);
        Long passedSubs = classSubmitRepository.countPassedByWeeklySession(weeklySessionId);
        Long failedSubs = classSubmitRepository.countFailedByWeeklySession(weeklySessionId);
        Long submittedStudents = classSubmitRepository.countDistinctStudentsByWeeklySession(weeklySessionId);
        Long passedStudents = classSubmitRepository.countDistinctPassedStudentsByWeeklySession(weeklySessionId);
        Double passRate = totalSubs > 0 ? (passedSubs.doubleValue() / totalSubs.doubleValue()) * 100 : 0.0;
        Double subRate = totalStudents > 0 ? (submittedStudents.doubleValue() / totalStudents.doubleValue()) * 100 : 0.0;

        SubmissionStatistics submissionStats = SubmissionStatistics.builder()
                .totalSubmissions(totalSubs)
                .passedSubmissions(passedSubs)
                .failedSubmissions(failedSubs)
                .submittedStudents(submittedStudents)
                .passedStudents(passedStudents)
                .passRate(Math.round(passRate * 100.0) / 100.0)
                .submissionRate(Math.round(subRate * 100.0) / 100.0)
                .build();

        // 학습 시간 통계
        BigDecimal totalHours = workLogRepository.sumWorkHoursByWeeklySessionId(weeklySessionId);
        BigDecimal avgHours = workLogRepository.avgWorkHoursByWeeklySessionId(weeklySessionId);
        BigDecimal maxHours = workLogRepository.maxWorkHoursByWeeklySessionId(weeklySessionId);
        BigDecimal minHours = workLogRepository.minWorkHoursByWeeklySessionId(weeklySessionId);

        WorkHourStatistics workHourStats = WorkHourStatistics.builder()
                .totalWorkHours(totalHours)
                .averageWorkHours(avgHours)
                .maxWorkHours(maxHours)
                .minWorkHours(minHours)
                .build();

        // 질문 통계
        Long totalQuestions = questionRepository.countByWeeklySession(weeklySessionId);
        Long unansweredQuestions = questionRepository.countUnansweredQuestions(weeklySessionId);
        Long answeredQuestions = totalQuestions - unansweredQuestions;
        Double qResponseRate = totalQuestions > 0 ? (answeredQuestions.doubleValue() / totalQuestions.doubleValue()) * 100 : 0.0;

        QuestionStatistics questionStats = QuestionStatistics.builder()
                .totalQuestions(totalQuestions)
                .answeredQuestions(answeredQuestions)
                .unansweredQuestions(unansweredQuestions)
                .highUrgencyQuestions(0L)
                .responseRate(Math.round(qResponseRate * 100.0) / 100.0)
                .build();

        // 진도 통계
        if (!allProgress.isEmpty()) {
            BigDecimal avgProgress = allProgress.stream()
                    .map(Progress::getProgressPct)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(allProgress.size()), 2, RoundingMode.HALF_UP);
            BigDecimal maxProgress = allProgress.stream()
                    .map(Progress::getProgressPct)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            BigDecimal minProgress = allProgress.stream()
                    .map(Progress::getProgressPct)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            ProgressStatistics progressStats = ProgressStatistics.builder()
                    .averageProgress(avgProgress)
                    .maxProgress(maxProgress)
                    .minProgress(minProgress)
                    .build();

            WeeklyReportResponse response = WeeklyReportResponse.of(
                    weeklySessionId, inviteId, session.getWeekNo(), null,
                    studentStats, submissionStats, workHourStats, questionStats, progressStats,
                    new ArrayList<>(), new ArrayList<>()
            );

            return response;
        }

        return null;
    }

    // 기간별 주차별 종합 리포트 조회
    @Override
    public WeeklyReportResponse getWeeklyReport(Long weeklySessionId, Long inviteId, LocalDate startDate, LocalDate endDate) {

        WeeklySession session = weeklySessionRepository.findById(weeklySessionId)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklySession", "weeklySessionId", weeklySessionId));

        // 학생 통계는 기간과 무관 (등록 학생 수 기반)
        List<Progress> allProgress = progressRepository.findAllByWeeklySessionId(weeklySessionId);
        Long totalStudents = (long) allProgress.size();
        Long activeStudents = allProgress.stream().filter(p -> p.getActivityStatus() == StudentActivityStatus.ACTIVE).count();
        Long completedStudents = allProgress.stream().filter(p -> p.getActivityStatus() == StudentActivityStatus.COMPLETED).count();
        Long needHelpStudents = allProgress.stream().filter(p -> p.getActivityStatus() == StudentActivityStatus.NEED_HELP).count();
        Long idleStudents = totalStudents - activeStudents - completedStudents - needHelpStudents;

        StudentStatistics studentStats = StudentStatistics.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .completedStudents(completedStudents)
                .idleStudents(idleStudents)
                .needHelpStudents(needHelpStudents)
                .build();

        // 제출 통계 (기간 필터)
        List<ClassSubmit> submits = classSubmitRepository.findByWeeklySessionId(weeklySessionId);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<ClassSubmit> rangedSubmits = submits.stream()
                .filter(cs -> cs.getSubmitYn() != null && cs.getSubmitYn())
                .filter(cs -> !cs.getSubmitAt().isBefore(startDateTime) && !cs.getSubmitAt().isAfter(endDateTime))
                .toList();

        long totalSubs = rangedSubmits.size();
        long passedSubs = rangedSubmits.stream().filter(cs -> Boolean.TRUE.equals(cs.getResult())).count();
        long failedSubs = totalSubs - passedSubs;
        long submittedStudents = rangedSubmits.stream().map(cs -> cs.getSubmitId()).distinct().count();
        long passedStudents = rangedSubmits.stream()
                .filter(cs -> Boolean.TRUE.equals(cs.getResult()))
                .map(cs -> cs.getSubmitId())
                .distinct()
                .count();

        Double passRate = totalSubs > 0 ? (passedSubs * 100.0) / totalSubs : 0.0;
        Double subRate = totalStudents > 0 ? (submittedStudents * 100.0) / totalStudents : 0.0;

        SubmissionStatistics submissionStats = SubmissionStatistics.builder()
                .totalSubmissions(totalSubs)
                .passedSubmissions(passedSubs)
                .failedSubmissions(failedSubs)
                .submittedStudents(submittedStudents)
                .passedStudents(passedStudents)
                .passRate(Math.round(passRate * 100.0) / 100.0)
                .submissionRate(Math.round(subRate * 100.0) / 100.0)
                .build();

        // 학습 시간 통계 (기간 필터)
        List<WorkLog> workLogs = workLogRepository.findByWeeklySessionIdAndDateBetween(weeklySessionId, startDate, endDate);
        BigDecimal totalHours = workLogs.stream()
                .map(WorkLog::getWorkHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgHours = workLogs.isEmpty() ? BigDecimal.ZERO : totalHours.divide(BigDecimal.valueOf(workLogs.size()), 2, RoundingMode.HALF_UP);
        BigDecimal maxHours = workLogs.stream().map(WorkLog::getWorkHours).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal minHours = workLogs.stream().map(WorkLog::getWorkHours).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        WorkHourStatistics workHourStats = WorkHourStatistics.builder()
                .totalWorkHours(totalHours)
                .averageWorkHours(avgHours)
                .maxWorkHours(maxHours)
                .minWorkHours(minHours)
                .build();

        // 질문 통계 (기간 필터)
        var questions = questionRepository.findByWeeklySession(weeklySessionId);
        var rangedQuestions = questions.stream()
                .filter(q -> q.getCreateDt() != null)
                .filter(q -> !q.getCreateDt().isBefore(startDateTime) && !q.getCreateDt().isAfter(endDateTime))
                .toList();
        long totalQuestions = rangedQuestions.size();
        long unansweredQuestions = rangedQuestions.stream().filter(q -> q.getStatus() != null && q.getStatus().name().equals("OPEN")).count();
        long answeredQuestions = totalQuestions - unansweredQuestions;
        Double qResponseRate = totalQuestions > 0 ? (answeredQuestions * 100.0) / totalQuestions : 0.0;

        QuestionStatistics questionStats = QuestionStatistics.builder()
                .totalQuestions(totalQuestions)
                .answeredQuestions(answeredQuestions)
                .unansweredQuestions(unansweredQuestions)
                .highUrgencyQuestions(0L)
                .responseRate(Math.round(qResponseRate * 100.0) / 100.0)
                .build();

        // 진도 통계 (기간과 무관 - 현재 진도 기준)
        BigDecimal avgProgress = BigDecimal.ZERO;
        BigDecimal maxProgress = BigDecimal.ZERO;
        BigDecimal minProgress = BigDecimal.ZERO;
        if (!allProgress.isEmpty()) {
            avgProgress = allProgress.stream()
                    .map(Progress::getProgressPct)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(allProgress.size()), 2, RoundingMode.HALF_UP);
            maxProgress = allProgress.stream().map(Progress::getProgressPct).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            minProgress = allProgress.stream().map(Progress::getProgressPct).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        }
        ProgressStatistics progressStats = ProgressStatistics.builder()
                .averageProgress(avgProgress)
                .maxProgress(maxProgress)
                .minProgress(minProgress)
                .build();

        WeeklyReportResponse response = WeeklyReportResponse.of(
                weeklySessionId, inviteId, session.getWeekNo(), null,
                studentStats, submissionStats, workHourStats, questionStats, progressStats,
                new ArrayList<>(), new ArrayList<>()
        );

        return response;
    }

    // 실시간 도움 필요 학생 목록 조회
    @Override
    public List<StudentDetailAnalysisResponse> getStudentsNeedingHelp(Long weeklySessionId) {

        List<Progress> needHelpStudentsList = progressRepository.findStudentsNeedingHelp(weeklySessionId);

        List<StudentDetailAnalysisResponse> studentDetailAnalysisResponseList = needHelpStudentsList.stream()
                .map(progress -> getStudentDetailAnalysis(progress.getUser().getUserId(), weeklySessionId))
                .collect(Collectors.toList());

        return studentDetailAnalysisResponseList;
    }

    /**
     * 차시별 성장률 분석
     * WeeklySession별로 성공률, 평균 제출 시간, 재도전 횟수를 계산
     */
    @Override
    public List<WeeklyGrowthAnalysisResponse> getWeeklyGrowthAnalysis(Long classId) {

        // 수업의 모든 주차 세션 조회
        List<WeeklySession> weeklySessions = weeklySessionRepository.findAllByClassId(classId);

        List<WeeklyGrowthAnalysisResponse> responses = new ArrayList<>();

        for (WeeklySession session : weeklySessions) {
            Long weeklySessionId = session.getWeeklySessionId();

            // 해당 주차의 학생 진도 조회
            List<Progress> progressList = progressRepository.findAllByWeeklySessionId(weeklySessionId);
            Long totalStudents = (long) progressList.size();
            Long completedStudents = progressList.stream()
                .filter(p -> p.getCompletedDt() != null)
                .count();

            // 해당 주차의 과제 성공률 계산
            Long totalSubmissions = classSubmitRepository.countByWeeklySession(weeklySessionId);
            Long passedSubmissions = classSubmitRepository.countPassedByWeeklySession(weeklySessionId);

            BigDecimal successRate = BigDecimal.ZERO;
            if (totalSubmissions > 0) {
                successRate = BigDecimal.valueOf(passedSubmissions)
                    .divide(BigDecimal.valueOf(totalSubmissions), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            }

            // 평균 제출 시간 계산 (분 단위)
            Double avgTimeMinutes = classSubmitRepository.calculateAverageSubmissionTimeByWeeklySession(weeklySessionId);
            BigDecimal averageSubmissionTimeMinutes = avgTimeMinutes != null
                ? BigDecimal.valueOf(avgTimeMinutes).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            // 평균 재도전 횟수 계산
            Double avgRetry = classSubmitRepository.calculateAverageRetryCountByWeeklySession(weeklySessionId);
            BigDecimal averageRetryCount = avgRetry != null
                ? BigDecimal.valueOf(avgRetry).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            WeeklyGrowthAnalysisResponse weeklyGrowthAnalysisResponse = WeeklyGrowthAnalysisResponse.builder()
                .weekNo(session.getWeekNo())
                .successRate(successRate)
                .averageSubmissionTimeMinutes(averageSubmissionTimeMinutes)
                .averageRetryCount(averageRetryCount)
                .completedStudents(completedStudents)
                .totalStudents(totalStudents)
                .startDt(session.getStartDt())
                .endDt(session.getStatus() == WeeklySessionStatus.COMPLETED
                    ? session.getUpdateDt()
                    : null)
                .build();

            responses.add(weeklyGrowthAnalysisResponse);
        }

        return responses;
    }

    /**
     * 어려운 과제 분석
     * Task별로 성공률, 평균 시도 횟수, 평균 소요 시간을 계산하여 난이도순으로 정렬
     */
    @Override
    public List<DifficultTaskAnalysisResponse> getDifficultTaskAnalysis(Long classId) {

        // 과제별 통계 조회 (과제 ID, 성공률, 평균 시도 횟수, 평균 소요 시간)
        List<Object[]> taskDifficultyStatisticsList = classSubmitRepository.calculateTaskDifficultyStatistics(classId);

        List<DifficultTaskAnalysisResponse> difficultTaskAnalysisResponseList = new ArrayList<>();

        int rank = 1;
        for (Object[] stat : taskDifficultyStatisticsList) {
            Long taskId = ((Number) stat[0]).longValue();
            Double successRateValue = stat[1] != null ? ((Number) stat[1]).doubleValue() : 0.0;
            Double avgAttemptsValue = stat[2] != null ? ((Number) stat[2]).doubleValue() : 0.0;
            Double avgTimeValue = stat[3] != null ? ((Number) stat[3]).doubleValue() : 0.0;

            BigDecimal successRate = BigDecimal.valueOf(successRateValue).setScale(2, RoundingMode.HALF_UP);
            BigDecimal averageAttempts = BigDecimal.valueOf(avgAttemptsValue).setScale(2, RoundingMode.HALF_UP);
            BigDecimal averageTimeMinutes = BigDecimal.valueOf(avgTimeValue).setScale(2, RoundingMode.HALF_UP);

            // 해당 과제에 대한 도움 요청 횟수 조회 (질문 수로 대체)
            Long helpRequestCount = questionRepository.findByClassId(classId).stream()
                .filter(q -> q.getTitle() != null && q.getTitle().contains("Task " + taskId))
                .count();

            // Task 이름 조회 (실제로는 Task 정보에서 가져와야 함, 여기서는 간단히 처리)
            String taskName = "과제 " + taskId;

            DifficultTaskAnalysisResponse difficultTaskAnalysisResponse = DifficultTaskAnalysisResponse.builder()
                .taskId(taskId)
                .difficultyRank(rank++)
                .taskName(taskName)
                .successRate(successRate)
                .averageAttempts(averageAttempts)
                .averageTimeMinutes(averageTimeMinutes)
                .helpRequestCount(helpRequestCount)
                .build();

            difficultTaskAnalysisResponseList.add(difficultTaskAnalysisResponse);
        }

        // 성공률 낮은 순으로 정렬 (난이도 높은 순)
        difficultTaskAnalysisResponseList.sort((a, b) -> a.getSuccessRate().compareTo(b.getSuccessRate()));

        // 순위 재조정
        for (int i = 0; i < difficultTaskAnalysisResponseList.size(); i++) {
            DifficultTaskAnalysisResponse response = difficultTaskAnalysisResponseList.get(i);
            DifficultTaskAnalysisResponse updated = DifficultTaskAnalysisResponse.builder()
                .taskId(response.getTaskId())
                .difficultyRank(i + 1)
                .taskName(response.getTaskName())
                .successRate(response.getSuccessRate())
                .averageAttempts(response.getAverageAttempts())
                .averageTimeMinutes(response.getAverageTimeMinutes())
                .helpRequestCount(response.getHelpRequestCount())
                .build();
            difficultTaskAnalysisResponseList.set(i, updated);
        }

        return difficultTaskAnalysisResponseList;
    }

    /**
     * 학습 패턴 분석 - 학습 시간대, 일관성, 문제 해결 속도 추세를 분석
     */
    @Override
    public LearningPatternAnalysisResponse getLearningPatternAnalysis(Long userId) {
        log.info("학습 패턴 분석 시작 - userId: {}", userId);

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // 주간 활동 패턴 조회 (요일별 학습 시간)
        List<Object[]> weeklyPattern = workLogRepository.findWeeklyActivityPatternByUserId(userId);
        List<Integer> weeklyActivityPattern = new ArrayList<>();

        // 요일별 학습 시간을 정수형으로 변환 (일-토: 1-7)
        for (int i = 1; i <= 7; i++) {
            final int dayOfWeek = i;
            weeklyActivityPattern.add(
                weeklyPattern.stream()
                    .filter(arr -> ((Number) arr[0]).intValue() == dayOfWeek)
                    .findFirst()
                    .map(arr -> ((BigDecimal) arr[1]).intValue())
                    .orElse(0)
            );
        }

        // 주요 학습 시간대 분석 (가장 많이 학습한 요일 기반)
        int maxActivityDay = 0;
        int maxActivityHours = 0;
        for (int i = 0; i < weeklyActivityPattern.size(); i++) {
            if (weeklyActivityPattern.get(i) > maxActivityHours) {
                maxActivityHours = weeklyActivityPattern.get(i);
                maxActivityDay = i;
            }
        }

        String[] dayNames = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        String preferredStudyTime = maxActivityHours > 0
            ? dayNames[maxActivityDay] + " 중심"
            : "데이터 부족";

        // 평균 일일 학습 시간 (분 단위로 변환)
        BigDecimal avgDailyHours = workLogRepository.avgDailyWorkHoursByUserId(userId);
        BigDecimal averageDailyMinutes = avgDailyHours.multiply(BigDecimal.valueOf(60))
            .setScale(1, RoundingMode.HALF_UP);

        // 학습 일관성 점수 계산 (최근 30일 기준)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<Object[]> dailyHours = workLogRepository.findDailyWorkHoursByUserIdSince(userId, thirtyDaysAgo);

        BigDecimal consistencyScore = BigDecimal.ZERO;
        if (!dailyHours.isEmpty()) {
            // 학습한 날짜 수 / 30일 * 100 으로 일관성 계산
            long studyDays = dailyHours.size();
            consistencyScore = BigDecimal.valueOf(studyDays)
                .divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
        }

        // 문제 해결 속도 추세 분석
        List<Double> recentTimes = classSubmitRepository.findRecentSubmissionTimesByUserId(userId);
        String solvingSpeedTrend = "데이터 부족";

        if (recentTimes.size() >= 10) {
            // 최근 5개와 그 이전 5개의 평균 시간을 비교
            double recentAvg = recentTimes.subList(0, 5).stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
            double previousAvg = recentTimes.subList(5, 10).stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

            if (previousAvg > 0) {
                double improvement = ((previousAvg - recentAvg) / previousAvg) * 100;
                if (improvement > 10) {
                    solvingSpeedTrend = "빠르게 개선 중 (+" + String.format("%.1f", improvement) + "%)";
                } else if (improvement > 0) {
                    solvingSpeedTrend = "점진적 개선 중 (+" + String.format("%.1f", improvement) + "%)";
                } else if (improvement > -10) {
                    solvingSpeedTrend = "안정적 유지";
                } else {
                    solvingSpeedTrend = "속도 감소 (" + String.format("%.1f", improvement) + "%)";
                }
            }
        } else if (recentTimes.size() > 0) {
            solvingSpeedTrend = "분석 중 (데이터 수집 필요)";
        }

        return LearningPatternAnalysisResponse.builder()
                .userId(userId)
                .userName(user.getName())
                .preferredStudyTime(preferredStudyTime)
                .averageDailyMinutes(averageDailyMinutes)
                .consistencyScore(consistencyScore)
                .solvingSpeedTrend(solvingSpeedTrend)
                .weeklyActivityPattern(weeklyActivityPattern)
                .build();
    }

    /**
     * 강점/약점 분석 - Syntax별 성공률을 분석하여 강점/약점을 도출
     */
    @Override
    public StrengthWeaknessAnalysisResponse getStrengthWeaknessAnalysis(Long userId) {
        // 사용자 정보 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // Syntax별 성공률 조회
        List<Object[]> syntaxStats = classSubmitRepository.calculateSyntaxSuccessRateByUserId(userId);

        List<SkillArea> strengths = new ArrayList<>();
        List<SkillArea> weaknesses = new ArrayList<>();
        List<ImprovementTrend> improvementTrends = new ArrayList<>();

        // 성공률 기준으로 강점/약점 분류
        for (Object[] stat : syntaxStats) {
            Long syntaxId = ((Number) stat[0]).longValue();
            Double successRate = ((Number) stat[1]).doubleValue();
            Long attemptCount = ((Number) stat[2]).longValue();

            // Syntax 정보 조회
            Syntax syntax = syntaxRepository.findByIdWithDetails(syntaxId).orElse(null);
            if (syntax == null || syntax.getSyntaxDetails().isEmpty()) {
                continue;
            }

            // 첫 번째 SyntaxDetail의 제목을 문법 이름으로 사용
            String syntaxName = syntax.getSyntaxDetails().get(0).getSyntaxTitle();

            SkillArea skillArea = SkillArea.builder()
                .areaName(syntaxName)
                .successRate(BigDecimal.valueOf(successRate).setScale(2, RoundingMode.HALF_UP))
                .averageAttempts(BigDecimal.valueOf(attemptCount))
                .description(successRate >= 70.0 ? "강점 영역" : "약점 영역")
                .build();

            // 성공률 70% 이상은 강점, 50% 미만은 약점으로 분류
            if (successRate >= 70.0) {
                strengths.add(skillArea);
            } else if (successRate < 50.0) {
                weaknesses.add(skillArea);
            }
        }

        // 강점은 성공률 높은 순, 약점은 낮은 순으로 정렬
        strengths.sort((a, b) -> b.getSuccessRate().compareTo(a.getSuccessRate()));
        weaknesses.sort((a, b) -> a.getSuccessRate().compareTo(b.getSuccessRate()));

        // 개선 추세 분석 (최근 데이터를 기반으로 간단히 처리) - 실제로는 시간 경과에 따른 성공률 변화를 분석해야 하지만, 여기서는 약점 영역에 대한 개선 제안
        for (SkillArea weakness : weaknesses) {
            ImprovementTrend trend = ImprovementTrend.builder()
                .areaName(weakness.getAreaName())
                .previousRate(BigDecimal.ZERO)
                .currentRate(weakness.getSuccessRate())
                .improvementRate(BigDecimal.ZERO)
                .build();
            improvementTrends.add(trend);

            if (improvementTrends.size() >= 5) {
                break;
            }
        }

        return StrengthWeaknessAnalysisResponse.builder()
                .userId(userId)
                .userName(user.getName())
                .strengths(strengths)
                .weaknesses(weaknesses)
                .improvementTrends(improvementTrends)
                .build();
    }

    /**
     * "진행 중" 수업들의 평균 출석률 및 평균 성공률 조회
     *
     * 평균 출석률: 진행 중 수업의 모든 학생에 대한 개인별 출석률 계산 후 평균
     *   - 각 학생별로 (완료한 차시 수 / 참여한 차시 수) * 100 계산
     *   - 모든 학생의 출석률 평균 계산
     *
     * 평균 성공률: 진행 중 수업의 모든 학생에 대한 개인별 과제 성공률 계산 후 평균
     *   - 각 학생별로 (성공한 과제 수 / 전체 과제 수) * 100 계산
     *   - 모든 학생의 성공률 평균 계산
     */
    @Override
    public InProgressClassStatisticsResponse getInProgressClassStatistics() {

        // 진행 중인 수업들의 진도 데이터 조회
        List<Progress> allProgress = progressRepository.findAllByInProgressSessions();

        // 진행 중인 수업 ID 목록 조회
        List<Long> inProgressClassIds = classSubmitRepository.findAllInProgressClassIds();
        Long inProgressClassCount = (long) inProgressClassIds.size();

        // 진행 중인 수업의 총 수강생 수 조회
        Long totalStudentCount = progressRepository.countDistinctStudentsInProgressSessions();

        // 진행 중인 수업의 총 차시 수 조회 (WeeklySession의 IN_PROGRESS 상태인 것들)
        Long totalWeeklySessionCount = weeklySessionRepository.countByStatus(
            com.cinemax.global.enums.WeeklySessionStatus.IN_PROGRESS
        );

        // 학생별 출석률 계산 (완료한 차시 수 / 전체 차시 수)
        List<Object[]> attendanceRates = progressRepository.calculateAttendanceRateByStudent();

        BigDecimal averageAttendanceRate = BigDecimal.ZERO;
        if (!attendanceRates.isEmpty()) {
            BigDecimal totalAttendanceRate = attendanceRates.stream()
                .map(arr -> new BigDecimal(arr[1].toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            averageAttendanceRate = totalAttendanceRate.divide(
                BigDecimal.valueOf(attendanceRates.size()),
                2,
                RoundingMode.HALF_UP
            );
        }

        // 학생별 과제 성공률 계산 - 진행 중인 수업에 등록된 모든 학생 ID 조회
        List<Long> studentIdsInProgressClassList = classSubmitRepository.findAllStudentIdsInProgressClasses();
        List<Double> successRateList = new ArrayList<>();

        for (Long studentId : studentIdsInProgressClassList) {
            for (Long classId : inProgressClassIds) {
                Double successRate = classSubmitRepository.calculateStudentSuccessRateInClass(studentId, classId);
                if (successRate != null && successRate > 0) {
                    successRateList.add(successRate);
                }
            }
        }

        BigDecimal averageSuccessRate = BigDecimal.ZERO;
        if (!successRateList.isEmpty()) {
            double totalSuccessRate = successRateList.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

            averageSuccessRate = BigDecimal.valueOf(totalSuccessRate / successRateList.size())
                .setScale(2, RoundingMode.HALF_UP);
        }

        return InProgressClassStatisticsResponse.of(
            averageAttendanceRate,
            averageSuccessRate,
            inProgressClassCount,
            totalStudentCount,
            totalWeeklySessionCount
        );
    }

    /*
     * //==========================STUB 유틸==========================\\
     */
    // 관리자 시스템 전체 대시보드 조회
    @Override
    public AdminSystemOverviewResponse getAdminSystemOverview() {
        // 1. 사용자 통계
        Long totalUsers = userRepository.count();
        Long studentCount = userRepository.countByRole(RoleType.STUDENT);
        Long professorCount = userRepository.countByRole(RoleType.PROFESSOR);
        Long adminCount = userRepository.countByRole(RoleType.ADMIN);

        // 최근 활동한 사용자 수 (최근 7일 기준)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        // 간소화: 전체 진도 데이터로 활성 사용자 추정
        List<Progress> recentProgress = progressRepository.findAll().stream()
                .filter(p -> p.getLastActivityTime() != null && p.getLastActivityTime().isAfter(sevenDaysAgo))
                .collect(Collectors.toList());
        Long activeUsers = recentProgress.stream().map(p -> p.getUser().getUserId()).distinct().count();

        // 신규 가입자 수
        Long newUsersLast7Days = userRepository.countByCreateDtAfter(sevenDaysAgo);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Long newUsersLast30Days = userRepository.countByCreateDtAfter(thirtyDaysAgo);

        UserStatistics userStats = UserStatistics.builder()
                .totalUsers(totalUsers)
                .studentCount(studentCount)
                .professorCount(professorCount)
                .adminCount(adminCount)
                .activeUsers(activeUsers)
                .newUsersLast7Days(newUsersLast7Days)
                .newUsersLast30Days(newUsersLast30Days)
                .build();

        // 2. 수업 통계
        Long totalClasses = weeklySessionRepository.count();
        // 간소화: 활성 클래스는 전체 클래스로 처리
        Long activeClasses = totalClasses;
        Long inProgressSessions = weeklySessionRepository.countByStatus(WeeklySessionStatus.IN_PROGRESS);
        Long completedSessions = weeklySessionRepository.countByStatus(WeeklySessionStatus.COMPLETED);

        // 전체 수강 등록 수
        Long totalEnrollments = progressRepository.count();

        // 평균 수업당 학생 수
        BigDecimal averageStudentsPerClass = activeClasses > 0
                ? BigDecimal.valueOf(totalEnrollments).divide(BigDecimal.valueOf(activeClasses), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        ClassStatistics classStats = ClassStatistics.builder()
                .totalClasses(totalClasses)
                .activeClasses(activeClasses)
                .inProgressSessions(inProgressSessions)
                .completedSessions(completedSessions)
                .totalEnrollments(totalEnrollments)
                .averageStudentsPerClass(averageStudentsPerClass)
                .build();

        // 3. 커리큘럼 통계
        Long totalCurriculums = userRepository.count(); // 실제로는 CurriculumRepository 사용
        Long activeCurriculums = userRepository.count(); // useYn = true인 커리큘럼

        // 언어별 커리큘럼 수 (간단히 처리)
        Long pythonCurriculums = 0L;
        Long javaCurriculums = 0L;
        Long javascriptCurriculums = 0L;
        Long cCurriculums = 0L;
        Long csharpCurriculums = 0L;
        String mostUsedLanguage = "Java";

        CurriculumStatistics curriculumStats = CurriculumStatistics.builder()
                .totalCurriculums(totalCurriculums)
                .activeCurriculums(activeCurriculums)
                .pythonCurriculums(pythonCurriculums)
                .javaCurriculums(javaCurriculums)
                .javascriptCurriculums(javascriptCurriculums)
                .cCurriculums(cCurriculums)
                .csharpCurriculums(csharpCurriculums)
                .mostUsedLanguage(mostUsedLanguage)
                .build();

        // 4. 활동 통계
        Long totalSubmissions = classSubmitRepository.count();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        Long todaySubmissions = classSubmitRepository.countBySubmitAtAfter(todayStart);

        Long totalQuestions = questionRepository.count();
        // 간소화: 미답변 질문 수
        List<com.cinemax.domain.qna.entity.Question> allQuestions = questionRepository.findAll();
        Long unansweredQuestions = allQuestions.stream()
                .filter(q -> q.getStatus() == com.cinemax.global.enums.QuestionStatus.OPEN)
                .count();

        Long totalNotifications = notificationRepository.count();

        // 평균 과제 합격률
        Long totalSubs = classSubmitRepository.count();
        Long passedSubs = classSubmitRepository.countPassed();
        BigDecimal averagePassRate = totalSubs > 0
                ? BigDecimal.valueOf(passedSubs).divide(BigDecimal.valueOf(totalSubs), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 활성 학생 비율
        BigDecimal activeStudentRate = totalUsers > 0
                ? BigDecimal.valueOf(activeUsers).divide(BigDecimal.valueOf(totalUsers), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        ActivityStatistics activityStats = ActivityStatistics.builder()
                .totalSubmissions(totalSubmissions)
                .todaySubmissions(todaySubmissions)
                .totalQuestions(totalQuestions)
                .unansweredQuestions(unansweredQuestions)
                .totalNotifications(totalNotifications)
                .averagePassRate(averagePassRate)
                .activeStudentRate(activeStudentRate)
                .build();

        return AdminSystemOverviewResponse.builder()
                .userStatistics(userStats)
                .classStatistics(classStats)
                .curriculumStatistics(curriculumStats)
                .activityStatistics(activityStats)
                .build();
    }

    // 학생 성과 대시보드 조회
    @Override
    public StudentPerformanceDashboardResponse getStudentPerformanceDashboard(Long userId, Long classId) {
        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // 수업 정보 조회 (간소화)
        String className = "프로그래밍 수업"; // 실제로는 ClassRepository에서 조회

        // 해당 수업의 모든 주차 세션 조회
        List<WeeklySession> weeklySessions = weeklySessionRepository.findAllByClassId(classId);

        // 주차별 성과 데이터 수집
        List<WeeklyPerformance> weeklyPerformances = new ArrayList<>();
        BigDecimal totalStudyHours = BigDecimal.ZERO;
        Long totalSubmissions = 0L;
        BigDecimal totalSubmissionTimeMinutes = BigDecimal.ZERO;
        Long successfulSubmissions = 0L;

        for (WeeklySession session : weeklySessions) {
            Long weeklySessionId = session.getWeeklySessionId();

            // 진도 조회
            Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                    .orElse(null);
            BigDecimal progressRate = progress != null ? progress.getProgressPct() : BigDecimal.ZERO;

            // 해당 주차의 제출 통계
            List<ClassSubmit> weeklySubmits = classSubmitRepository.findByWeeklySessionId(weeklySessionId).stream()
                    .filter(cs -> cs.getSubmitYn() != null && cs.getSubmitYn())
                    .collect(Collectors.toList());

            Long submittedTasks = (long) weeklySubmits.size();
            Long passedTasks = weeklySubmits.stream()
                    .filter(cs -> Boolean.TRUE.equals(cs.getResult()))
                    .count();

            BigDecimal weekSuccessRate = submittedTasks > 0
                    ? BigDecimal.valueOf(passedTasks).divide(BigDecimal.valueOf(submittedTasks), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            // 학습 시간 조회
            BigDecimal weekStudyHours = workLogRepository.sumWorkHoursByUserIdAndWeeklySessionId(userId, weeklySessionId);
            if (weekStudyHours == null) {
                weekStudyHours = BigDecimal.ZERO;
            }

            // 평균 점수 (간소화)
            BigDecimal averageScore = weekSuccessRate;

            WeeklyPerformance weeklyPerformance = WeeklyPerformance.builder()
                    .weekNumber(session.getWeekNo())
                    .progressRate(progressRate)
                    .submittedTasks(submittedTasks)
                    .passedTasks(passedTasks)
                    .successRate(weekSuccessRate)
                    .studyHours(weekStudyHours)
                    .averageScore(averageScore)
                    .build();

            weeklyPerformances.add(weeklyPerformance);

            // 전체 통계 집계
            totalStudyHours = totalStudyHours.add(weekStudyHours);
            totalSubmissions += submittedTasks;
            successfulSubmissions += passedTasks;
        }

        // 평균 제출 시간 계산
        Double avgTimeMinutes = classSubmitRepository.calculateAverageSubmissionTimeByWeeklySession(
                weeklySessions.isEmpty() ? 0L : weeklySessions.get(0).getWeeklySessionId()
        );
        BigDecimal averageSubmissionTime = avgTimeMinutes != null
                ? BigDecimal.valueOf(avgTimeMinutes).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 전체 성공률
        BigDecimal successRate = totalSubmissions > 0
                ? BigDecimal.valueOf(successfulSubmissions).divide(BigDecimal.valueOf(totalSubmissions), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 개선 추세 분석
        String improvementTrend = "STABLE";
        BigDecimal successRateChange = BigDecimal.ZERO;

        if (weeklyPerformances.size() >= 2) {
            BigDecimal lastWeekRate = weeklyPerformances.get(weeklyPerformances.size() - 1).getSuccessRate();
            BigDecimal prevWeekRate = weeklyPerformances.get(weeklyPerformances.size() - 2).getSuccessRate();
            successRateChange = lastWeekRate.subtract(prevWeekRate);

            if (successRateChange.compareTo(BigDecimal.valueOf(5)) > 0) {
                improvementTrend = "IMPROVING";
            } else if (successRateChange.compareTo(BigDecimal.valueOf(-5)) < 0) {
                improvementTrend = "DECLINING";
            }
        }

        // 목표 진도율 설정 (간소화: 80%로 가정)
        BigDecimal targetProgress = BigDecimal.valueOf(80.0);

        // 현재 진도율 (전체 평균)
        BigDecimal currentProgress = progressRepository.getAverageProgressByUserId(userId);
        if (currentProgress == null) {
            currentProgress = BigDecimal.ZERO;
        }

        // 목표 달성률
        BigDecimal achievementRate = targetProgress.compareTo(BigDecimal.ZERO) > 0
                ? currentProgress.divide(targetProgress, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 수업 내 순위 계산 (간소화)
        List<Long> allStudentIds = classSubmitRepository.findAllStudentIdsInProgressClasses();
        Integer classRank = 1;
        Integer totalStudents = allStudentIds.size();

        // 학생들의 성공률로 순위 계산
        List<Double> successRates = new ArrayList<>();
        for (Long studentId : allStudentIds) {
            Double studentSuccessRate = classSubmitRepository.calculateStudentSuccessRateInClass(studentId, classId);
            if (studentSuccessRate != null) {
                successRates.add(studentSuccessRate);
            }
        }

        // 현재 사용자보다 높은 성공률을 가진 학생 수 + 1 = 순위
        long betterStudents = successRates.stream()
                .filter(rate -> rate > successRate.doubleValue())
                .count();
        classRank = (int) betterStudents + 1;

        return StudentPerformanceDashboardResponse.builder()
                .userId(userId)
                .userName(user.getName())
                .classId(classId)
                .className(className)
                .weeklyPerformances(weeklyPerformances)
                .totalStudyHours(totalStudyHours)
                .totalSubmissions(totalSubmissions)
                .averageSubmissionTime(averageSubmissionTime)
                .successRate(successRate)
                .improvementTrend(improvementTrend)
                .successRateChange(successRateChange)
                .targetProgress(targetProgress)
                .currentProgress(currentProgress)
                .achievementRate(achievementRate)
                .classRank(classRank)
                .totalStudents(totalStudents)
                .build();
    }

    // 스킬 레벨 분석 (stub)
    @Override
    public SkillLevelAnalysisResponse getSkillLevelAnalysis(Long userId) {
        throw new UnsupportedOperationException("Not yet implemented - requires custom repository methods");
    }
}
