package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자용 문제 응답 (정답/해설 포함)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 문제 응답(관리자)")
public class CbtQuestionResponse {

    @Schema(description = "문제 ID")
    private Long questionId;

    @Schema(description = "반 ID")
    private Long classId;

    @Schema(description = "과목 ID")
    private Long subjectId;

    @Schema(description = "과목명")
    private String subjectNm;

    @Schema(description = "지문")
    private String content;

    @Schema(description = "해설")
    private String explanation;

    @Schema(description = "사용 여부")
    private Boolean useYn;

    @Schema(description = "등록 시간")
    private LocalDateTime createDt;

    @Schema(description = "보기 목록")
    private List<Option> options;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "보기")
    public static class Option {

        @Schema(description = "보기 ID")
        private Long optionId;

        @Schema(description = "보기 순서")
        private Integer orderNo;

        @Schema(description = "보기 내용")
        private String content;

        @Schema(description = "정답 여부")
        private Boolean correctYn;
    }
}
