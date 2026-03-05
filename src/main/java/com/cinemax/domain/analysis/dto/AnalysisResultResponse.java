package com.cinemax.domain.analysis.dto;

import com.cinemax.domain.analysis.entity.AnalysisResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 분석 결과 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultResponse {

    private Long analysisId;

    private Long userId;

    private Long taskId;

    private Long submitId;

    private Long classId;

    private Long cycleId;

    private String studentPrompt;

    private String studentCode;

    private String llmResponse;

    private LocalDateTime requestedAt;

    private LocalDateTime analyzedAt;

    // 분석 결과 플래그
    private Boolean requirementsMet;

    private Boolean codeQualityPass;

    private Boolean hasLogicError;

    private Boolean hasSecurityIssue;

    private Boolean needsImprovement;

    // 메타 정보
    private String modelVersion;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;

    private String additionalNotes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Entity -> DTO 변환
    public static AnalysisResultResponse from(AnalysisResult entity) {
        return AnalysisResultResponse.builder()
                .analysisId(entity.getAnalysisId())
                .userId(entity.getUserId())
                .taskId(entity.getTaskId())
                .submitId(entity.getSubmitId())
                .classId(entity.getClassId())
                .cycleId(entity.getCycleId())
                .studentPrompt(entity.getStudentPrompt())
                .studentCode(entity.getStudentCode())
                .llmResponse(entity.getLlmResponse())
                .requestedAt(entity.getRequestedAt())
                .analyzedAt(entity.getAnalyzedAt())
                .requirementsMet(entity.getRequirementsMet())
                .codeQualityPass(entity.getCodeQualityPass())
                .hasLogicError(entity.getHasLogicError())
                .hasSecurityIssue(entity.getHasSecurityIssue())
                .needsImprovement(entity.getNeedsImprovement())
                .modelVersion(entity.getModelVersion())
                .promptTokens(entity.getPromptTokens())
                .completionTokens(entity.getCompletionTokens())
                .totalTokens(entity.getTotalTokens())
                .additionalNotes(entity.getAdditionalNotes())
                .createdAt(entity.getCreateDt())
                .updatedAt(entity.getUpdateDt())
                .build();
    }
}
