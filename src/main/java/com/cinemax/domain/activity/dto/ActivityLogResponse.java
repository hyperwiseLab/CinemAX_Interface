package com.cinemax.domain.activity.dto;

import com.cinemax.domain.activity.entity.ActivityMonitor;
import com.cinemax.global.enums.ActivityAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 활동 로그 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponse {

    private Long monitorId;
    private Long weeklySessionId;
    private Long inviteId;
    private Long taskId;
    private LocalDateTime lastSeenDt;
    private ActivityAction lastAction;
    private String actionDescription;
    private Long minutesSinceLastActivity;
    private Integer weekNo;

    // ActivityMonitor 엔티티로부터 DTO 생성
    public static ActivityLogResponse from(ActivityMonitor activityMonitor) {
        return ActivityLogResponse.builder()
                .monitorId(activityMonitor.getMonitorId())
                .weeklySessionId(activityMonitor.getWeeklySessionId())
                .inviteId(activityMonitor.getInviteId())
                .taskId(activityMonitor.getTaskId())
                .lastSeenDt(activityMonitor.getLastSeenDt())
                .lastAction(activityMonitor.getLastAction())
                .actionDescription(activityMonitor.getLastAction().getDescription())
                .minutesSinceLastActivity(activityMonitor.getMinutesSinceLastActivity())
                .weekNo(activityMonitor.getWeeklySession() != null
                        ? activityMonitor.getWeeklySession().getWeekNo()
                        : null)
                .build();
    }
}
