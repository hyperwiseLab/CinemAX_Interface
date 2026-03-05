package com.cinemax.domain.assign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Assign 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Assign 생성/수정 요청")
public class AssignRequest {

    @Schema(description = "Assign ID (수정 시 사용)", example = "1")
    private Long assignId;

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
    @Schema(description = "과제 제목", example = "변수 활용 문제")
    private String title;

    @NotBlank(message = "부제목은 필수입니다")
    @Schema(description = "과제 부제목", example = "변수 선언과 출력")
    private String subTitle;

    @NotBlank(message = "과제 내용은 필수입니다")
    @Schema(description = "과제 내용", example = "변수를 선언하고 값을 출력하는 프로그램을 작성하세요")
    private String assignContent;
}
