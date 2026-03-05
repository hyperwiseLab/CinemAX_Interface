package com.cinemax.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이메일 인증 상태 응답 DTO")
public class EmailVerificationStatusResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "이메일 주소", example = "test@example.com")
    private String email;

    @Schema(description = "인증 완료 여부", example = "true")
    private Boolean isVerified;

    @Schema(description = "인증 시도 횟수", example = "2")
    private Integer attemptCount;

    @Schema(description = "남은 시도 횟수", example = "3")
    private Integer remainingAttempts;

    @Schema(description = "메시지", example = "이메일 인증이 완료되었습니다.")
    private String message;
}
