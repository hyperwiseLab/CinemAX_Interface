package com.cinemax.domain.assign.dto;

import com.cinemax.domain.assign.entity.Assign;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Assign 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Assign 응답")
public class AssignResponse {

    @Schema(description = "Assign ID", example = "1")
    private Long assignId;

    @Schema(description = "Cycle ID", example = "1")
    private Long cycleId;

    @Schema(description = "Task ID", example = "1")
    private Long taskId;

    @Schema(description = "캐릭터 이미지 파일명", example = "character.png")
    private String characterImg;

    @Schema(description = "캐릭터 이미지 경로", example = "/images/characters/")
    private String characterPath;

    @Schema(description = "과제 제목", example = "변수 활용 문제")
    private String title;

    @Schema(description = "과제 부제목", example = "변수 선언과 출력")
    private String subTitle;

    @Schema(description = "과제 내용", example = "변수를 선언하고 값을 출력하는 프로그램을 작성하세요")
    private String assignContent;

    @Schema(description = "생성 날짜")
    private LocalDateTime createDt;

    @Schema(description = "수정 날짜")
    private LocalDateTime updateDt;

    // Entity -> DTO 변환
    public static AssignResponse from(Assign assign) {
        return AssignResponse.builder()
                .assignId(assign.getAssignId())
                .cycleId(assign.getCycleId())
                .taskId(assign.getTaskId())
                .characterImg(assign.getCharacterImg())
                .characterPath(assign.getCharacterPath())
                .title(assign.getTitle())
                .subTitle(assign.getSubTitle())
                .assignContent(assign.getAssignContent())
                .createDt(assign.getCreateDt())
                .updateDt(assign.getUpdateDt())
                .build();
    }
}
