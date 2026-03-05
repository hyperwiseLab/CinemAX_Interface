package com.cinemax.domain.auth.service.impl;

import com.cinemax.core.exception.BusinessException;
import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.core.error.enums.ErrorCode;
import com.cinemax.domain.auth.dto.PasswordResetRequest;
import com.cinemax.domain.auth.dto.PasswordResetResponse;
import com.cinemax.domain.auth.entity.PasswordResetRequestEntity;
import com.cinemax.domain.auth.repository.PasswordResetRequestRepository;
import com.cinemax.domain.auth.service.AuthLogService;
import com.cinemax.domain.auth.service.EmailService;
import com.cinemax.domain.auth.service.PasswordResetService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.global.enums.AuthEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 비밀번호 재설정 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final UserRepository userRepository;
    private final AuthLogService authLogService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.password-reset.expiration-minutes:30}")
    private int expirationMinutes;

    @Value("${app.password-reset.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.password-reset.rate-limit-minutes:5}")
    private int rateLimitMinutes;

    // 비밀번호 재설정 요청
    @Override
    @Transactional
    public PasswordResetResponse requestPasswordReset(PasswordResetRequest request) {

        // 사용자 존재 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("해당 이메일로 등록된 사용자를 찾을 수 없습니다."));

        // 요청 제한 확인 (5분 내 중복 요청 방지)
        LocalDateTime rateLimitTime = LocalDateTime.now().minusMinutes(rateLimitMinutes);
        Long recentRequestCount = passwordResetRequestRepository.countRecentRequestsByEmail(request.getEmail(), rateLimitTime);
        
        if (recentRequestCount > 0) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "잠시 후 다시 시도해주세요.");
        }

        // 기존 활성 요청 비활성화
        passwordResetRequestRepository.findActiveByEmail(request.getEmail(), LocalDateTime.now())
            .ifPresent(existingRequest -> {
                existingRequest.updateStatus("CANCELLED");
                passwordResetRequestRepository.save(existingRequest);
            });

        // 임시 비밀번호 생성 및 적용
        String tempPassword = generateTempPassword(12);
        user.updatePassword(passwordEncoder.encode(tempPassword));
        user.setMustChangePassword(true);
        userRepository.save(user);

        // 기록용 요청 생성 (임시 비밀번호 방식)
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes);
        PasswordResetRequestEntity resetRequest = PasswordResetRequestEntity.builder()
                .email(request.getEmail())
                .verificationCode("TEMP")
                .expiresAt(expiresAt)
                .resetMethod("TEMP_PASSWORD")
                .user(user)
                .build();

        PasswordResetRequestEntity savedRequest = passwordResetRequestRepository.save(resetRequest);

        // 임시 비밀번호 이메일 발송
        emailService.sendTemporaryPassword(request.getEmail(), tempPassword);

        // 인증 로그 기록
        authLogService.createAuthLog(user, AuthEventType.PASSWORD_RESET_REQUESTED);

        return PasswordResetResponse.builder()
                .requestId(savedRequest.getRequestId().toString())
                .email(request.getEmail())
                .resetMethod("TEMP_PASSWORD")
                .expiresAt(expiresAt)
                .message("임시 비밀번호가 이메일로 발급되었습니다.")
                .nextStep("임시 비밀번호로 로그인 후 비밀번호를 변경하세요.")
                .build();
    }

    @Override
    @Transactional
    public void cancelPasswordResetRequest(String email) {
        passwordResetRequestRepository.findActiveByEmail(email, LocalDateTime.now())
            .ifPresent(resetRequest -> {
                resetRequest.updateStatus("CANCELLED");
                passwordResetRequestRepository.save(resetRequest);
                authLogService.createAuthLog(resetRequest.getUser(), AuthEventType.PASSWORD_RESET_CANCELLED);
            });
    }

    // 임시 비밀번호 생성(대/소문자+숫자+특수문자 포함)
    private String generateTempPassword(int length) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";
        String all = upper + lower + digits + special;

        StringBuilder sb = new StringBuilder();
        sb.append(upper.charAt((int)(Math.random()*upper.length())));
        sb.append(lower.charAt((int)(Math.random()*lower.length())));
        sb.append(digits.charAt((int)(Math.random()*digits.length())));
        sb.append(special.charAt((int)(Math.random()*special.length())));
        for (int i = 4; i < length; i++) {
            sb.append(all.charAt((int)(Math.random()*all.length())));
        }
        return sb.toString();
    }
}
