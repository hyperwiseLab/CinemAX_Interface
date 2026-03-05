package com.cinemax.domain.worklog.service;

import com.cinemax.domain.worklog.dto.CycleStatisticsResponse;
import com.cinemax.domain.worklog.dto.ProfessorFeedbackRequest;
import com.cinemax.domain.worklog.dto.WeeklyFeedbackResponse;
import com.cinemax.domain.worklog.dto.WorkHourStatisticsResponse;
import com.cinemax.domain.worklog.dto.WorkLogRequest;
import com.cinemax.domain.worklog.dto.WorkLogResponse;
import com.cinemax.domain.worklog.dto.WorkLogStatisticsResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * 업무일지 서비스 인터페이스
 */
public interface WorkLogService {

    // 업무일지 생성
    WorkLogResponse createWorkLog(WorkLogRequest request);

    // 업무일지 수정
    WorkLogResponse updateWorkLog(WorkLogRequest request);

    // 업무일지 조회 (특정 날짜)
    WorkLogResponse getWorkLog(Long userId, Long weeklySessionId, LocalDate logDate);

    // 특정 사용자의 특정 주차 업무일지 목록 조회
    List<WorkLogResponse> getWorkLogsByUserAndWeeklySession(Long userId, Long weeklySessionId);

    // 특정 사용자의 모든 업무일지 조회
    List<WorkLogResponse> getWorkLogsByUser(Long userId);

    // inviteId 기반 전체 업무일지 조회
    List<WorkLogResponse> getMyAllWorkLogsByInviteId(Long userId, Long inviteId);

    // 특정 주차의 모든 학생 업무일지 조회 (교수용)
    List<WorkLogResponse> getWorkLogsByWeeklySession(Long weeklySessionId);

    // 특정 기간 업무일지 조회
    List<WorkLogResponse> getWorkLogsByDateRange(Long userId, Long weeklySessionId, LocalDate startDate, LocalDate endDate);

    // 업무일지 통계 조회
    WorkLogStatisticsResponse getStatistics(Long userId, Long weeklySessionId);

    // 업무일지 삭제
    void deleteWorkLog(Long userId, Long weeklySessionId, LocalDate logDate);

    // 업무일지 존재 여부 확인
    boolean existsWorkLog(Long userId, Long weeklySessionId, LocalDate logDate);

    // 특정 사용자의 특정 주차 학습 시간 통계 조회
    WorkHourStatisticsResponse getWorkHourStatisticsByUser(Long userId, Long weeklySessionId);

    // 특정 주차의 전체 학생 학습 시간 통계 조회 (교수용)
    WorkHourStatisticsResponse getWorkHourStatisticsByWeeklySession(Long weeklySessionId);

    // 교수 피드백 추가 (교수 전용)
    WorkLogResponse addProfessorFeedback(Long userId, Long weeklySessionId, LocalDate logDate, ProfessorFeedbackRequest request);

    // 교수 피드백 수정 (교수 전용)
    WorkLogResponse updateProfessorFeedback(Long userId, Long weeklySessionId, LocalDate logDate, ProfessorFeedbackRequest request);

    // Cycle별 통계 조회 (특정 사용자의 특정 커리큘럼)
    List<CycleStatisticsResponse> getCycleStatistics(Long userId, Long curId);

    // Week별 피드백 조회
    WeeklyFeedbackResponse getWeeklyFeedback(Long userId, Integer weekNo);

    // Class별 Cycle 통계 조회 (교수용)
    List<CycleStatisticsResponse> getCycleStatisticsByClass(Long classId);
}
