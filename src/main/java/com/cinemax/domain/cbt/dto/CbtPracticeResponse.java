package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 학생 응시용 문제 세트 (정답/해설 제외, 과목별 랜덤 추출)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 응시 문제 세트(정답 숨김)")
public class CbtPracticeResponse {

    @Schema(description = "총 문항 수")
    private Integer totalCount;

    @Schema(description = "주차 번호(주차별 CBT 인 경우, 전체 모의고사면 null)")
    private Integer weekNo;

    @Schema(description = "요청한 출제 수보다 보유 문항이 적어 축소 출제된 경우 true")
    private Boolean reduced;

    @Schema(description = "문제 목록(과목 순서대로)")
    private List<Question> questions;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "응시 문항")
    public static class Question {

        @Schema(description = "문제 ID")
        private Long questionId;

        @Schema(description = "과목 ID")
        private Long subjectId;

        @Schema(description = "과목명")
        private String subjectNm;

        @Schema(description = "출제 순번(1부터)")
        private Integer orderNo;

        @Schema(description = "지문")
        private String content;

        @Schema(description = "보기 목록")
        private List<Option> options;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "응시 보기(정답 여부 미포함)")
    public static class Option {

        @Schema(description = "보기 순서")
        private Integer orderNo;

        @Schema(description = "보기 내용")
        private String content;
    }
}
