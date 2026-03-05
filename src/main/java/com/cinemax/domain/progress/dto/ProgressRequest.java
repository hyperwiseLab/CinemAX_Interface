package com.cinemax.domain.progress.dto;

import com.cinemax.global.enums.TaskMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 진도 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "진도 생성/수정 요청")
public class ProgressRequest {

    @NotNull(message = "수업 ID는 필수입니다.")
    @Schema(description = "수업 ID", example = "1", required = true)
    private Long classId;

    @NotNull(message = "주차별 수업 ID는 필수입니다.")
    @Schema(description = "주차별 수업 ID", example = "1", required = true)
    private Long weeklySessionId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    @Schema(description = "사용자 ID", example = "1", required = true)
    private Long userId;

    @NotNull(message = "진도율은 필수입니다.")
    @DecimalMin(value = "0.00", message = "진도율은 0 이상이어야 합니다.")
    @DecimalMax(value = "100.00", message = "진도율은 100 이하여야 합니다.")
    @Schema(description = "진도율 (%)", example = "75.50", required = true)
    private BigDecimal progressPct;

    @Schema(description = "학습 반복 횟수", example = "1")
    private Integer cycleCount;

    @Schema(description = "문제 모드 (ADVANCE, EASY)", example = "ADVANCE")
    private TaskMode mode;
}
