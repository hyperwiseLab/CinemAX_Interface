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
 * 학생 성과 대시보드 응답 DTO
 * - 시각화된 성과 그래프
 * - 주차별 비교
 * - 개선 추세
 * - 목표 대비 달성률
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "학생 성과 대시보드")
public class StudentPerformanceDashboardResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "수업 ID", example = "1")
    private Long classId;

    @Schema(description = "수업 이름", example = "자바 프로그래밍")
    private String className;

    // 주차별 성과 데이터
    @Schema(description = "주차별 성과 그래프 데이터")
    private List<WeeklyPerformance> weeklyPerformances;

    // 전체 통계
    @Schema(description = "총 학습 시간 (시간)", example = "45.5")
    private BigDecimal totalStudyHours;

    @Schema(description = "총 제출 횟수", example = "25")
    private Long totalSubmissions;

    @Schema(description = "평균 제출 시간 (분)", example = "32.5")
    private BigDecimal averageSubmissionTime;

    @Schema(description = "성공률 (%)", example = "85.5")
    private BigDecimal successRate;

    // 개선 추세
    @Schema(description = "최근 4주 개선 추세", example = "IMPROVING")
    private String improvementTrend; // IMPROVING, STABLE, DECLINING

    @Schema(description = "지난 주 대비 성공률 변화 (%)", example = "5.5")
    private BigDecimal successRateChange;

    // 목표 대비 달성률
    @Schema(description = "목표 진도율 (%)", example = "80.0")
    private BigDecimal targetProgress;

    @Schema(description = "현재 진도율 (%)", example = "75.5")
    private BigDecimal currentProgress;

    @Schema(description = "목표 달성률 (%)", example = "94.4")
    private BigDecimal achievementRate;

    // 순위 정보
    @Schema(description = "수업 내 순위", example = "3")
    private Integer classRank;

    @Schema(description = "전체 학생 수", example = "30")
    private Integer totalStudents;
}
