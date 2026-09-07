package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 문제 은행 JSON 대량 등록 요청.
 * 문항별 검증(보기 2개 이상, 정답 정확히 1개)은 서비스에서 수행하고 실패 문항은 리포트로 반환한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 문제 대량 등록 요청")
public class CbtQuestionBulkRequest {

    @NotNull
    @Schema(description = "반 ID")
    private Long classId;

    @NotEmpty
    @Valid
    @Schema(description = "문제 목록")
    private List<QuestionItem> questions;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "등록 문제 항목")
    public static class QuestionItem {

        @NotBlank
        @Size(max = 100)
        @Schema(description = "과목명(미등록 과목이면 자동 생성)", example = "파이썬 기초")
        private String subject;

        @Min(1)
        @Schema(description = "커리큘럼 주차(1~N). 생략 시 주차 미지정 - 전체 모의고사에만 출제됨", example = "3")
        private Integer weekNo;

        @NotBlank
        @Schema(description = "지문")
        private String content;

        @Size(max = 1000)
        @Schema(description = "해설")
        private String explanation;

        @NotEmpty
        @Valid
        @Schema(description = "보기 목록")
        private List<OptionItem> options;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "보기 항목")
    public static class OptionItem {

        @NotNull
        @Schema(description = "보기 순서(1~4)")
        private Integer orderNo;

        @NotBlank
        @Size(max = 1000)
        @Schema(description = "보기 내용")
        private String content;

        @NotNull
        @Schema(description = "정답 여부")
        private Boolean correct;
    }
}
