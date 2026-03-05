package com.cinemax.domain.auth.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.auth.dto.AuthLogRequest;
import com.cinemax.domain.auth.dto.AuthLogResponse;
import com.cinemax.domain.auth.dto.AuthLogStatisticsResponse;
import com.cinemax.domain.auth.entity.AuthLog;
import com.cinemax.domain.auth.repository.AuthLogRepository;
import com.cinemax.domain.auth.service.AuthLogService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.global.enums.AuthEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 인증 로그 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthLogServiceImpl implements AuthLogService {

    private final AuthLogRepository authLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuthLogResponse createAuthLog(AuthLogRequest request) {

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. ID: " + request.getUserId()));
        }

        AuthLog authLog = AuthLog.builder()
                .email(request.getEmail())
                .event(request.getEventType().getCode())
                .user(user)
                .build();

        AuthLog savedAuthLog = authLogRepository.save(authLog);

        return AuthLogResponse.from(savedAuthLog);
    }

    // 사용자 인증 로그 생성
    @Override
    @Transactional
    public AuthLogResponse createAuthLog(User user, AuthEventType eventType) {

        AuthLog authLog = AuthLog.builder()
                .email(user.getEmail())
                .event(eventType.getCode())
                .user(user)
                .build();

        AuthLog savedAuthLog = authLogRepository.save(authLog);

        return AuthLogResponse.from(savedAuthLog);
    }

    // 이메일 인증 로그 생성
    @Override
    @Transactional
    public AuthLogResponse createAuthLog(String email, AuthEventType eventType) {

        AuthLog authLog = AuthLog.builder()
                .email(email)
                .event(eventType.getCode())
                .user(null)
                .build();

        AuthLog savedAuthLog = authLogRepository.save(authLog);

        return AuthLogResponse.from(savedAuthLog);
    }

    // 인증 로그 조회
    @Override
    public AuthLogResponse getAuthLog(Long authLogId) {

        AuthLog authLog = authLogRepository.findById(authLogId)
                .orElseThrow(() -> new ResourceNotFoundException("인증 로그를 찾을 수 없습니다. ID: " + authLogId));

        return AuthLogResponse.from(authLog);
    }

    // 사용자 별 인증 로그 조회
    @Override
    public List<AuthLogResponse> getAuthLogsByUserId(Long userId) {

        List<AuthLog> authLogs = authLogRepository.findByUserId(userId);

        return authLogs.stream()
                .map(AuthLogResponse::from)
                .toList();
    }

    // 사용자 별 인증 로그 조회 (페이징)
    @Override
    public Page<AuthLogResponse> getAuthLogsByUserId(Long userId, Pageable pageable) {

        Page<AuthLog> authLogs = authLogRepository.findByUserIdOrderByCreateDtDesc(userId, pageable);

        return authLogs.map(AuthLogResponse::from);
    }

    // 이메일 별 인증 로그 조회
    @Override
    public List<AuthLogResponse> getAuthLogsByEmail(String email) {

        List<AuthLog> authLogs = authLogRepository.findByEmail(email);

        return authLogs.stream()
                .map(AuthLogResponse::from)
                .toList();
    }

    // 이메일 별 인증 로그 조회 (페이징)
    @Override
    public Page<AuthLogResponse> getAuthLogsByEmail(String email, Pageable pageable) {

        Page<AuthLog> authLogs = authLogRepository.findByEmailOrderByCreateDtDesc(email, pageable);

        return authLogs.map(AuthLogResponse::from);
    }

    // 이벤트 타입별 인증 로그 조회
    @Override
    public List<AuthLogResponse> getAuthLogsByEventType(AuthEventType eventType) {

        List<AuthLog> authLogs = authLogRepository.findByEventOrderByCreateDtDesc(eventType.getCode());

        return authLogs.stream()
                .map(AuthLogResponse::from)
                .toList();
    }

    // 이벤트 타입별 인증 로그 조회 (페이징)
    @Override
    public Page<AuthLogResponse> getAuthLogsByEventType(AuthEventType eventType, Pageable pageable) {

        Page<AuthLog> authLogs = authLogRepository.findByEventOrderByCreateDtDesc(eventType.getCode(), pageable);

        return authLogs.map(AuthLogResponse::from);
    }

    // 기간별 인증 로그 조회
    @Override
    public List<AuthLogResponse> getAuthLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {

        List<AuthLog> authLogs = authLogRepository.findByCreateDtBetweenOrderByCreateDtDesc(startDate, endDate);

        return authLogs.stream()
                .map(AuthLogResponse::from)
                .toList();
    }

    // 기간별 인증 로그 조회 (페이징)
    @Override
    public Page<AuthLogResponse> getAuthLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        Page<AuthLog> authLogs = authLogRepository.findByCreateDtBetweenOrderByCreateDtDesc(startDate, endDate, pageable);

        return authLogs.map(AuthLogResponse::from);
    }

    // 사용자별 기간별 인증 로그 조회
    @Override
    public List<AuthLogResponse> getAuthLogsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {

        List<AuthLog> authLogs = authLogRepository.findByUserIdAndCreateDtBetweenOrderByCreateDtDesc(userId, startDate, endDate);

        return authLogs.stream()
                .map(AuthLogResponse::from)
                .toList();
    }

    // 최근 로그인 성공 로그 조회
    @Override
    public AuthLogResponse getLatestLoginSuccess(Long userId) {

        AuthLog authLog = authLogRepository.findLatestLoginSuccessByUserId(userId, AuthEventType.LOGIN_SUCCESS.getCode())
                .orElse(null);

        return authLog != null ? AuthLogResponse.from(authLog) : null;
    }

    @Override
    public List<AuthLogResponse> getRecentLoginFailures(Long userId) {
        log.info("최근 로그인 실패 로그 조회: userId={}", userId);

        List<AuthLog> authLogs = authLogRepository.findTop5LatestLoginFailuresByUserId(userId, AuthEventType.LOGIN_FAILED.getCode());
        return authLogs.stream()
                .map(AuthLogResponse::from)
                .toList();
    }

    @Override
    public Long getLoginFailureCountSince(Long userId, LocalDateTime since) {
        log.info("로그인 실패 횟수 조회: userId={}, since={}", userId, since);

        return authLogRepository.countLoginFailuresSince(userId, AuthEventType.LOGIN_FAILED.getCode(), since);
    }

    @Override
    public Long getLoginFailureCountByEmailSince(String email, LocalDateTime since) {
        log.info("이메일별 로그인 실패 횟수 조회: email={}, since={}", email, since);

        return authLogRepository.countLoginFailuresByEmailSince(email, AuthEventType.LOGIN_FAILED.getCode(), since);
    }

    @Override
    public AuthLogStatisticsResponse getAuthLogStatisticsByUserId(Long userId) {
        log.info("사용자별 인증 로그 통계 조회: userId={}", userId);

        List<Object[]> statistics = authLogRepository.getAuthLogStatisticsByUserId(userId);
        Map<AuthEventType, Long> eventStats = statistics.stream()
                .collect(Collectors.toMap(
                        stat -> AuthEventType.fromCode((Integer) stat[0]),
                        stat -> (Long) stat[1]
                ));

        // 최근 로그 조회
        List<AuthLogResponse> recentLogs = getAuthLogsByUserId(userId).stream()
                .limit(10)
                .toList();

        // 보안 메트릭 계산
        Long totalLoginAttempts = eventStats.getOrDefault(AuthEventType.LOGIN_SUCCESS, 0L) + 
                                 eventStats.getOrDefault(AuthEventType.LOGIN_FAILED, 0L);
        Long successfulLogins = eventStats.getOrDefault(AuthEventType.LOGIN_SUCCESS, 0L);
        Long failedLogins = eventStats.getOrDefault(AuthEventType.LOGIN_FAILED, 0L);
        Long suspiciousActivities = eventStats.getOrDefault(AuthEventType.SUSPICIOUS_ACTIVITY, 0L);
        Double successRate = totalLoginAttempts > 0 ? (double) successfulLogins / totalLoginAttempts * 100 : 0.0;
        Long recentFailedAttempts = getLoginFailureCountSince(userId, LocalDateTime.now().minusHours(24));

        AuthLogStatisticsResponse.SecurityMetrics securityMetrics = AuthLogStatisticsResponse.SecurityMetrics.builder()
                .totalLoginAttempts(totalLoginAttempts)
                .successfulLogins(successfulLogins)
                .failedLogins(failedLogins)
                .suspiciousActivities(suspiciousActivities)
                .successRate(successRate)
                .recentFailedAttempts(recentFailedAttempts)
                .build();

        return AuthLogStatisticsResponse.builder()
                .totalLogs(statistics.stream().mapToLong(stat -> (Long) stat[1]).sum())
                .eventStatistics(eventStats)
                .recentLogs(recentLogs.stream()
                        .map(log -> AuthLogStatisticsResponse.AuthLogSummary.builder()
                                .authLogId(log.getAuthLogId())
                                .email(log.getEmail())
                                .eventType(AuthEventType.fromCodeOrNull(log.getEvent()))
                                .eventDescription(log.getEventDescription())
                                .createDt(log.getCreateDt().toString())
                                .build())
                        .toList())
                .securityMetrics(securityMetrics)
                .build();
    }

    @Override
    public AuthLogStatisticsResponse getOverallAuthLogStatistics() {
        log.info("전체 인증 로그 통계 조회");

        List<Object[]> statistics = authLogRepository.getOverallAuthLogStatistics();
        Map<AuthEventType, Long> eventStats = statistics.stream()
                .collect(Collectors.toMap(
                        stat -> AuthEventType.fromCode((Integer) stat[0]),
                        stat -> (Long) stat[1]
                ));

        // 보안 메트릭 계산
        Long totalLoginAttempts = eventStats.getOrDefault(AuthEventType.LOGIN_SUCCESS, 0L) + 
                                 eventStats.getOrDefault(AuthEventType.LOGIN_FAILED, 0L);
        Long successfulLogins = eventStats.getOrDefault(AuthEventType.LOGIN_SUCCESS, 0L);
        Long failedLogins = eventStats.getOrDefault(AuthEventType.LOGIN_FAILED, 0L);
        Long suspiciousActivities = eventStats.getOrDefault(AuthEventType.SUSPICIOUS_ACTIVITY, 0L);
        Double successRate = totalLoginAttempts > 0 ? (double) successfulLogins / totalLoginAttempts * 100 : 0.0;

        AuthLogStatisticsResponse.SecurityMetrics securityMetrics = AuthLogStatisticsResponse.SecurityMetrics.builder()
                .totalLoginAttempts(totalLoginAttempts)
                .successfulLogins(successfulLogins)
                .failedLogins(failedLogins)
                .suspiciousActivities(suspiciousActivities)
                .successRate(successRate)
                .recentFailedAttempts(0L) // 전체 통계에서는 최근 실패 횟수 계산 생략
                .build();

        return AuthLogStatisticsResponse.builder()
                .totalLogs(statistics.stream().mapToLong(stat -> (Long) stat[1]).sum())
                .eventStatistics(eventStats)
                .recentLogs(List.of()) // 전체 통계에서는 최근 로그 조회 생략
                .securityMetrics(securityMetrics)
                .build();
    }

    @Override
    public AuthLogStatisticsResponse getAuthLogStatisticsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("기간별 인증 로그 통계 조회: startDate={}, endDate={}", startDate, endDate);

        List<Object[]> statistics = authLogRepository.getAuthLogStatisticsByDateRange(startDate, endDate);
        Map<AuthEventType, Long> eventStats = statistics.stream()
                .collect(Collectors.toMap(
                        stat -> AuthEventType.fromCode((Integer) stat[0]),
                        stat -> (Long) stat[1]
                ));

        // 보안 메트릭 계산
        Long totalLoginAttempts = eventStats.getOrDefault(AuthEventType.LOGIN_SUCCESS, 0L) + 
                                 eventStats.getOrDefault(AuthEventType.LOGIN_FAILED, 0L);
        Long successfulLogins = eventStats.getOrDefault(AuthEventType.LOGIN_SUCCESS, 0L);
        Long failedLogins = eventStats.getOrDefault(AuthEventType.LOGIN_FAILED, 0L);
        Long suspiciousActivities = eventStats.getOrDefault(AuthEventType.SUSPICIOUS_ACTIVITY, 0L);
        Double successRate = totalLoginAttempts > 0 ? (double) successfulLogins / totalLoginAttempts * 100 : 0.0;

        AuthLogStatisticsResponse.SecurityMetrics securityMetrics = AuthLogStatisticsResponse.SecurityMetrics.builder()
                .totalLoginAttempts(totalLoginAttempts)
                .successfulLogins(successfulLogins)
                .failedLogins(failedLogins)
                .suspiciousActivities(suspiciousActivities)
                .successRate(successRate)
                .recentFailedAttempts(0L) // 기간별 통계에서는 최근 실패 횟수 계산 생략
                .build();

        return AuthLogStatisticsResponse.builder()
                .totalLogs(statistics.stream().mapToLong(stat -> (Long) stat[1]).sum())
                .eventStatistics(eventStats)
                .recentLogs(List.of()) // 기간별 통계에서는 최근 로그 조회 생략
                .securityMetrics(securityMetrics)
                .build();
    }

    @Override
    public List<AuthLogResponse> detectSuspiciousActivity(Long userId, LocalDateTime since) {
        log.info("의심스러운 활동 감지: userId={}, since={}", userId, since);

        List<AuthLog> authLogs = authLogRepository.findSuspiciousLoginFailures(userId, AuthEventType.LOGIN_FAILED.getCode(), since);
        return authLogs.stream()
                .map(AuthLogResponse::from)
                .toList();
    }

    @Override
    public List<AuthLogResponse> detectSuspiciousActivityByEmail(String email, LocalDateTime since) {
        log.info("이메일별 의심스러운 활동 감지: email={}, since={}", email, since);

        List<AuthLog> authLogs = authLogRepository.findSuspiciousLoginFailuresByEmail(email, AuthEventType.LOGIN_FAILED.getCode(), since);
        return authLogs.stream()
                .map(AuthLogResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAuthLog(Long authLogId) {
        log.info("인증 로그 삭제: authLogId={}", authLogId);

        AuthLog authLog = authLogRepository.findById(authLogId)
                .orElseThrow(() -> new ResourceNotFoundException("인증 로그를 찾을 수 없습니다. ID: " + authLogId));

        authLogRepository.delete(authLog);
        log.info("인증 로그 삭제 완료: authLogId={}", authLogId);
    }

    @Override
    @Transactional
    public void deleteAuthLogsByUserId(Long userId) {
        log.info("사용자별 인증 로그 삭제: userId={}", userId);

        List<AuthLog> authLogs = authLogRepository.findByUserId(userId);
        authLogRepository.deleteAll(authLogs);
        log.info("사용자별 인증 로그 삭제 완료: userId={}, count={}", userId, authLogs.size());
    }

    @Override
    @Transactional
    public void deleteAuthLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("기간별 인증 로그 삭제: startDate={}, endDate={}", startDate, endDate);

        List<AuthLog> authLogs = authLogRepository.findByCreateDtBetweenOrderByCreateDtDesc(startDate, endDate);
        authLogRepository.deleteAll(authLogs);
        log.info("기간별 인증 로그 삭제 완료: count={}", authLogs.size());
    }
}

