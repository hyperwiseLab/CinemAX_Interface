package com.cinemax.domain.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 분석 통계 응답 DTO (교수용)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisStatisticsResponse {

    private Long totalAnalyses;

    private Double requirementsMetRate;

    private Double codeQualityPassRate;

    private Double logicErrorRate;

    private Double securityIssueRate;

    private List<IssueCount> topIssues;
}
