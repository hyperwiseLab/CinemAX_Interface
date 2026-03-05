package com.cinemax.domain.dashboard.dto.admin;


import com.cinemax.domain.dashboard.dto.common.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관리자 시스템 전체 대시보드 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 시스템 전체 대시보드 응답")
public class AdminSystemOverviewResponse {

    @Schema(description = "사용자 통계")
    private UserStatistics userStatistics;

    @Schema(description = "수업 통계")
    private ClassStatistics classStatistics;

    @Schema(description = "커리큘럼 통계")
    private CurriculumStatistics curriculumStatistics;

    @Schema(description = "활동 통계")
    private ActivityStatistics activityStatistics;
}
