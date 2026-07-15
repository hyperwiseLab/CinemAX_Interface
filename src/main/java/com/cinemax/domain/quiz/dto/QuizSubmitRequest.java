package com.cinemax.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "퀴즈 응시 답안 제출 요청")
public class QuizSubmitRequest {

    @Valid
    @NotEmpty
    @Schema(description = "문항별 답안 목록")
    private List<Answer> answers;

    @Getter
    @NoArgsConstructor
    @Schema(description = "문항 답안")
    public static class Answer {

        @NotNull
        @Schema(description = "문항 ID")
        private Long questionId;

        @NotNull
        @Schema(description = "제출 답 (OX: O/X, MULTIPLE: 선택한 보기 순번)")
        private String selected;
    }
}
