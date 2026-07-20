package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자용: 반 학생별 회차별 응시 현황
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 반 응시 현황(관리자)")
public class CbtClassAttemptsResponse {

    @Schema(description = "총 응시 학생 수")
    private Integer studentCount;

    @Schema(description = "총 응시 횟수")
    private Integer attemptCount;

    @Schema(description = "학생별 현황")
    private List<StudentRow> students;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "학생별 응시 현황")
    public static class StudentRow {

        @Schema(description = "학생 ID")
        private Long userId;

        @Schema(description = "학생 이름")
        private String userName;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "응시 횟수")
        private Integer attemptCount;

        @Schema(description = "최고 점수")
        private Double bestScore;

        @Schema(description = "합격 이력 여부(한 번이라도 합격)")
        private Boolean everPassed;

        @Schema(description = "마지막 응시 시간")
        private LocalDateTime lastAttemptAt;

        @Schema(description = "회차별 기록 (회차 오름차순)")
        private List<CbtAttemptSummaryResponse> attempts;
    }
}
