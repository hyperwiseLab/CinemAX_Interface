package com.cinemax.domain.auth.dto;

import com.cinemax.global.enums.AuthEventType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * 인증 로그 생성 요청 DTO
 */
@Getter
@Builder
public class AuthLogRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotNull(message = "이벤트 타입은 필수입니다")
    private AuthEventType eventType;

    private Long userId;
}

