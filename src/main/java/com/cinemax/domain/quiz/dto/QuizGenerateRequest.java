package com.cinemax.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "주차별 퀴즈 AI 생성 요청 (시나리오는 백엔드가 aggregate)")
public class QuizGenerateRequest {

    @NotNull
    @Schema(description = "반 ID")
    private Long classId;

    @Valid
    @NotEmpty
    @Schema(description = "생성할 주차별 설정 목록")
    private List<WeekSpec> weeks;

    @Getter
    @NoArgsConstructor
    @Schema(description = "주차별 문제 개수 설정")
    public static class WeekSpec {

        @NotNull
        @Schema(description = "주차 번호")
        private Integer weekNo;

        @NotNull
        @PositiveOrZero
        @Schema(description = "객관식 문항 수")
        private Integer multipleCount;

        @NotNull
        @PositiveOrZero
        @Schema(description = "O/X 문항 수")
        private Integer oxCount;
    }
}
