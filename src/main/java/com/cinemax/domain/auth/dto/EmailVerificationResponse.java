package com.cinemax.domain.auth.dto;

import com.cinemax.domain.auth.entity.EmailVerification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "이메일 인증 응답 DTO")
public class EmailVerificationResponse {

    @Schema(description = "인증 ID", example = "1")
    private Long verificationId;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "이메일 주소", example = "test@example.com")
    private String email;

    @Schema(description = "인증 코드", example = "123456")
    private String code;

    @Schema(description = "만료 시간", example = "2023-10-26T10:30:00")
    private LocalDateTime expiresAt;

    @Schema(description = "시도 횟수", example = "2")
    private Byte attemptCount;

    @Schema(description = "인증 완료 시간", example = "2023-10-26T10:25:00")
    private LocalDateTime verifiedAt;

    @Schema(description = "생성 시간", example = "2023-10-26T10:00:00")
    private LocalDateTime createDt;

    @Schema(description = "인증 완료 여부", example = "true")
    private Boolean isVerified;

    @Schema(description = "만료 여부", example = "false")
    private Boolean isExpired;

    public static EmailVerificationResponse from(EmailVerification emailVerification) {
        return EmailVerificationResponse.builder()
                .verificationId(emailVerification.getVerificationId())
                .userId(emailVerification.getUserId())
                .email(emailVerification.getEmail())
                .code(emailVerification.getCode())
                .expiresAt(emailVerification.getExpiresAt())
                .attemptCount(emailVerification.getAttemptCount())
                .verifiedAt(emailVerification.getVerifiedAt())
                .createDt(emailVerification.getCreateDt())
                .isVerified(emailVerification.isVerified())
                .isExpired(emailVerification.isExpired())
                .build();
    }
}
