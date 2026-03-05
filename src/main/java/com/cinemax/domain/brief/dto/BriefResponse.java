package com.cinemax.domain.brief.dto;

import com.cinemax.domain.brief.entity.Brief;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Brief 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Brief 응답")
public class BriefResponse {

    @Schema(description = "Brief ID", example = "1")
    private Long briefId;

    @Schema(description = "Cycle ID", example = "1")
    private Long cycleId;

    @Schema(description = "Task ID", example = "1")
    private Long taskId;

    @Schema(description = "캐릭터 이미지 파일명", example = "character.png")
    private String characterImg;

    @Schema(description = "캐릭터 이미지 경로", example = "/images/characters/")
    private String characterPath;

    @Schema(description = "Briefing 제목", example = "변수와 자료형")
    private String title;

    @Schema(description = "Briefing 부제목", example = "프로그래밍의 기초")
    private String subTitle;

    @Schema(description = "Briefing 내용", example = "이번 과제에서는...")
    private String briefContent;

    @Schema(description = "생성 날짜")
    private LocalDateTime createDt;

    @Schema(description = "수정 날짜")
    private LocalDateTime updateDt;

    // Entity -> DTO 변환
    public static BriefResponse from(Brief brief) {
        return BriefResponse.builder()
                .briefId(brief.getBriefId())
                .cycleId(brief.getCycleId())
                .taskId(brief.getTaskId())
                .characterImg(brief.getCharacterImg())
                .characterPath(brief.getCharacterPath())
                .title(brief.getTitle())
                .subTitle(brief.getSubTitle())
                .briefContent(brief.getBriefContent())
                .createDt(brief.getCreateDt())
                .updateDt(brief.getUpdateDt())
                .build();
    }
}
