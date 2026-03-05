package com.cinemax.domain.activity.service;

import com.cinemax.domain.activity.dto.ActivityLogRequest;
import com.cinemax.domain.activity.dto.ActivityLogResponse;
import com.cinemax.domain.activity.dto.ActivityLogStatisticsResponse;
import com.cinemax.domain.activity.dto.StudentActivityResponse;
import com.cinemax.global.enums.ActivityAction;
import com.cinemax.global.enums.StudentActivityStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 실시간 활동 모니터링 서비스 인터페이스
 */
public interface ActivityMonitorService {

    // ===== 실시간 활동 상태 모니터링 =====

    // 주차별 수업의 모든 학생 활동 상태 조회
    List<StudentActivityResponse> getAllStudentActivities(Long weeklySessionId);

    // 특정 상태의 학생 목록 조회
    List<StudentActivityResponse> getStudentsByStatus(Long weeklySessionId, StudentActivityStatus status);

    // 도움이 필요한 학생 목록 조회
    List<StudentActivityResponse> getStudentsNeedingHelp(Long weeklySessionId);

    // 활동중인 학생 목록 조회
    List<StudentActivityResponse> getActiveStudents(Long weeklySessionId);

    // 완료한 학생 목록 조회
    List<StudentActivityResponse> getCompletedStudents(Long weeklySessionId);

    // 특정 학생의 활동 상태 조회
    StudentActivityResponse getStudentActivity(Long weeklySessionId, Long userId);

    // 모든 학생의 활동 상태 갱신 (스케줄러용)
    void refreshAllStudentStatuses(Long weeklySessionId);

    // 학생 활동 기록 (코드 저장, 테스트 실행 등)
    void recordActivity(Long weeklySessionId, Long userId);

    // 테스트 실패 기록
    void recordTestFailure(Long weeklySessionId, Long userId);

    // 테스트 성공 기록
    void recordTestSuccess(Long weeklySessionId, Long userId);

    // ===== 활동 로그 기록 및 조회 =====

    // 활동 로그 기록
    ActivityLogResponse recordActivityLog(ActivityLogRequest request);

    // 특정 액션 타입 활동 로그 기록
    ActivityLogResponse recordActivityLog(Long weeklySessionId, Long inviteId, Long taskId, ActivityAction action);

    // 주차별 수업의 모든 활동 로그 조회
    List<ActivityLogResponse> getAllActivityLogs(Long weeklySessionId);

    // 특정 학생(inviteId)의 활동 로그 조회
    List<ActivityLogResponse> getActivityLogsByInviteId(Long weeklySessionId, Long inviteId);

    // 특정 액션 타입의 활동 로그 조회
    List<ActivityLogResponse> getActivityLogsByAction(Long weeklySessionId, ActivityAction action);

    // 특정 시간 이후의 활동 로그 조회
    List<ActivityLogResponse> getRecentActivityLogs(Long weeklySessionId, LocalDateTime fromTime);

    // 특정 학생의 특정 시간 이후 활동 로그 조회
    List<ActivityLogResponse> getRecentActivityLogsByInviteId(Long weeklySessionId, Long inviteId, LocalDateTime fromTime);

    // 특정 학생의 활동 로그 통계 조회
    ActivityLogStatisticsResponse getActivityLogStatistics(Long weeklySessionId, Long inviteId);

    // 특정 학생의 최근 활동 조회
    ActivityLogResponse getLatestActivityLog(Long weeklySessionId, Long inviteId);

    // 유저/과제별 에러(테스트 실패) 카운트 조회
    long getErrorCount(Long userId, Long taskId);

    // 에러(테스트 실패) 기록
    ActivityLogResponse recordErrorCount(Long userId, Long weeklySessionId, Long taskId);
}
