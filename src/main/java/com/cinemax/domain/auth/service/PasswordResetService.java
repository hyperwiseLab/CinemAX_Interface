package com.cinemax.domain.auth.service;

import com.cinemax.domain.auth.dto.PasswordResetRequest;
import com.cinemax.domain.auth.dto.PasswordResetResponse;

/**
 * 비밀번호 재설정 서비스 인터페이스 (임시비밀번호 발급 전용)
 */
public interface PasswordResetService {

    // 임시비밀번호 발급 요청
    PasswordResetResponse requestPasswordReset(PasswordResetRequest request);

    // 비밀번호 재설정 요청 취소
    void cancelPasswordResetRequest(String email);

    // 만료된 비밀번호 재설정 요청 정리
//    void cleanupExpiredRequests();
}
