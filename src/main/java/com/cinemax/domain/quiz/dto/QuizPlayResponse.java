package com.cinemax.domain.quiz.dto;

import com.cinemax.global.enums.QuizQuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 학생 응시용 퀴즈 뷰 — 정답/해설을 제외한다.
 */
@Getter
@Builder
@Schema(description = "학생 응시용 퀴즈 (정답 숨김)")
public class QuizPlayResponse {

    @Schema(description = "퀴즈 ID")
    private Long quizId;

    @Schema(description = "반 ID")
    private Long classId;

    @Schema(description = "주차 번호")
    private Integer weekNo;

    @Schema(description = "퀴즈 제목")
    private String title;

    @Schema(description = "문항 목록 (정답 제외)")
    private List<Question> questions;

    @Getter
    @Builder
    @Schema(description = "응시용 문항")
    public static class Question {
        @Schema(description = "문항 ID")
        private Long questionId;

        @Schema(description = "문항 유형 (MULTIPLE / OX)")
        private QuizQuestionType type;

        @Schema(description = "문항 순번")
        private Integer orderNo;

        @Schema(description = "문제 지문")
        private String content;

        @Schema(description = "배점")
        private Integer score;

        @Schema(description = "객관식 보기 (정답 표시 없음, OX는 빈 목록)")
        private List<Option> options;
    }

    @Getter
    @Builder
    @Schema(description = "응시용 보기")
    public static class Option {
        @Schema(description = "보기 순번")
        private Integer orderNo;

        @Schema(description = "보기 내용")
        private String content;
    }
}
