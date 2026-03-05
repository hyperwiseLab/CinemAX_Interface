package com.cinemax.domain.auth.dto;

import com.cinemax.global.enums.AuthEventType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 인증 로그 통계 응답 DTO
 */
@Getter
@Builder
public class AuthLogStatisticsResponse {

    private Long totalLogs;
    private Map<AuthEventType, Long> eventStatistics;
    private List<AuthLogSummary> recentLogs;
    private SecurityMetrics securityMetrics;

    @Getter
    @Builder
    public static class AuthLogSummary {
        private Long authLogId;
        private String email;
        private AuthEventType eventType;
        private String eventDescription;
        private String createDt;
    }

    @Getter
    @Builder
    public static class SecurityMetrics {
        private Long totalLoginAttempts;
        private Long successfulLogins;
        private Long failedLogins;
        private Long suspiciousActivities;
        private Double successRate;
        private Long recentFailedAttempts;
    }
}

