package com.cinemax.domain.codesnapshot.dto;

import com.cinemax.domain.codesnapshot.entity.CodePeekPenalty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 감시 제재 실시간 메시지")
public class CodePeekPenaltyMessage {

    @Schema(description = "이벤트 타입", example = "INCREMENT|RESET|DELETE")
    private String eventType;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "주차 세션 ID")
    private Long weeklySessionId;

    @Schema(description = "초대 ID")
    private Long inviteId;

    @Schema(description = "감시 횟수")
    private Integer peekCount;

    @Schema(description = "제재 점수")
    private Integer penaltyPoints;

    @Schema(description = "마지막 감시 시각")
    private LocalDateTime lastPeekDt;

    @Schema(description = "메시지 전송 시각")
    private LocalDateTime timestamp;

    public static CodePeekPenaltyMessage from(CodePeekPenalty penalty, String eventType) {
        return CodePeekPenaltyMessage.builder()
                .eventType(eventType)
                .userId(penalty.getUserId())
                .userName(penalty.getUser() != null ? penalty.getUser().getName() : null)
                .weeklySessionId(penalty.getWeeklySessionId())
                .inviteId(penalty.getInviteId())
                .peekCount(penalty.getPeekCount())
                .penaltyPoints(penalty.getPenaltyPoints())
                .lastPeekDt(penalty.getLastPeekDt())
                .timestamp(LocalDateTime.now())
                .build();
    }
}


