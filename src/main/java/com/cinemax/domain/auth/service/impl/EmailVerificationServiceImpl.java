package com.cinemax.domain.auth.service.impl;

import com.cinemax.core.exception.BusinessException;
import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.core.error.enums.ErrorCode;
import com.cinemax.domain.auth.dto.EmailVerificationRequest;
import com.cinemax.domain.auth.dto.EmailVerificationResponse;
import com.cinemax.domain.auth.dto.EmailVerificationStatusResponse;
import com.cinemax.domain.auth.dto.SendEmailVerificationRequest;
import com.cinemax.domain.auth.entity.EmailVerification;
import com.cinemax.domain.auth.repository.EmailVerificationRepository;
import com.cinemax.domain.auth.service.EmailService;
import com.cinemax.domain.auth.service.EmailVerificationService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRY_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    // 이메일 인증 코드 발송 요청
    @Override
    @Transactional
    public EmailVerificationResponse sendVerificationCode(SendEmailVerificationRequest request) {

        // 사용자 조회 (선택적 - 회원가입 전에는 없을 수 있음)
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        Long userId = userOptional.map(User::getUserId).orElse(null); // 회원가입 전에는 null

        // 기존 활성 인증 코드가 있는지 확인 (이메일 기반)
        Optional<EmailVerification> existingVerification = emailVerificationRepository
                .findActiveByEmail(request.getEmail(), LocalDateTime.now());

        EmailVerification emailVerification;
        if (existingVerification.isPresent()) {
            // 기존 코드 재사용 (만료 시간 연장)
            emailVerification = existingVerification.get();
            String newCode = generateVerificationCode();
            emailVerification.regenerateCode(newCode, LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
        } else {
            // 새 인증 코드 생성
            String code = generateVerificationCode();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES);

            emailVerification = EmailVerification.builder()
                    .verificationId(generateVerificationId())
                    .userId(userId)
                    .email(request.getEmail())
                    .code(code)
                    .expiresAt(expiresAt)
                    .attemptCount((byte) 0)
                    .createDt(LocalDateTime.now())
                    .build();
        }

        EmailVerification savedVerification = emailVerificationRepository.save(emailVerification);

        // 이메일 발송
        try {
            emailService.sendEmailVerificationCode(request.getEmail(), savedVerification.getCode(), savedVerification.getExpiresAt());
            log.info("인증 코드 이메일 발송 완료: {}", request.getEmail());
        } catch (Exception e) {
            log.error("인증 코드 이메일 발송 실패: {}", request.getEmail(), e);
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED, "이메일 발송에 실패했습니다.");
        }

        return EmailVerificationResponse.from(savedVerification);
    }

    // 이메일 인증 코드 검증 요청
    @Override
    @Transactional
    public EmailVerificationResponse verifyCode(EmailVerificationRequest request) {

        // 활성 인증 코드 조회 (이메일 기반)
        EmailVerification emailVerification = emailVerificationRepository
                .findActiveByEmail(request.getEmail(), LocalDateTime.now())
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_CODE_NOT_FOUND, "인증 코드를 찾을 수 없습니다."));

        // 인증 코드 검증
        if (!emailVerification.verifyCode(request.getCode())) {
            emailVerification.incrementAttemptCount();
            emailVerificationRepository.save(emailVerification);

            if (emailVerification.getAttemptCount() >= MAX_ATTEMPTS) {
                throw new BusinessException(ErrorCode.VERIFICATION_ATTEMPTS_EXCEEDED, "인증 시도 횟수를 초과했습니다.");
            }

            throw new BusinessException(ErrorCode.VERIFICATION_CODE_INVALID, "인증 코드가 올바르지 않습니다.");
        }

        // 인증 완료 처리
        emailVerification.markAsVerified();
        EmailVerification savedVerification = emailVerificationRepository.save(emailVerification);

        return EmailVerificationResponse.from(savedVerification);
    }

    // 이메일 인증 코드 재발송 요청
    @Override
    @Transactional
    public EmailVerificationResponse resendVerificationCode(String email) {

        SendEmailVerificationRequest request = SendEmailVerificationRequest.builder()
                .email(email)
                .purpose("재발송")
                .build();

        return sendVerificationCode(request);
    }

    // 이메일 인증 상태 조회
    @Override
    public EmailVerificationStatusResponse getVerificationStatus(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        boolean isVerified = emailVerificationRepository.isUserEmailVerified(userId);
        Optional<Byte> latestAttemptCount = emailVerificationRepository.findLatestAttemptCountByUserId(userId);

        int attemptCount = latestAttemptCount.orElse((byte) 0);
        int remainingAttempts = Math.max(0, MAX_ATTEMPTS - attemptCount);

        String message = isVerified ? "이메일 인증이 완료되었습니다." : "이메일 인증이 필요합니다.";

        return EmailVerificationStatusResponse.builder()
                .userId(userId)
                .email(user.getEmail())
                .isVerified(isVerified)
                .attemptCount(attemptCount)
                .remainingAttempts(remainingAttempts)
                .message(message)
                .build();
    }

    // 특정 이메일에 대한 인증 상태 조회
    @Override
    public EmailVerificationStatusResponse getVerificationStatusByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return getVerificationStatus(user.getUserId());
    }

    // 이메일 인증 이력 조회
    @Override
    public List<EmailVerificationResponse> getVerificationHistory(Long userId) {

        List<EmailVerification> verifications = emailVerificationRepository.findByUserId(userId);

        return verifications.stream()
                .map(EmailVerificationResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEmailVerified(Long userId) {
        return emailVerificationRepository.isUserEmailVerified(userId);
    }

    @Override
    public boolean isEmailVerifiedByEmail(String email) {
        return emailVerificationRepository.isEmailVerified(email);
    }

    // 만료된 인증 코드 정리
    @Override
    @Transactional
    public void cleanupExpiredVerifications() {
        emailVerificationRepository.deleteExpiredVerifications(LocalDateTime.now());
    }

    // 사용자 인증 코드 전체 삭제
    @Override
    @Transactional
    public void deleteAllVerificationsByUserId(Long userId) {
        emailVerificationRepository.deleteByUserId(userId);
    }

    // 특정 인증 코드 이력 삭제
    @Override
    @Transactional
    public void deleteVerification(Long verificationId, Long userId) {
        emailVerificationRepository.deleteById(verificationId);
    }

    // 6자리 인증 코드 생성
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }

    // 인증 ID 생성
    private Long generateVerificationId() {
        return System.currentTimeMillis();
    }
}
