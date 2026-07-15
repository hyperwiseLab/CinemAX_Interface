package com.cinemax.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "퀴즈 객관식 보기 응답")
public class QuizOptionResponse {

    @Schema(description = "보기 ID")
    private Long optionId;

    @Schema(description = "보기 순번 (1~4)")
    private Integer orderNo;

    @Schema(description = "보기 내용")
    private String content;

    @Schema(description = "정답 여부")
    private Boolean isCorrect;
}
