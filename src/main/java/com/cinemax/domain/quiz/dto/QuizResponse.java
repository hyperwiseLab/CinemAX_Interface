package com.cinemax.domain.quiz.dto;

import com.cinemax.global.enums.QuizStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "퀴즈 응답")
public class QuizResponse {

    @Schema(description = "퀴즈 ID")
    private Long quizId;

    @Schema(description = "반 ID")
    private Long classId;

    @Schema(description = "주차 번호")
    private Integer weekNo;

    @Schema(description = "주차 세션 ID")
    private Long weeklySessionId;

    @Schema(description = "퀴즈 제목")
    private String title;

    @Schema(description = "상태 (DRAFT / REVIEW / PUBLISHED)")
    private QuizStatus status;

    @Schema(description = "생성자 userId")
    private Long createdBy;

    @Schema(description = "문항 목록")
    private List<QuizQuestionResponse> questions;
}
