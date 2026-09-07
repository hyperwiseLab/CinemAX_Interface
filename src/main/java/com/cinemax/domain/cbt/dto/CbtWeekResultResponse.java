package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주차별 CBT 채점 결과. 과목 배점/과락이 아닌 정답률(%) 기준으로 판정한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주차별 CBT 결과")
public class CbtWeekResultResponse {

    @Schema(description = "응시 기록 ID")
    private Long attemptId;

    @Schema(description = "주차 번호")
    private Integer weekNo;

    @Schema(description = "해당 주차 응시 회차")
    private Integer roundNo;

    @Schema(description = "맞힌 문항 수")
    private Integer correctCount;

    @Schema(description = "총 문항 수")
    private Integer totalCount;

    @Schema(description = "정답률(%)")
    private Double score;

    @Schema(description = "합격 기준 정답률(%)")
    private Double passScore;

    @Schema(description = "합격 여부")
    private Boolean passYn;

    @Schema(description = "제출 시각")
    private LocalDateTime submittedAt;

    @Schema(description = "문항별 정오답/해설")
    private List<CbtAttemptResultResponse.Item> items;
}
