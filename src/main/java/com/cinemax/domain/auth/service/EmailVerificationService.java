package com.cinemax.domain.auth.service;

import com.cinemax.domain.auth.dto.EmailVerificationRequest;
import com.cinemax.domain.auth.dto.EmailVerificationResponse;
import com.cinemax.domain.auth.dto.EmailVerificationStatusResponse;
import com.cinemax.domain.auth.dto.SendEmailVerificationRequest;

import java.util.List;

/**
 * 이메일 인증 서비스 인터페이스
 */
public interface EmailVerificationService {

    /**
     * 이메일 인증 코드 발송
     */
    EmailVerificationResponse sendVerificationCode(SendEmailVerificationRequest request);

    /**
     * 이메일 인증 코드 검증
     */
    EmailVerificationResponse verifyCode(EmailVerificationRequest request);

    /**
     * 이메일 인증 코드 재발송
     */
    EmailVerificationResponse resendVerificationCode(String email);

    /**
     * 사용자의 이메일 인증 상태 조회
     */
    EmailVerificationStatusResponse getVerificationStatus(Long userId);

    /**
     * 사용자의 이메일 인증 상태 조회 (이메일로)
     */
    EmailVerificationStatusResponse getVerificationStatusByEmail(String email);

    /**
     * 사용자의 모든 이메일 인증 이력 조회
     */
    List<EmailVerificationResponse> getVerificationHistory(Long userId);

    /**
     * 이메일 인증 완료 여부 확인
     */
    boolean isEmailVerified(Long userId);

    /**
     * 이메일 인증 완료 여부 확인 (이메일로)
     */
    boolean isEmailVerifiedByEmail(String email);

    /**
     * 만료된 인증 코드 정리
     */
    void cleanupExpiredVerifications();

    /**
     * 사용자의 모든 인증 코드 삭제
     */
    void deleteAllVerificationsByUserId(Long userId);

    /**
     * 특정 인증 코드 삭제
     */
    void deleteVerification(Long verificationId, Long userId);
}
