package com.cinemax.domain.feedback.dto;

import com.cinemax.domain.feedback.entity.Feedback;
import com.cinemax.global.enums.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 피드백 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    private Long feedbackId;
    private Long cycleId;
    private Long taskId;
    private FeedbackType feedbackType;
    private String characterImg;
    private String characterPath;
    private String title;
    private String subTitle;
    private String feedbackContent;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    // Entity to DTO
    public static FeedbackResponse from(Feedback feedback) {
        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .cycleId(feedback.getCycleId())
                .taskId(feedback.getTaskId())
                .feedbackType(feedback.getFeedbackType())
                .characterImg(feedback.getCharacterImg())
                .characterPath(feedback.getCharacterPath())
                .title(feedback.getTitle())
                .subTitle(feedback.getSubTitle())
                .feedbackContent(feedback.getFeedbackContent())
                .createDt(feedback.getCreateDt())
                .updateDt(feedback.getUpdateDt())
                .build();
    }
}
