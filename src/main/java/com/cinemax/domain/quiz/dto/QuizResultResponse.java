package com.cinemax.domain.quiz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@Schema(description = "퀴즈 채점 결과 (본인)")
public class QuizResultResponse {

    @Schema(description = "퀴즈 ID")
    private Long quizId;

    @Schema(description = "획득 총점")
    private Integer totalScore;

    @Schema(description = "만점")
    private Integer maxScore;

    @Schema(description = "정답 문항 수")
    private Integer correctCount;

    @Schema(description = "전체 문항 수")
    private Integer questionCount;

    @Schema(description = "이번이 최초 제출인지 (false면 연습 응시, 성적 미반영)")
    private Boolean firstAttempt;

    @Schema(description = "문항별 채점 결과")
    private List<Item> items;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "문항별 채점 상세")
    public static class Item {
        @Schema(description = "문항 ID")
        private Long questionId;

        @Schema(description = "제출 답")
        private String selected;

        @Schema(description = "정답")
        private String answer;

        @Schema(description = "정답 여부")
        private Boolean correct;

        @Schema(description = "해설")
        private String explanation;
    }
}
