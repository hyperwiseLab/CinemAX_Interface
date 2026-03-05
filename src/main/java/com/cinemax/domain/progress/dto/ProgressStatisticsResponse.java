package com.cinemax.domain.progress.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 진도 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "진도 통계 응답")
public class ProgressStatisticsResponse {

    @Schema(description = "주차별 수업 ID", example = "1")
    private Long weeklySessionId;

    @Schema(description = "주차 번호", example = "1")
    private Integer weekNo;

    @Schema(description = "총 학생 수", example = "30")
    private Integer totalStudents;

    @Schema(description = "활동중 학생 수", example = "15")
    private Integer activeStudents;

    @Schema(description = "도움 필요 학생 수", example = "3")
    private Integer studentsNeedingHelp;

    @Schema(description = "대기중 학생 수", example = "7")
    private Integer idleStudents;

    @Schema(description = "완료 학생 수", example = "5")
    private Integer completedStudents;

    @Schema(description = "평균 진도율 (%)", example = "65.75")
    private BigDecimal averageProgress;

    @Schema(description = "최소 진도율 (%)", example = "0.00")
    private BigDecimal minProgress;

    @Schema(description = "최대 진도율 (%)", example = "100.00")
    private BigDecimal maxProgress;

    @Schema(description = "완료율 (%)", example = "16.67")
    private BigDecimal completionRate;
}
