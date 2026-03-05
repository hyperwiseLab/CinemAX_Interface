package com.cinemax.domain.activity.dto;

import com.cinemax.global.enums.ActivityAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 활동 로그 통계 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogStatisticsResponse {

    private Long weeklySessionId;
    private Long inviteId;
    private Integer weekNo;

    // 총 활동 수
    private Long totalActivityCount;

    // 액션별 활동 수
    private Long codeSaveCount;
    private Long codeRunCount;
    private Long testRunCount;
    private Long testPassCount;
    private Long testFailCount;
    private Long pageViewCount;
    private Long lectureViewCount;
    private Long hintViewCount;
    private Long questionSubmitCount;

    // 활동 타입별 비율
    private Map<ActivityAction, Long> activityCountByAction;

    // 최근 활동 정보
    private ActivityAction lastAction;
    private String lastActionDescription;
    private Long minutesSinceLastActivity;
}
