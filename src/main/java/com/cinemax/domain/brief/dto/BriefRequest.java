package com.cinemax.domain.brief.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Brief 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Brief 생성/수정 요청")
public class BriefRequest {

    @Schema(description = "Brief ID (수정 시 사용)", example = "1")
    private Long briefId;

    @NotNull(message = "Cycle ID는 필수입니다")
    @Schema(description = "Cycle ID", example = "1")
    private Long cycleId;

    @NotNull(message = "Task ID는 필수입니다")
    @Schema(description = "Task ID", example = "1")
    private Long taskId;

    @NotBlank(message = "캐릭터 이미지는 필수입니다")
    @Schema(description = "캐릭터 이미지 파일명", example = "character.png")
    private String characterImg;

    @NotBlank(message = "캐릭터 경로는 필수입니다")
    @Schema(description = "캐릭터 이미지 경로", example = "/images/characters/")
    private String characterPath;

    @NotBlank(message = "제목은 필수입니다")
    @Schema(description = "Briefing 제목", example = "변수와 자료형")
    private String title;

    @NotBlank(message = "부제목은 필수입니다")
    @Schema(description = "Briefing 부제목", example = "프로그래밍의 기초")
    private String subTitle;

    @NotBlank(message = "내용은 필수입니다")
    @Schema(description = "Briefing 내용", example = "이번 과제에서는...")
    private String briefContent;
}
