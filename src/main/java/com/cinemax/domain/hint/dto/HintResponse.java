package com.cinemax.domain.hint.dto;

import com.cinemax.domain.hint.entity.Hint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Hint 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hint 응답")
public class HintResponse {

    @Schema(description = "Hint ID", example = "1")
    private Long hintId;

    @Schema(description = "Task ID", example = "1")
    private Long taskId;

    @Schema(description = "힌트 제목", example = "변수 선언 힌트")
    private String title;

    @Schema(description = "힌트 내용", example = "변수는 자료형과 함께 선언합니다")
    private String content;

    @Schema(description = "힌트 동영상 URL", example = "https://youtube.com/...")
    private String videoUrl;

    @Schema(description = "생성 날짜")
    private LocalDateTime createDt;

    @Schema(description = "수정 날짜")
    private LocalDateTime updateDt;

    /**
     * Entity -> DTO 변환
     */
    public static HintResponse from(Hint hint) {
        return HintResponse.builder()
                .hintId(hint.getHintId())
                .taskId(hint.getTaskId())
                .title(hint.getTitle())
                .content(hint.getContent())
                .videoUrl(hint.getVideoUrl())
                .createDt(hint.getCreateDt())
                .updateDt(hint.getUpdateDt())
                .build();
    }
}
