package com.cinemax.domain.dashboard.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 커리큘럼 통계 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "커리큘럼 통계")
public class CurriculumStatistics {

    @Schema(description = "전체 커리큘럼 수")
    private Long totalCurriculums;

    @Schema(description = "활성 커리큘럼 수")
    private Long activeCurriculums;

    @Schema(description = "언어별 커리큘럼 수 (Python)")
    private Long pythonCurriculums;

    @Schema(description = "언어별 커리큘럼 수 (Java)")
    private Long javaCurriculums;

    @Schema(description = "언어별 커리큘럼 수 (JavaScript)")
    private Long javascriptCurriculums;

    @Schema(description = "언어별 커리큘럼 수 (C)")
    private Long cCurriculums;

    @Schema(description = "언어별 커리큘럼 수 (C#)")
    private Long csharpCurriculums;

    @Schema(description = "가장 많이 사용되는 커리큘럼 언어")
    private String mostUsedLanguage;
}
