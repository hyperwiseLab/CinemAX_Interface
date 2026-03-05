package com.cinemax.domain.task.dto;

import com.cinemax.global.enums.TaskMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Task 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Task 생성/수정 요청")
public class TaskRequest {

    @NotNull(message = "Task ID는 필수입니다")
    @Schema(description = "Task ID", example = "1")
    private Long taskId;

    @NotNull(message = "Cycle ID는 필수입니다")
    @Schema(description = "Cycle ID", example = "1")
    private Long cycleId;

    @NotNull(message = "Curriculum ID는 필수입니다")
    @Schema(description = "Curriculum ID", example = "1")
    private Long curId;

    @NotNull(message = "Task Title은 필수입니다")
    @Schema(description = "Task Title", example = "1")
    private String taskTitle;

    @NotNull(message = "주차 번호는 필수입니다")
    @Positive(message = "주차 번호는 양수여야 합니다")
    @Schema(description = "주차 번호", example = "1")
    private Integer weekNo;

    @Schema(description = "Task To do Code")
    private String startCode;

    @Schema(description = "Task 모드 (ADVANCE, EASY)", example = "EASY")
    private TaskMode taskMode;

    @Schema(description = "컴파일 설정 JSON", example = "{\"timeout\": 3000}")
    private String configJson;

    @NotNull(message = "순서는 필수입니다")
    @Positive(message = "순서는 양수여야 합니다")
    @Schema(description = "주차 내 표시 순서", example = "1")
    private Integer orderNo;
}
