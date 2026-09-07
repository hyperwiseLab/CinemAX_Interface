package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 주차별 CBT 설정 + 주차별 보유 문항 수 (관리자 화면용)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주차별 CBT 설정 목록")
public class CbtWeekConfigResponse {

    @Schema(description = "커리큘럼 총 주차 수")
    private Integer totalWeeks;

    @Schema(description = "주차별 설정")
    private List<Item> weeks;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "주차 설정 항목")
    public static class Item {

        @Schema(description = "주차 번호")
        private Integer weekNo;

        @Schema(description = "주차 제목(커리큘럼 기준)")
        private String title;

        @Schema(description = "출제 문항 수")
        private Integer questionCount;

        @Schema(description = "합격 기준 정답률(%)")
        private Double passScore;

        @Schema(description = "사용 여부")
        private Boolean useYn;

        @Schema(description = "해당 주차 보유 문항 수(활성)")
        private Long bankCount;

        @Schema(description = "보유 문항이 출제 수보다 적으면 true")
        private Boolean insufficient;
    }
}
