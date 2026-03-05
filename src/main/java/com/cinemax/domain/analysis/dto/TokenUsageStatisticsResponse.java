package com.cinemax.domain.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 토큰 사용량 통계 응답 DTO (관리자 전용)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenUsageStatisticsResponse {

    // 전체 통계
    private Long totalAnalyses;
    private Long totalPromptTokens;
    private Long totalCompletionTokens;
    private Long totalTokens;

    // 평균 통계
    private Double averagePromptTokens;
    private Double averageCompletionTokens;
    private Double averageTokensPerAnalysis;

    // 모델별 통계
    private ModelTokenUsage modelTokenUsage;

    // 기간 정보
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    // 예상 비용 (참고용)
    private CostEstimate costEstimate;

}
