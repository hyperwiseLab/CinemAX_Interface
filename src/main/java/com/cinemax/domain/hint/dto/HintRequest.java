package com.cinemax.domain.hint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Hint 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hint 생성/수정 요청")
public class HintRequest {

    @NotNull(message = "Hint ID는 필수입니다")
    @Schema(description = "Hint ID", example = "1")
    private Long hintId;

    @NotNull(message = "Task ID는 필수입니다")
    @Schema(description = "Task ID", example = "1")
    private Long taskId;

    @NotBlank(message = "제목은 필수입니다")
    @Schema(description = "힌트 제목", example = "변수 선언 힌트")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    @Schema(description = "힌트 내용", example = "변수는 자료형과 함께 선언합니다")
    private String content;

    @Schema(description = "힌트 동영상 URL", example = "https://youtube.com/...")
    private String videoUrl;
}
