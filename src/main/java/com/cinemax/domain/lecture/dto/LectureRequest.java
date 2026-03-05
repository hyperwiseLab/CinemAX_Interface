package com.cinemax.domain.lecture.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Lecture 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Lecture 생성/수정 요청")
public class LectureRequest {

    @Schema(description = "Lecture ID (수정 시 사용)", example = "1")
    private Long lectureId;

    @NotNull(message = "Cycle ID는 필수입니다")
    @Schema(description = "Cycle ID", example = "1")
    private Long cycleId;

    @NotNull(message = "Task ID는 필수입니다")
    @Schema(description = "Task ID", example = "1")
    private Long taskId;

    @NotBlank(message = "캐릭터 이미지는 필수입니다")
    @JsonProperty("character")
    @Schema(description = "캐릭터 이미지 파일명", example = "profKim")
    private String characterImg;

    @NotBlank(message = "캐릭터 경로는 필수입니다")
    @Schema(description = "캐릭터 이미지 경로", example = "/images/characters/")
    private String characterPath;

    @NotBlank(message = "제목은 필수입니다")
    @Schema(description = "강의 노트 제목", example = "print() 함수와 문자열")
    private String title;

    @NotBlank(message = "Key Takeaway는 필수입니다")
    @Schema(description = "주요 요약 내용", example = "`print()` 함수는 괄호 안의 내용을 화면에 보여주는 가장 기본적인 명령입니다.")
    private String keyTakeaway;

    @NotBlank(message = "Sandbox Code는 필수입니다")
    @Schema(description = "샌드박스 코드", example = "print(\"파이썬, 반가워!\")")
    private String sandboxCode;

    @JsonProperty("sections")
    @Schema(description = "강의 섹션 목록")
    private List<LectureSectionRequest> sections;
}
