package com.cinemax.domain.curriculum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커리큘럼 주차 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumWeekRequest {

    @NotNull(message = "주차 번호는 필수입니다")
    @Positive(message = "주차 번호는 양수여야 합니다")
    private Integer weekNo;

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    private String subtitle;
    private String content;

    @NotNull(message = "난이도는 필수입니다")
    @Positive(message = "난이도는 양수여야 합니다")
    private Integer curLev;

    private String characterNm;
}