package com.cinemax.domain.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelTokenUsage {
    private String modelVersion;
    private Long analysisCount;
    private Long totalTokens;
    private Double averageTokens;
}