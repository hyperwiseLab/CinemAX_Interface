package com.cinemax.domain.dashboard.dto.student;


import com.cinemax.domain.dashboard.dto.common.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 스킬 레벨 분석 응답 DTO
 * - 언어별 숙련도
 * - 개념별 이해도
 * - 문제 해결 능력
 * - 레이더 차트 표시용 데이터
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스킬 레벨 분석")
public class SkillLevelAnalysisResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    // 언어별 숙련도
    @Schema(description = "언어별 숙련도 목록")
    private List<LanguageProficiency> languageProficiencies;

    // 개념별 이해도
    @Schema(description = "개념별 이해도 목록")
    private List<ConceptUnderstanding> conceptUnderstandings;

    // 문제 해결 능력
    @Schema(description = "문제 해결 능력 점수 (0-100)", example = "85.5")
    private BigDecimal problemSolvingScore;

    @Schema(description = "평균 해결 시간 (분)", example = "32.5")
    private BigDecimal averageSolvingTime;

    @Schema(description = "첫 제출 성공률 (%)", example = "75.5")
    private BigDecimal firstSubmitSuccessRate;

    // 레이더 차트용 데이터
    @Schema(description = "레이더 차트 데이터")
    private RadarChartData radarChartData;
}
