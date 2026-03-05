package com.cinemax.domain.codesnapshot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코드 엿보기 제재 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodePeekPenaltyRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "주차 세션 ID는 필수입니다.")
    private Long weeklySessionId;

    @NotNull(message = "초대 ID는 필수입니다.")
    private Long inviteId;
}
