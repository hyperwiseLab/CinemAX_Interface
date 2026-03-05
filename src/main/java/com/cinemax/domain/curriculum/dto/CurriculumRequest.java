package com.cinemax.domain.curriculum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 커리큘럼 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumRequest {

    @NotBlank(message = "언어는 필수입니다")
    private String lang;

    @NotBlank(message = "커리큘럼명은 필수입니다")
    private String name;

    private String description;

    @NotNull(message = "기간(주차)은 필수입니다")
    @Positive(message = "기간은 양수여야 합니다")
    private Integer durationWeeks;

    @Builder.Default
    private Boolean useYn = true;

    private List<CurriculumWeekRequest> curriculumWeeks;
}
