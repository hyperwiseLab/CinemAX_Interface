package com.cinemax.domain.worklog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 교수 피드백 요청 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "교수 피드백 요청")
public class ProfessorFeedbackRequest {

    @NotBlank(message = "피드백 내용을 입력해주세요.")
    @Schema(description = "피드백 내용", example = "업무일지를 잘 작성하셨습니다. 학습 내용을 구체적으로 정리하여 좋았습니다.")
    private String feedback;

    @Min(value = 1, message = "피드백 점수는 최소 1점입니다.")
    @Max(value = 5, message = "피드백 점수는 최대 5점입니다.")
    @Schema(description = "피드백 점수 (1-5점)", example = "4")
    private Double score;
}
