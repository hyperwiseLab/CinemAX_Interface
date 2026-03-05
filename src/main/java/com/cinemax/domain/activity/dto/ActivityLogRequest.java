package com.cinemax.domain.activity.dto;

import com.cinemax.global.enums.ActivityAction;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 활동 로그 기록 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogRequest {

    @NotNull(message = "주차별 수업 ID는 필수입니다.")
    private Long weeklySessionId;

    @NotNull(message = "초대 ID는 필수입니다.")
    private Long inviteId;

    private Long taskId;

    @NotNull(message = "활동 액션은 필수입니다.")
    private ActivityAction action;
}
