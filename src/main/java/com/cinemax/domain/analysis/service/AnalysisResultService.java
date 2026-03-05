package com.cinemax.domain.analysis.service;

import com.cinemax.domain.analysis.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * AI 분석 결과 서비스 인터페이스
 */
public interface AnalysisResultService {

    // 분석 결과 저장
    AnalysisResultResponse saveAnalysisResult(AnalysisResultRequest request);

    // 분석 결과 상세 조회
    AnalysisResultResponse getAnalysisResult(Long analysisId);

    // 사용자별 분석 히스토리 조회 (페이징)
    Page<AnalysisHistoryResponse> getUserHistory(Long userId, Pageable pageable);

    // 사용자 + 과제별 분석 히스토리 조회
    Page<AnalysisHistoryResponse> getUserHistoryByTask(Long userId, Long taskId, Pageable pageable);

    // 사용자 + 사이클별 분석 히스토리 조회
    Page<AnalysisHistoryResponse> getUserHistoryByCycle(Long userId, Long cycleId, Pageable pageable);

    //특정 기간 내 분석 결과 조회
    Page<AnalysisHistoryResponse> getUserHistoryByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // 제출 ID로 분석 결과 조회
    AnalysisResultResponse getAnalysisResultBySubmitId(Long submitId);

    // 과제별 통계 조회 (교수용)
    AnalysisStatisticsResponse getTaskStatistics(Long taskId);

    // 수업별 통계 조회 (교수용)
    AnalysisStatisticsResponse getClassStatistics(Long classId, Long cycleId);

    // 분석 결과 플래그 업데이트
    AnalysisResultResponse updateAnalysisFlags(Long analysisId, Boolean requirementsMet, Boolean codeQualityPass, Boolean hasLogicError, Boolean hasSecurityIssue, Boolean needsImprovement);

    // 분석 결과 삭제
    void deleteAnalysisResult(Long analysisId);

    // 토큰 사용량 통계 조회 (관리자 전용)
    TokenUsageStatisticsResponse getTokenUsageStatistics(LocalDateTime startDate, LocalDateTime endDate, Long classId, Long cycleId);

    // 전체 토큰 사용량 조회 (관리자 전용)
    TokenUsageStatisticsResponse getTotalTokenUsageStatistics();
}
