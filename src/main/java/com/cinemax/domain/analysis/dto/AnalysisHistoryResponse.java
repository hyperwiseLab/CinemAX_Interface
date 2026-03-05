package com.cinemax.domain.analysis.dto;

import com.cinemax.domain.analysis.entity.AnalysisResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 분석 히스토리 응답 DTO (학생 복습용)
 * 요약 정보만 포함
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisHistoryResponse {

    private Long analysisId;

    private Long taskId;

    private String taskTitle; // Task 조인 시 추가

    private LocalDateTime requestedAt;

    private LocalDateTime analyzedAt;

    // 분석 결과 요약
    private Boolean requirementsMet;

    private Boolean codeQualityPass;

    private Boolean hasLogicError;

    private Boolean hasSecurityIssue;

    private Boolean needsImprovement;

    // LLM 응답 요약 (전체가 아닌 일부만)
    private String llmResponseSummary;

    private String modelVersion;

    private Integer totalTokens;

    // Entity -> DTO 변환
    public static AnalysisHistoryResponse from(AnalysisResult entity) {
        return AnalysisHistoryResponse.builder()
                .analysisId(entity.getAnalysisId())
                .taskId(entity.getTaskId())
                .taskTitle(entity.getTask() != null ? entity.getTask().getTaskTitle() : null)
                .requestedAt(entity.getRequestedAt())
                .analyzedAt(entity.getAnalyzedAt())
                .requirementsMet(entity.getRequirementsMet())
                .codeQualityPass(entity.getCodeQualityPass())
                .hasLogicError(entity.getHasLogicError())
                .hasSecurityIssue(entity.getHasSecurityIssue())
                .needsImprovement(entity.getNeedsImprovement())
                .llmResponseSummary(summarizeLlmResponse(entity.getLlmResponse()))
                .modelVersion(entity.getModelVersion())
                .totalTokens(entity.getTotalTokens())
                .build();
    }

    // LLM 응답 요약 (첫 200자만)
    private static String summarizeLlmResponse(String fullResponse) {
        if (fullResponse == null) {
            return null;
        }
        return fullResponse.length() > 200
                ? fullResponse.substring(0, 200) + "..."
                : fullResponse;
    }
}
