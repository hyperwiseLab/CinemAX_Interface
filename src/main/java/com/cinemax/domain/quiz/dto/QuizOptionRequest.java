package com.cinemax.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "퀴즈 객관식 보기 요청")
public class QuizOptionRequest {

    @NotNull
    @Schema(description = "보기 순번 (1~4)")
    private Integer orderNo;

    @NotBlank
    @Schema(description = "보기 내용")
    private String content;

    @NotNull
    @Schema(description = "정답 여부")
    private Boolean isCorrect;
}
