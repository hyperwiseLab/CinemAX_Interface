package com.cinemax.domain.auth.service;

import com.cinemax.domain.auth.dto.AuthLogRequest;
import com.cinemax.domain.auth.dto.AuthLogResponse;
import com.cinemax.domain.auth.dto.AuthLogStatisticsResponse;
import com.cinemax.domain.user.entity.User;
import com.cinemax.global.enums.AuthEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 인증 로그 서비스 인터페이스
 */
public interface AuthLogService {

    /**
     * 인증 로그 생성
     */
    AuthLogResponse createAuthLog(AuthLogRequest request);

    /**
     * 인증 로그 생성 (편의 메서드)
     */
    AuthLogResponse createAuthLog(User user, AuthEventType eventType);

    /**
     * 인증 로그 생성 (이메일만으로)
     */
    AuthLogResponse createAuthLog(String email, AuthEventType eventType);

    /**
     * 인증 로그 조회
     */
    AuthLogResponse getAuthLog(Long authLogId);

    /**
     * 사용자별 인증 로그 조회
     */
    List<AuthLogResponse> getAuthLogsByUserId(Long userId);

    /**
     * 사용자별 인증 로그 조회 (페이징)
     */
    Page<AuthLogResponse> getAuthLogsByUserId(Long userId, Pageable pageable);

    /**
     * 이메일별 인증 로그 조회
     */
    List<AuthLogResponse> getAuthLogsByEmail(String email);

    /**
     * 이메일별 인증 로그 조회 (페이징)
     */
    Page<AuthLogResponse> getAuthLogsByEmail(String email, Pageable pageable);

    /**
     * 이벤트 타입별 인증 로그 조회
     */
    List<AuthLogResponse> getAuthLogsByEventType(AuthEventType eventType);

    /**
     * 이벤트 타입별 인증 로그 조회 (페이징)
     */
    Page<AuthLogResponse> getAuthLogsByEventType(AuthEventType eventType, Pageable pageable);

    /**
     * 기간별 인증 로그 조회
     */
    List<AuthLogResponse> getAuthLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 기간별 인증 로그 조회 (페이징)
     */
    Page<AuthLogResponse> getAuthLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 사용자별 기간별 인증 로그 조회
     */
    List<AuthLogResponse> getAuthLogsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 최근 로그인 성공 로그 조회
     */
    AuthLogResponse getLatestLoginSuccess(Long userId);

    /**
     * 최근 로그인 실패 로그 조회 (최대 5개)
     */
    List<AuthLogResponse> getRecentLoginFailures(Long userId);

    /**
     * 특정 시간 내 로그인 실패 횟수 조회
     */
    Long getLoginFailureCountSince(Long userId, LocalDateTime since);

    /**
     * 이메일로 특정 시간 내 로그인 실패 횟수 조회
     */
    Long getLoginFailureCountByEmailSince(String email, LocalDateTime since);

    /**
     * 사용자별 인증 로그 통계 조회
     */
    AuthLogStatisticsResponse getAuthLogStatisticsByUserId(Long userId);

    /**
     * 전체 인증 로그 통계 조회
     */
    AuthLogStatisticsResponse getOverallAuthLogStatistics();

    /**
     * 기간별 인증 로그 통계 조회
     */
    AuthLogStatisticsResponse getAuthLogStatisticsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 의심스러운 활동 감지
     */
    List<AuthLogResponse> detectSuspiciousActivity(Long userId, LocalDateTime since);

    /**
     * 이메일로 의심스러운 활동 감지
     */
    List<AuthLogResponse> detectSuspiciousActivityByEmail(String email, LocalDateTime since);

    /**
     * 인증 로그 삭제
     */
    void deleteAuthLog(Long authLogId);

    /**
     * 사용자별 인증 로그 삭제
     */
    void deleteAuthLogsByUserId(Long userId);

    /**
     * 기간별 인증 로그 삭제
     */
    void deleteAuthLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}

