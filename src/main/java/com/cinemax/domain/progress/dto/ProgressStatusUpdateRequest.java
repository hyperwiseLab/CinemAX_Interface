package com.cinemax.domain.progress.dto;

import com.cinemax.global.enums.StudentActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 진도 활동 상태 업데이트 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "진도 활동 상태 업데이트 요청")
public class ProgressStatusUpdateRequest {

    @NotNull(message = "활동 상태는 필수입니다.")
    @Schema(description = "활동 상태 (ACTIVE, NEED_HELP, IDLE, COMPLETED)",
            example = "ACTIVE",
            required = true,
            allowableValues = {"ACTIVE", "NEED_HELP", "IDLE", "COMPLETED"})
    private StudentActivityStatus activityStatus;
}
