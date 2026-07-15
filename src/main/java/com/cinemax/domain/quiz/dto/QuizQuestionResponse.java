package com.cinemax.domain.quiz.dto;

import com.cinemax.global.enums.QuizQuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "퀴즈 문항 응답")
public class QuizQuestionResponse {

    @Schema(description = "문항 ID")
    private Long questionId;

    @Schema(description = "문항 유형 (MULTIPLE / OX)")
    private QuizQuestionType type;

    @Schema(description = "문항 순번")
    private Integer orderNo;

    @Schema(description = "문제 지문")
    private String content;

    @Schema(description = "정답 (OX: O/X, MULTIPLE: 정답 보기 순번)")
    private String answer;

    @Schema(description = "해설")
    private String explanation;

    @Schema(description = "배점")
    private Integer score;

    @Schema(description = "객관식 보기 목록 (OX는 빈 목록)")
    private List<QuizOptionResponse> options;
}
