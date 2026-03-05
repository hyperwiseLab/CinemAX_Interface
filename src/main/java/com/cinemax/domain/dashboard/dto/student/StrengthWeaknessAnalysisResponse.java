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
 * 강점/약점 분석 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "강점/약점 분석")
public class StrengthWeaknessAnalysisResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "강점 영역")
    private List<SkillArea> strengths;

    @Schema(description = "약점 영역")
    private List<SkillArea> weaknesses;

    @Schema(description = "개선 추세")
    private List<ImprovementTrend> improvementTrends;
}
