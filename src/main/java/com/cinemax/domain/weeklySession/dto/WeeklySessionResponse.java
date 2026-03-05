package com.cinemax.domain.weeklySession.dto;

import com.cinemax.global.enums.WeeklySessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 주차별 수업 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySessionResponse {

    private Long weeklySessionId;
    private Long inviteId;
    private Integer weekNo;
    private WeeklySessionStatus status;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private Boolean autoClosed;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
