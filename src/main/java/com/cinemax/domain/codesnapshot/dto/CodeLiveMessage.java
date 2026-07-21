package com.cinemax.domain.codesnapshot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 실시간 코드 타이핑 릴레이 메시지.
 * 학생 에디터가 /app/code-live/{weeklySessionId} 로 보내면
 * 서버가 DB 저장 없이 /topic/code-live/{weeklySessionId}/{userId} 로 그대로 중계한다.
 * (영속화는 기존 10초 자동저장이 담당 - 이 채널은 교수 코드보기 실시간용)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "실시간 코드 릴레이 메시지")
public class CodeLiveMessage {

    @Schema(description = "학생 ID")
    private Long userId;

    @Schema(description = "학생 이름")
    private String userName;

    @Schema(description = "과제 ID")
    private Long taskId;

    @Schema(description = "사이클 ID")
    private Long cycleId;

    @Schema(description = "코드 내용")
    private String content;

    @Schema(description = "타이핑 시각(클라이언트 기준, ISO)")
    private String typedAt;
}
