package com.cinemax.domain.dashboard.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 통계 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 통계")
public class UserStatistics {

    @Schema(description = "전체 사용자 수")
    private Long totalUsers;

    @Schema(description = "학생 수")
    private Long studentCount;

    @Schema(description = "교수 수")
    private Long professorCount;

    @Schema(description = "관리자 수")
    private Long adminCount;

    @Schema(description = "활성 사용자 수")
    private Long activeUsers;

    @Schema(description = "신규 가입자 수 (최근 7일)")
    private Long newUsersLast7Days;

    @Schema(description = "신규 가입자 수 (최근 30일)")
    private Long newUsersLast30Days;
}
