package com.cinemax.domain.weeklySession.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주차별 수업 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySessionRequest {

    @NotNull(message = "초대 ID는 필수입니다")
    private Long inviteId;

    @NotNull(message = "주차 번호는 필수입니다")
    @Positive(message = "주차 번호는 양수여야 합니다")
    private Integer weekNo;
}
