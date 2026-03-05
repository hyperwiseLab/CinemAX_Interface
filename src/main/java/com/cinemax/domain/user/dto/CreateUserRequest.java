package com.cinemax.domain.user.dto;

import com.cinemax.global.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자용 사용자 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자용 사용자 생성 요청")
public class CreateUserRequest {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Schema(description = "이메일", example = "user@example.com", required = true)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "비밀번호", example = "password123", required = true)
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    @Schema(description = "이름", example = "홍길동", required = true)
    private String name;

    @Schema(description = "학번", example = "20240001")
    private String studentId;

    @NotNull(message = "역할은 필수입니다")
    @Schema(description = "사용자 역할", example = "STUDENT", required = true)
    private RoleType role;

    @Schema(description = "활성화 여부", example = "true")
    @Builder.Default
    private Boolean isActive = true;
}
