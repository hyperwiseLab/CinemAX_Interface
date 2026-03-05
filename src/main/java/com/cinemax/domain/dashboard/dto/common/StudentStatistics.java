package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 학생 통계
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentStatistics {
    private Long totalStudents;         // 전체 학생 수
    private Long activeStudents;        // 활동 중인 학생 수
    private Long completedStudents;     // 완료한 학생 수
    private Long idleStudents;          // 대기 중인 학생 수
    private Long needHelpStudents;      // 도움 필요 학생 수
}
