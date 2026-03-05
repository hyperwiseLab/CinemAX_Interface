package com.cinemax.domain.syntax.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SyntaxDetail 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문법 상세 정보 요청")
public class SyntaxDetailRequest {

    @NotBlank(message = "문법 제목은 필수입니다")
    @JsonProperty("syntax_title")
    @Schema(description = "문법 제목", example = "산술 연산자")
    private String syntaxTitle;

    @JsonProperty("syntax_comment")
    @Schema(description = "문법 설명", example = "숫자를 계산하는 기호입니다.")
    private String syntaxComment;

    @JsonProperty("syntax_code")
    @Schema(description = "예제 코드", example = "result = 10 + 5")
    private String syntaxCode;
}
