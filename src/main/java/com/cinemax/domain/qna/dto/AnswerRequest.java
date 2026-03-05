package com.cinemax.domain.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "답변 생성/수정 요청")
public class AnswerRequest {

    @Schema(description = "답변 ID (수정시만 필요)", example = "1")
    private Long answerId;

    @NotNull(message = "질문 ID는 필수입니다")
    @Schema(description = "질문 ID", example = "1")
    private Long questionId;

    @NotBlank(message = "답변 내용은 필수입니다")
    @Size(max = 500, message = "답변 내용은 500자 이내여야 합니다")
    @Schema(description = "답변 내용", example = "int 변수는 'int a = 10;' 이런 형식으로 선언합니다.")
    private String content;
}
