package com.cinemax.domain.dashboard.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 수업 통계 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "수업 통계")
public class ClassStatistics {

    @Schema(description = "전체 수업 수")
    private Long totalClasses;

    @Schema(description = "활성 수업 수")
    private Long activeClasses;

    @Schema(description = "진행 중인 차시 수")
    private Long inProgressSessions;

    @Schema(description = "완료된 차시 수")
    private Long completedSessions;

    @Schema(description = "전체 학생 참여 수")
    private Long totalEnrollments;

    @Schema(description = "평균 수업당 학생 수")
    private BigDecimal averageStudentsPerClass;
}
