package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "반의 CBT 과목 설정 응답")
public class CbtSubjectsResponse {

    @Schema(description = "설정 존재 여부")
    private Boolean configured;

    @Schema(description = "최종 합격 점수")
    private Double passScore;

    @Schema(description = "활성 과목 배점 합계(100이어야 응시 가능)")
    private Double totalMaxScore;

    @Schema(description = "과목 목록")
    private List<CbtSubjectResponse> subjects;
}
