package com.cinemax.domain.lecture.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LectureSection 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "LectureSection 생성/수정 요청")
public class LectureSectionRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Schema(description = "섹션 제목", example = "핵심 개념")
    private String heading;

    @NotBlank(message = "섹션 텍스트는 필수입니다")
    @JsonProperty("text")
    @Schema(description = "섹션 설명 텍스트", example = "프로그램은 컴퓨터에 내리는 '명령문'들의 순차적인 나열입니다")
    private String lectureSectionTxt;

    @JsonProperty("code")
    @Schema(description = "섹션 코드 예제", example = "print(\"Hello, World!\")")
    private String lectureSectionCode;
}
