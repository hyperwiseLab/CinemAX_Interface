package com.cinemax.domain.activity.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.activity.dto.ActivityLogResponse;
import com.cinemax.domain.activity.dto.ActivityLogStatisticsResponse;
import com.cinemax.domain.activity.entity.ActivityMonitor;
import com.cinemax.global.enums.ActivityAction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ActivityMonitor 엔티티와 DTO 간 변환을 위한 Mapper
@Mapper(config = GlobalMapperConfig.class)
public interface ActivityMonitorMapper {

    @Mapping(source = "monitorId", target = "monitorId")
    @Mapping(source = "weeklySessionId", target = "weeklySessionId")
    @Mapping(source = "inviteId", target = "inviteId")
    @Mapping(source = "taskId", target = "taskId")
    @Mapping(source = "lastSeenDt", target = "lastSeenDt")
    @Mapping(source = "lastAction", target = "lastAction")
    @Mapping(source = "lastAction.description", target = "actionDescription")
    @Mapping(source = "weeklySession.weekNo", target = "weekNo")
    @Mapping(expression = "java(entity.getMinutesSinceLastActivity())", target = "minutesSinceLastActivity")
    ActivityLogResponse toDto(ActivityMonitor entity);

    List<ActivityLogResponse> toDto(List<ActivityMonitor> entities);

    default ActivityLogStatisticsResponse toStatisticsDto(List<ActivityMonitor> activityMonitors,
                                                          Long weeklySessionId,
                                                          Long inviteId) {
        if (activityMonitors.isEmpty()) {
            return ActivityLogStatisticsResponse.builder()
                    .weeklySessionId(weeklySessionId)
                    .inviteId(inviteId)
                    .totalActivityCount(0L)
                    .codeSaveCount(0L)
                    .codeRunCount(0L)
                    .testRunCount(0L)
                    .testPassCount(0L)
                    .testFailCount(0L)
                    .pageViewCount(0L)
                    .lectureViewCount(0L)
                    .hintViewCount(0L)
                    .questionSubmitCount(0L)
                    .build();
        }

        // 액션별 개수 계산
        Map<ActivityAction, Long> activityCountByAction = activityMonitors.stream()
                .collect(Collectors.groupingBy(ActivityMonitor::getLastAction, Collectors.counting()));

        // 최근 활동 정보
        ActivityMonitor latestActivity = activityMonitors.get(0);

        Integer weekNo = latestActivity.getWeeklySession() != null
                ? latestActivity.getWeeklySession().getWeekNo()
                : null;

        return ActivityLogStatisticsResponse.builder()
                .weeklySessionId(weeklySessionId)
                .inviteId(inviteId)
                .weekNo(weekNo)
                .totalActivityCount((long) activityMonitors.size())
                .codeSaveCount(activityCountByAction.getOrDefault(ActivityAction.CODE_SAVE, 0L))
                .codeRunCount(activityCountByAction.getOrDefault(ActivityAction.CODE_RUN, 0L))
                .testRunCount(activityCountByAction.getOrDefault(ActivityAction.TEST_RUN, 0L))
                .testPassCount(activityCountByAction.getOrDefault(ActivityAction.TEST_PASS, 0L))
                .testFailCount(activityCountByAction.getOrDefault(ActivityAction.TEST_FAIL, 0L))
                .pageViewCount(activityCountByAction.getOrDefault(ActivityAction.PAGE_VIEW, 0L))
                .lectureViewCount(activityCountByAction.getOrDefault(ActivityAction.LECTURE_VIEW, 0L))
                .hintViewCount(activityCountByAction.getOrDefault(ActivityAction.HINT_VIEW, 0L))
                .questionSubmitCount(activityCountByAction.getOrDefault(ActivityAction.QUESTION_SUBMIT, 0L))
                .activityCountByAction(activityCountByAction)
                .lastAction(latestActivity.getLastAction())
                .lastActionDescription(latestActivity.getLastAction().getDescription())
                .minutesSinceLastActivity(latestActivity.getMinutesSinceLastActivity())
                .build();
    }
}
