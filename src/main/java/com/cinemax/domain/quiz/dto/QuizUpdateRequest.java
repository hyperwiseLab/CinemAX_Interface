package com.cinemax.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "퀴즈 전체 수정 요청 (문항/보기/정답/해설 전체 교체)")
public class QuizUpdateRequest {

    @NotBlank
    @Schema(description = "퀴즈 제목")
    private String title;

    @Valid
    @NotNull
    @Schema(description = "문항 목록 (통째로 교체됨)")
    private List<QuizQuestionRequest> questions;
}
