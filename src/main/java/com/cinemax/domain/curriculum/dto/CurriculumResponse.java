package com.cinemax.domain.curriculum.dto;

import com.cinemax.domain.curriculum.entity.Curriculum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 커리큘럼 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumResponse {

    private Long curId;
    private String lang;
    private String name;
    private String description;
    private Integer durationWeeks;
    private Boolean useYn;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
    private List<CurriculumWeekResponse> curriculumWeeks;

    // Entity -> DTO 변환
    public static CurriculumResponse from(Curriculum curriculum) {
        return CurriculumResponse.builder()
                .curId(curriculum.getCurId())
                .lang(curriculum.getLang())
                .name(curriculum.getName())
                .description(curriculum.getDescription())
                .durationWeeks(curriculum.getDurationWeeks())
                .useYn(curriculum.getUseYn())
                .createDt(curriculum.getCreateDt())
                .updateDt(curriculum.getUpdateDt())
                .build();
    }

    // Entity -> DTO 변환 (주차 정보 포함)
    public static CurriculumResponse fromWithWeeks(Curriculum curriculum) {
        return CurriculumResponse.builder()
                .curId(curriculum.getCurId())
                .lang(curriculum.getLang())
                .name(curriculum.getName())
                .description(curriculum.getDescription())
                .durationWeeks(curriculum.getDurationWeeks())
                .useYn(curriculum.getUseYn())
                .createDt(curriculum.getCreateDt())
                .updateDt(curriculum.getUpdateDt())
                .curriculumWeeks(curriculum.getCurriculumWeeks().stream()
                        .map(CurriculumWeekResponse::from)
                        .toList())
                .build();
    }
}
