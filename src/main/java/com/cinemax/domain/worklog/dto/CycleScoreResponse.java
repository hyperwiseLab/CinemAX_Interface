package com.cinemax.domain.worklog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사이클별 평가 점수 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CycleScoreResponse {

    private Long cycleId;
    private Integer conceptScore;
    private Integer applicationScore;
    private Integer orderNo;
}
