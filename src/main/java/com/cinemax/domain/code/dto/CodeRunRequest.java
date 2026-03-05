package com.cinemax.domain.code.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코드 실행 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 실행 요청")
public class CodeRunRequest {

    @NotBlank(message = "언어는 필수입니다")
    @Schema(description = "프로그래밍 언어", example = "java")
    private String language;

    @NotBlank(message = "코드는 필수입니다")
    @Schema(description = "실행할 코드")
    private String code;

    @Schema(description = "표준 입력 (stdin)", example = "5")
    private String stdin;

    @Schema(description = "명령줄 인자", example = "[\"arg1\", \"arg2\"]")
    private String[] args;

    @Schema(description = "타임아웃 (밀리초)", example = "3000")
    private Integer timeout;
}
