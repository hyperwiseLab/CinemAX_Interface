package com.cinemax.domain.task.dto;

import com.cinemax.global.enums.TaskMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Task 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Task 응답")
public class TaskResponse {

    @Schema(description = "Task ID", example = "1")
    private Long taskId;

    @Schema(description = "Cycle ID", example = "1")
    private Long cycleId;

    @Schema(description = "Task 제목", example = "첫 번째 프로그래밍 과제")
    private String taskTitle;

    @Schema(description = "Task 모드 (ADVANCE, EASY)", example = "EASY")
    private TaskMode taskMode;

    @Schema(description = "Task To do Code")
    private String startCode;

    @Schema(description = "컴파일 설정 JSON", example = "{\"timeout\": 3000}")
    private String configJson;

    @Schema(description = "주차 내 표시 순서", example = "1")
    private Integer orderNo;

    @Schema(description = "생성 날짜")
    private LocalDateTime createDt;

    @Schema(description = "수정 날짜")
    private LocalDateTime updateDt;
}
