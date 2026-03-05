package com.cinemax.domain.lecture.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lecture 응답 DTO
 * MapStruct를 통해 Lecture 엔티티로부터 자동 변환됨
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Lecture 응답")
public class LectureResponse {

    @Schema(description = "Lecture ID", example = "1")
    private Long lectureId;

    @Schema(description = "Cycle ID", example = "1")
    private Long cycleId;

    @Schema(description = "Task ID", example = "1")
    private Long taskId;

    @JsonProperty("character")
    @Schema(description = "캐릭터 이미지 파일명", example = "profKim")
    private String characterImg;

    @Schema(description = "캐릭터 이미지 경로", example = "/images/characters/")
    private String characterPath;

    @Schema(description = "강의 노트 제목", example = "print() 함수와 문자열")
    private String title;

    @Schema(description = "주요 요약 내용", example = "`print()` 함수는 괄호 안의 내용을 화면에 보여주는 가장 기본적인 명령입니다.")
    private String keyTakeaway;

    @Schema(description = "샌드박스 코드", example = "print(\"파이썬, 반가워!\")")
    private String sandboxCode;

    @JsonProperty("sections")
    @Schema(description = "강의 섹션 목록")
    private List<LectureSectionResponse> sections;

    @Schema(description = "생성 날짜")
    private LocalDateTime createDt;

    @Schema(description = "수정 날짜")
    private LocalDateTime updateDt;
}
