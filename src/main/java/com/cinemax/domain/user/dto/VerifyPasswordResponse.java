package com.cinemax.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 확인 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 확인 응답")
public class VerifyPasswordResponse {

    @Schema(description = "비밀번호 일치 여부")
    private Boolean isValid;

    @Schema(description = "메시지")
    private String message;

    public static VerifyPasswordResponse of(Boolean isValid) {
        return VerifyPasswordResponse.builder()
                .isValid(isValid)
                .message(isValid ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다.")
                .build();
    }
}
