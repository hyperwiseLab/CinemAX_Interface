package com.cinemax.domain.lecture.dto;

import com.cinemax.domain.lecture.entity.LectureSection;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * LectureSection 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "LectureSection 응답")
public class LectureSectionResponse {

    @Schema(description = "섹션 제목", example = "핵심 개념")
    private String heading;

    @JsonProperty("text")
    @Schema(description = "섹션 설명 텍스트", example = "프로그램은 컴퓨터에 내리는 '명령문'들의 순차적인 나열입니다")
    private String lectureSectionTxt;

    @JsonProperty("code")
    @Schema(description = "섹션 코드 예제", example = "print(\"Hello, World!\")")
    private String lectureSectionCode;

    /**
     * Entity -> DTO 변환
     */
    public static LectureSectionResponse from(LectureSection lectureSection) {
        return LectureSectionResponse.builder()
                .heading(lectureSection.getHeading())
                .lectureSectionTxt(lectureSection.getLectureSectionTxt())
                .lectureSectionCode(lectureSection.getLectureSectionCode())
                .build();
    }
}
