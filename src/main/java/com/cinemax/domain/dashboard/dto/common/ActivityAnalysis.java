package com.cinemax.domain.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 활동 분석
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAnalysis {
    private Long totalActivities;           // 총 활동 수
    private Long codeSubmissions;           // 코드 제출 수
    private Long codePeeks;                 // 정답 코드 엿보기 수
    private Long snapshotSaves;             // 코드 자동저장 수
    private LocalDateTime firstActivity;    // 첫 활동 시간
    private LocalDateTime lastActivity;     // 마지막 활동 시간
}
