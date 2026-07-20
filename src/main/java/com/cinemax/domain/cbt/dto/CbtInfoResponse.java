package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 학생 시작 화면용: 시험 구성 + 내 회차별 기록
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 시험 정보(학생)")
public class CbtInfoResponse {

    @Schema(description = "응시 가능 여부 (과목 설정 완료 + 배점 합계 100 + 은행 문항 충분)")
    private Boolean available;

    @Schema(description = "응시 불가 사유")
    private String unavailableReason;

    @Schema(description = "최종 합격 점수")
    private Double passScore;

    @Schema(description = "총 출제 문항 수")
    private Integer totalQuestionCount;

    @Schema(description = "과목 구성")
    private List<CbtSubjectResponse> subjects;

    @Schema(description = "내 회차별 기록 (최신순)")
    private List<CbtAttemptSummaryResponse> myAttempts;
}
