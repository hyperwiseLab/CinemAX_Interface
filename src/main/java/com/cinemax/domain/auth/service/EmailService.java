package com.cinemax.domain.auth.service;

import java.time.LocalDateTime;

/**
 * 이메일 서비스 인터페이스
 */
public interface EmailService {

    // 이메일 인증 코드 발송
    void sendEmailVerificationCode(String email, String verificationCode, LocalDateTime expiresAt);

    // 계정 보안 알림 이메일 발송
    void sendSecurityAlertEmail(String email, String eventType, String details);

    // 임시 비밀번호 발송
    void sendTemporaryPassword(String email, String tempPassword);
}