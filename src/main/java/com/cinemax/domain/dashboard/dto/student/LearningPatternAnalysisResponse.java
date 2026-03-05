package com.cinemax.domain.dashboard.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 학습 패턴 분석 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "학습 패턴 분석")
public class LearningPatternAnalysisResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "주요 학습 시간대", example = "오후 (14:00-18:00)")
    private String preferredStudyTime;

    @Schema(description = "평균 일일 학습 시간 (분)", example = "120.5")
    private BigDecimal averageDailyMinutes;

    @Schema(description = "학습 일관성 점수 (0-100)", example = "85.5")
    private BigDecimal consistencyScore;

    @Schema(description = "문제 해결 속도 추세", example = "개선중")
    private String solvingSpeedTrend;

    @Schema(description = "주간 활동 패턴 (월-일)")
    private List<Integer> weeklyActivityPattern;
}
