package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 질문/답변 분석
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnAAnalysis {
    private Long totalQuestions;            // 총 질문 수
    private Long answeredQuestions;         // 답변 받은 질문 수
    private Long unansweredQuestions;       // 미답변 질문 수
    private Long highUrgencyQuestions;      // 긴급 질문 수
    private Double responseRate;            // 답변률 (%)
}