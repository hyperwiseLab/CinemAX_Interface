package com.cinemax.domain.quiz.dto;

import com.cinemax.global.enums.QuizQuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "퀴즈 문항 요청 (전체 수정 시 사용)")
public class QuizQuestionRequest {

    @NotNull
    @Schema(description = "문항 유형 (MULTIPLE / OX)")
    private QuizQuestionType type;

    @NotNull
    @Schema(description = "문항 순번")
    private Integer orderNo;

    @NotBlank
    @Schema(description = "문제 지문")
    private String content;

    @NotBlank
    @Schema(description = "정답 (OX: O/X, MULTIPLE: 정답 보기 순번)")
    private String answer;

    @Schema(description = "해설")
    private String explanation;

    @NotNull
    @Schema(description = "배점")
    private Integer score;

    @Valid
    @Schema(description = "객관식 보기 목록 (OX는 생략/빈 목록)")
    private List<QuizOptionRequest> options;
}
