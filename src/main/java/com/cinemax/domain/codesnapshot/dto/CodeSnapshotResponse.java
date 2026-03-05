package com.cinemax.domain.codesnapshot.dto;

import com.cinemax.domain.codesnapshot.entity.CodeSnapshot;
import com.cinemax.global.enums.LanguageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 코드 스냅샷 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 스냅샷 응답")
public class CodeSnapshotResponse {

    @Schema(description = "코드 스냅샷 ID", example = "1")
    private Long codeId;

    @Schema(description = "과제 ID", example = "1")
    private Long taskId;

    @Schema(description = "주차 수업 ID", example = "1")
    private Long weeklySessionId;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사이클 ID", example = "1")
    private Long cycleId;

    @Schema(description = "프로그래밍 언어", example = "JAVA")
    private LanguageType lang;

    @Schema(description = "코드 내용")
    private String content;

    @Schema(description = "저장 시각")
    private LocalDateTime saveAt;

    @Schema(description = "생성 시각")
    private LocalDateTime createDt;

    @Schema(description = "수정 시각")
    private LocalDateTime updateDt;

    // Entity -> Response DTO 변환
    public static CodeSnapshotResponse from(CodeSnapshot codeSnapshot) {
        return CodeSnapshotResponse.builder()
                .codeId(codeSnapshot.getCodeId())
                .taskId(codeSnapshot.getTaskId())
                .weeklySessionId(codeSnapshot.getWeeklySessionId())
                .userId(codeSnapshot.getUserId())
                .cycleId(codeSnapshot.getCycleId())
                .lang(codeSnapshot.getLang())
                .content(codeSnapshot.getContent())
                .saveAt(codeSnapshot.getSaveAt())
                .createDt(codeSnapshot.getCreateDt())
                .updateDt(codeSnapshot.getUpdateDt())
                .build();
    }
}
