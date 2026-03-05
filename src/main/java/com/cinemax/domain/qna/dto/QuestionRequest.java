package com.cinemax.domain.qna.dto;

import com.cinemax.global.enums.QuestionUrgency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "질문 생성/수정 요청")
public class QuestionRequest {

    @Schema(description = "질문 ID (수정시만 필요)", example = "1")
    private Long questionId;

    @NotNull(message = "클래스 ID는 필수입니다")
    @Schema(description = "클래스 ID", example = "1")
    private Long classId;

    @NotNull(message = "주차별 세션 ID는 필수입니다")
    @Schema(description = "주차별 세션 ID", example = "1")
    private Long weeklySessionId;

    @NotNull(message = "초대 ID는 필수입니다")
    @Schema(description = "초대 ID", example = "1")
    private Long inviteId;

    @NotNull(message = "사용자 ID는 필수입니다")
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @NotBlank(message = "질문 제목은 필수입니다")
    @Size(max = 200, message = "질문 제목은 200자 이내여야 합니다")
    @Schema(description = "질문 제목", example = "변수 선언 관련 질문입니다")
    private String title;

    @NotBlank(message = "질문 내용은 필수입니다")
    @Size(max = 500, message = "질문 내용은 500자 이내여야 합니다")
    @Schema(description = "질문 내용", example = "int형 변수를 어떻게 선언하나요?")
    private String content;

    @Size(max = 500, message = "태그는 500자 이내여야 합니다")
    @Schema(description = "태그 (쉼표로 구분)", example = "Java,변수,선언")
    private String tags;

    @Schema(description = "긴급도", example = "HIGH")
    private QuestionUrgency urgency;
}
