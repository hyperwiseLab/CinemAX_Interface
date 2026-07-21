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
    // 초대(classInvite) 경유 반 정보 - 세션 목록에서 반 조회 N+1 제거용
    private Long classId;
    private String classNm;
    private WeeklySessionStatus status;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private Boolean autoClosed;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
