package com.cinemax.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 비밀번호 재설정 완료 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 재설정 완료 응답 DTO")
public class PasswordResetCompleteResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "재설정 완료 시간", example = "2023-10-26T15:30:00")
    private LocalDateTime resetAt;

    @Schema(description = "메시지", example = "비밀번호가 성공적으로 재설정되었습니다.")
    private String message;

    @Schema(description = "다음 단계 안내", example = "새 비밀번호로 로그인해주세요.")
    private String nextStep;
}
