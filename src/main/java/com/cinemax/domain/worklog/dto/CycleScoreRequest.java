package com.cinemax.domain.worklog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사이클별 평가 점수 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CycleScoreRequest {

    @NotNull(message = "사이클 ID는 필수입니다.")
    private Long cycleId;

    @NotNull(message = "개념 이해도는 필수입니다.")
    @Min(value = 1, message = "개념 이해도는 1 이상이어야 합니다.")
    @Max(value = 5, message = "개념 이해도는 5 이하여야 합니다.")
    private Integer conceptScore;

    @NotNull(message = "코드 활용도는 필수입니다.")
    @Min(value = 1, message = "코드 활용도는 1 이상이어야 합니다.")
    @Max(value = 5, message = "코드 활용도는 5 이하여야 합니다.")
    private Integer applicationScore;

    // 화면 표시 순서. 없으면 서버가 목록 순서대로 부여한다.
    private Integer orderNo;
}
