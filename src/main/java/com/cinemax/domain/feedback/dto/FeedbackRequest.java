package com.cinemax.domain.feedback.dto;

import com.cinemax.global.enums.FeedbackType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 피드백 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    private Long feedbackId;

    @NotNull(message = "Cycle ID는 필수입니다.")
    private Long cycleId;

    @NotNull(message = "Task ID는 필수입니다.")
    private Long taskId;

    @NotNull(message = "피드백 타입은 필수입니다.")
    private FeedbackType feedbackType;

    @NotBlank(message = "캐릭터 이미지는 필수입니다.")
    private String characterImg;

    @NotBlank(message = "캐릭터 경로는 필수입니다.")
    private String characterPath;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "부제목은 필수입니다.")
    private String subTitle;

    @NotBlank(message = "피드백 내용은 필수입니다.")
    private String feedbackContent;
}
