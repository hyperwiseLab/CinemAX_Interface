package com.cinemax.domain.worklog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Week별 피드백 조회 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyFeedbackResponse {

    private Integer weekNo;
    private List<FeedbackDetail> feedbacks;

    /**
     * 개별 피드백 상세
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackDetail {
        private Long workLogId;
        private LocalDate logDate;
        private String meaningfulContent;    // 가장 의미있었던 내용
        private String difficultContent;     // 제일 어려웠던 내용
        private String questionContent;      // 궁금한 점
    }
}
