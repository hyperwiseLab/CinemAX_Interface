package com.cinemax.infrastructure.gemini.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 구조화된 AI 분석 응답 DTO
 * Gemini에서 JSON 형식으로 응답받을 때 사용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructuredAnalysisResponse {

    // 요구사항 충족 여부
    @JsonProperty("requirementsMet")
    private Boolean requirementsMet;

    // 코드 품질 통과 여부
    @JsonProperty("codeQualityPass")
    private Boolean codeQualityPass;

    // 논리 오류 존재 여부
    @JsonProperty("hasLogicError")
    private Boolean hasLogicError;

    // 보안 이슈 존재 여부
    @JsonProperty("hasSecurityIssue")
    private Boolean hasSecurityIssue;

    // 개선 필요 여부
    @JsonProperty("needsImprovement")
    private Boolean needsImprovement;

    // 상세 피드백 텍스트
    @JsonProperty("feedback")
    private String feedback;

    // 잘한 점
    @JsonProperty("strengths")
    private String strengths;

    // 개선이 필요한 점
    @JsonProperty("improvements")
    private String improvements;

    // 학습 조언
    @JsonProperty("advice")
    private String advice;
}
