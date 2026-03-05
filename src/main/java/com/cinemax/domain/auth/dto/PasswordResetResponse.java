package com.cinemax.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 비밀번호 재설정 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 재설정 응답 DTO")
public class PasswordResetResponse {

    @Schema(description = "요청 ID", example = "req_123456")
    private String requestId;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "재설정 방식", example = "EMAIL")
    private String resetMethod;

    @Schema(description = "인증 코드 만료 시간", example = "2023-10-26T15:30:00")
    private LocalDateTime expiresAt;

    @Schema(description = "메시지", example = "비밀번호 재설정 이메일이 발송되었습니다.")
    private String message;

    @Schema(description = "다음 단계 안내", example = "이메일을 확인하여 인증 코드를 입력해주세요.")
    private String nextStep;
}
