package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 회차 요약 (기록 목록용, 문항 복기 제외)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 회차 요약")
public class CbtAttemptSummaryResponse {

    @Schema(description = "응시 ID")
    private Long attemptId;

    @Schema(description = "회차")
    private Integer roundNo;

    @Schema(description = "총점(100점 만점)")
    private Double totalScore;

    @Schema(description = "합격 여부")
    private Boolean passYn;

    @Schema(description = "제출 시간")
    private LocalDateTime submittedAt;

    @Schema(description = "과목별 점수")
    private List<CbtAttemptResultResponse.SubjectScore> subjectScores;
}
