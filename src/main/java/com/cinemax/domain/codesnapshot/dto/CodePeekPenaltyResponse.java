package com.cinemax.domain.codesnapshot.dto;

import com.cinemax.domain.codesnapshot.entity.CodePeekPenalty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 코드 엿보기 제재 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodePeekPenaltyResponse {

    private Long userId;
    private String userName;
    private Long weeklySessionId;
    private Long inviteId;
    private Integer peekCount;
    private Integer penaltyPoints;
    private LocalDateTime lastPeekDt;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    /**
     * Entity -> DTO 변환
     */
    public static CodePeekPenaltyResponse from(CodePeekPenalty penalty) {
        return CodePeekPenaltyResponse.builder()
                .userId(penalty.getUserId())
                .userName(penalty.getUser() != null ? penalty.getUser().getName() : null)
                .weeklySessionId(penalty.getWeeklySessionId())
                .inviteId(penalty.getInviteId())
                .peekCount(penalty.getPeekCount())
                .penaltyPoints(penalty.getPenaltyPoints())
                .lastPeekDt(penalty.getLastPeekDt())
                .createDt(penalty.getCreateDt())
                .updateDt(penalty.getUpdateDt())
                .build();
    }
}
