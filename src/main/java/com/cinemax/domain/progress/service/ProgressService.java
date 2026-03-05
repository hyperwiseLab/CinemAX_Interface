package com.cinemax.domain.progress.service;

import com.cinemax.domain.progress.dto.ProgressRequest;
import com.cinemax.domain.progress.dto.ProgressResponse;
import com.cinemax.domain.progress.dto.ProgressStatisticsResponse;
import com.cinemax.global.enums.StudentActivityStatus;
import com.cinemax.global.enums.TaskMode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 진도 관리 서비스 인터페이스
 */
public interface ProgressService {

    // 진도 생성 (학생이 수업 참여 시 자동 생성)
    ProgressResponse createProgress(ProgressRequest request);

    // 진도 조회 (특정 학생의 특정 주차 진도)
    ProgressResponse getProgress(Long weeklySessionId, Long userId);

    // 주차별 수업의 모든 학생 진도 조회
    List<ProgressResponse> getAllProgressByWeeklySession(Long weeklySessionId);

    // 수업의 모든 학생 진도 조회
    List<ProgressResponse> getAllProgressByClass(Long classId);

    // 진도율 업데이트
    ProgressResponse updateProgressPct(Long weeklySessionId, Long userId, BigDecimal progressPct);

    // 진도 완료 처리
    ProgressResponse markAsCompleted(Long weeklySessionId, Long userId);

    // 활동 상태 업데이트
    ProgressResponse updateActivityStatus(Long weeklySessionId, Long userId, StudentActivityStatus activityStatus);

    // 모드 업데이트
    ProgressResponse updateMode(Long weeklySessionId, Long userId, TaskMode mode);

    //진도 통계 조회
    ProgressStatisticsResponse getStatistics(Long weeklySessionId);

    // 학생의 전체 수업 진도 목록 조회
    List<ProgressResponse> getStudentAllProgress(Long userId);

    // 진도 존재 여부 확인
    boolean existsProgress(Long weeklySessionId, Long userId);

    // 진도 삭제 (수강 취소 시)
    void deleteProgress(Long weeklySessionId, Long userId);
}
