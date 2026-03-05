package com.cinemax.domain.analysis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 분석 결과 저장 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotNull(message = "과제 ID는 필수입니다")
    private Long taskId;

    private Long submitId;

    private Long classId;

    private Long cycleId;

    private String studentPrompt;

    @NotBlank(message = "학생 코드는 필수입니다")
    private String studentCode;

    @NotBlank(message = "LLM 응답은 필수입니다")
    private String llmResponse;

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
}
