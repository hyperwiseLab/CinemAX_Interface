package com.cinemax.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 확인 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPasswordRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
