package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 과목 응답")
public class CbtSubjectResponse {

    @Schema(description = "과목 ID")
    private Long subjectId;

    @Schema(description = "과목명")
    private String subjectNm;

    @Schema(description = "표시 순서")
    private Integer orderNo;

    @Schema(description = "문제당 점수")
    private Double pointPerQuestion;

    @Schema(description = "회차당 출제 문항 수")
    private Integer questionCount;

    @Schema(description = "과목 배점(문제당 점수 x 출제수)")
    private Double maxScore;

    @Schema(description = "과락 기준점")
    private Double cutScore;

    @Schema(description = "과락 적용 여부")
    private Boolean cutYn;

    @Schema(description = "사용 여부")
    private Boolean useYn;

    @Schema(description = "문제 은행 보유 문항 수(활성)")
    private Long bankCount;
}
