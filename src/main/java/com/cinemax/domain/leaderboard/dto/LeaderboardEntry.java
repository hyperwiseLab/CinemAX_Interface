package com.cinemax.domain.leaderboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Leaderboard 항목 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리더보드 항목")
public class LeaderboardEntry {

    @Schema(description = "순위", example = "1")
    private Integer rank;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "학번", example = "2024001")
    private String studentNum;

    @Schema(description = "총 점수", example = "950.50")
    private BigDecimal totalScore;

    @Schema(description = "제출 횟수", example = "15")
    private Long submissionCount;

    @Schema(description = "평균 진도율", example = "85.50")
    private BigDecimal averageProgress;

    @Schema(description = "완료한 과제 수", example = "12")
    private Long completedTasks;
}
