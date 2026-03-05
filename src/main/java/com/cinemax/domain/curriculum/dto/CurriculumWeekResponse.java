package com.cinemax.domain.curriculum.dto;

import com.cinemax.domain.curriculum.entity.CurriculumWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 커리큘럼 주차 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumWeekResponse {

    private Long curWeekId;
    private Long curId;
    private Integer weekNo;
    private String title;
    private String subtitle;
    private String content;
    private Integer curLev;
    private String characterNm;
    private LocalDateTime createDt;

    // Entity -> DTO 변환
    public static CurriculumWeekResponse from(CurriculumWeek curriculumWeek) {
        return CurriculumWeekResponse.builder()
                .curWeekId(curriculumWeek.getCurWeekId())
                .curId(curriculumWeek.getCurId())
                .weekNo(curriculumWeek.getWeekNo())
                .title(curriculumWeek.getTitle())
                .subtitle(curriculumWeek.getSubtitle())
                .content(curriculumWeek.getContent())
                .curLev(curriculumWeek.getCurLev())
                .characterNm(curriculumWeek.getCharacterNm())
                .createDt(curriculumWeek.getCreateDt())
                .build();
    }
}
