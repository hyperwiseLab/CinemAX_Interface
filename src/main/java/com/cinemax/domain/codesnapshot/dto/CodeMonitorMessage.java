package com.cinemax.domain.codesnapshot.dto;

import com.cinemax.domain.codesnapshot.entity.CodeSnapshot;
import com.cinemax.global.enums.EventType;
import com.cinemax.global.enums.LanguageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WebSocket을 통한 실시간 코드 모니터링 메시지 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "실시간 코드 모니터링 메시지")
public class CodeMonitorMessage {

    @Schema(description = "이벤트 타입", example = "CODE_SAVED")
    private EventType eventType;

    @Schema(description = "코드 스냅샷 ID", example = "1")
    private Long codeId;

    @Schema(description = "과제 ID", example = "1")
    private Long taskId;

    @Schema(description = "주차 수업 ID", example = "1")
    private Long weeklySessionId;

    @Schema(description = "학생 ID", example = "1")
    private Long userId;

    @Schema(description = "학생 이름")
    private String userName;

    @Schema(description = "학생 이메일")
    private String userEmail;

    @Schema(description = "사이클 ID", example = "1")
    private Long cycleId;

    @Schema(description = "프로그래밍 언어", example = "JAVA")
    private LanguageType lang;

    @Schema(description = "코드 내용")
    private String content;

    @Schema(description = "저장 시각")
    private LocalDateTime saveAt;

    @Schema(description = "메시지 전송 시각")
    private LocalDateTime timestamp;

    // CodeSnapshot Entity로부터 메시지 생성
    public static CodeMonitorMessage from(CodeSnapshot codeSnapshot, String userName, String userEmail, EventType eventType) {
        return CodeMonitorMessage.builder()
                .eventType(eventType)
                .codeId(codeSnapshot.getCodeId())
                .taskId(codeSnapshot.getTaskId())
                .weeklySessionId(codeSnapshot.getWeeklySessionId())
                .userId(codeSnapshot.getUserId())
                .userName(userName)
                .userEmail(userEmail)
                .cycleId(null)
                .lang(codeSnapshot.getLang())
                .content(codeSnapshot.getContent())
                .saveAt(codeSnapshot.getSaveAt())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
