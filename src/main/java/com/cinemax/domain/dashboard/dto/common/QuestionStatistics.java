package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 질문 통계
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStatistics {
    private Long totalQuestions;            // 전체 질문 수
    private Long answeredQuestions;         // 답변된 질문 수
    private Long unansweredQuestions;       // 미답변 질문 수
    private Long highUrgencyQuestions;      // 긴급 질문 수
    private Double responseRate;            // 답변률 (%)
}