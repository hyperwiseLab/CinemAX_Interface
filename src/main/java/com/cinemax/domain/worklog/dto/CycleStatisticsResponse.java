package com.cinemax.domain.worklog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Cycle별 WorkLog 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CycleStatisticsResponse {

    private Long cycleId;
    private Integer weekNo;
    private String cycleTitle;

    // 코드 활용 점수 평균 (proficiencyLevel)
    private Double proficiencyAvg;

    // 개념 이해 점수 평균 (difficultyLevel)
    private Double difficultyAvg;

    // 전체 평균 = (proficiencyAvg + difficultyAvg) / 2
    private Double overallAvg;

    // 해당 Cycle의 WorkLog 개수
    private Long workLogCount;

    // 총 작업 시간
    private BigDecimal totalWorkHours;

    /**
     * 전체 평균 계산
     */
    public void calculateOverallAvg() {
        if (proficiencyAvg != null && difficultyAvg != null) {
            this.overallAvg = roundToTwoDecimals((proficiencyAvg + difficultyAvg) / 2.0);
        } else if (proficiencyAvg != null) {
            this.overallAvg = roundToTwoDecimals(proficiencyAvg);
        } else if (difficultyAvg != null) {
            this.overallAvg = roundToTwoDecimals(difficultyAvg);
        }
    }

    /**
     * 소수점 2자리로 반올림
     */
    private Double roundToTwoDecimals(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * 평균값 반올림 처리
     */
    public void roundAverages() {
        this.proficiencyAvg = roundToTwoDecimals(proficiencyAvg);
        this.difficultyAvg = roundToTwoDecimals(difficultyAvg);
        calculateOverallAvg();
    }
}
