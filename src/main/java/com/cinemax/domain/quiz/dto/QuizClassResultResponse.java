package com.cinemax.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "퀴즈 반 전체 결과 집계 (관리자)")
public class QuizClassResultResponse {

    @Schema(description = "퀴즈 ID")
    private Long quizId;

    @Schema(description = "만점")
    private Integer maxScore;

    @Schema(description = "제출 인원 수")
    private Integer submittedCount;

    @Schema(description = "반 수강 인원 수 (분모)")
    private Integer totalStudents;

    @Schema(description = "평균 점수")
    private Double averageScore;

    @Schema(description = "학생별 순위 목록 (점수 내림차순)")
    private List<Ranking> rankings;

    @Getter
    @Builder
    @Schema(description = "학생 순위 항목")
    public static class Ranking {
        @Schema(description = "순위")
        private Integer rank;

        @Schema(description = "학생 userId")
        private Long userId;

        @Schema(description = "학생 이름")
        private String userName;

        @Schema(description = "획득 점수")
        private Integer totalScore;
    }
}
