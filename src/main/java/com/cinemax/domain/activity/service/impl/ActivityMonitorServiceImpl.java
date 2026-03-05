package com.cinemax.domain.activity.service.impl;

import com.cinemax.domain.activity.dto.*;
import com.cinemax.domain.activity.entity.ActivityMonitor;
import com.cinemax.domain.activity.mapper.ActivityMonitorMapper;
import com.cinemax.domain.activity.repository.ActivityMonitorRepository;
import com.cinemax.domain.activity.service.ActivityMonitorService;
import com.cinemax.domain.progress.entity.Progress;
import com.cinemax.domain.progress.repository.ProgressRepository;
import com.cinemax.global.enums.ActivityAction;
import com.cinemax.global.enums.StudentActivityStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 실시간 활동 모니터링 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityMonitorServiceImpl implements ActivityMonitorService {

    private final ProgressRepository progressRepository;
    private final ActivityMonitorRepository activityMonitorRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ActivityMonitorMapper activityMonitorMapper;

    // 주차별 수업 모든 학생 활동 상태 조회
    @Override
    public List<StudentActivityResponse> getAllStudentActivities(Long weeklySessionId) {

        List<Progress> progresses = progressRepository.findAllByWeeklySessionId(weeklySessionId);

        return progresses.stream()
                .map(progress -> {
                    StudentActivityResponse baseResponse = StudentActivityResponse.from(progress);

                    // ActivityMonitor의 createDt 조회 (inviteId를 통해)
                    if (progress.getWeeklySession() != null && progress.getWeeklySession().getInviteId() != null) {
                        Long inviteId = progress.getWeeklySession().getInviteId();
                        return activityMonitorRepository.findLatestByWeeklySessionIdAndInviteId(weeklySessionId, inviteId)
                                .map(activityMonitor -> StudentActivityResponse.builder()
                                        .userId(baseResponse.getUserId())
                                        .userName(baseResponse.getUserName())
                                        .userEmail(baseResponse.getUserEmail())
                                        .studentNum(baseResponse.getStudentNum())
                                        .weeklySessionId(baseResponse.getWeeklySessionId())
                                        .inviteId(baseResponse.getInviteId())
                                        .weekNo(baseResponse.getWeekNo())
                                        .progressPct(baseResponse.getProgressPct())
                                        .activityStatus(baseResponse.getActivityStatus())
                                        .activityStatusDisplay(baseResponse.getActivityStatusDisplay())
                                        .lastActivityTime(baseResponse.getLastActivityTime())
                                        .testFailCount(baseResponse.getTestFailCount())
                                        .completedDt(baseResponse.getCompletedDt())
                                        .minutesSinceLastActivity(baseResponse.getMinutesSinceLastActivity())
                                        .activityMonitorCreateDt(activityMonitor.getCreateDt())
                                        .cycleCount(baseResponse.getCycleCount())
                                        .mode(baseResponse.getMode())
                                        .build())
                                .orElse(baseResponse);
                    }

                    return baseResponse;
                })
                .collect(Collectors.toList());
    }

    // 특정 상태 학생 조회
    @Override
    public List<StudentActivityResponse> getStudentsByStatus(Long weeklySessionId, StudentActivityStatus status) {

        List<Progress> progresses = progressRepository.findAllByWeeklySessionIdAndStatus(weeklySessionId, status);

        return progresses.stream()
                .map(StudentActivityResponse::from)
                .collect(Collectors.toList());
    }

    // 도움 필요 학생 조회
    @Override
    public List<StudentActivityResponse> getStudentsNeedingHelp(Long weeklySessionId) {

        List<Progress> progresses = progressRepository.findStudentsNeedingHelp(weeklySessionId);

        return progresses.stream()
                .map(StudentActivityResponse::from)
                .collect(Collectors.toList());
    }

    // 활동 중 학생 조회
    @Override
    public List<StudentActivityResponse> getActiveStudents(Long weeklySessionId) {

        List<Progress> progresses = progressRepository.findActiveStudents(weeklySessionId);

        return progresses.stream()
                .map(StudentActivityResponse::from)
                .collect(Collectors.toList());
    }

    // 완료 학생 조회
    @Override
    public List<StudentActivityResponse> getCompletedStudents(Long weeklySessionId) {

        List<Progress> progresses = progressRepository.findCompletedStudents(weeklySessionId);

        return progresses.stream()
                .map(StudentActivityResponse::from)
                .collect(Collectors.toList());
    }

    // 학생 활동 상태 조회
    @Override
    public StudentActivityResponse getStudentActivity(Long weeklySessionId, Long userId) {

        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다."));

        StudentActivityResponse baseResponse = StudentActivityResponse.from(progress);

        // ActivityMonitor의 createDt 조회 (inviteId를 통해)
        if (progress.getWeeklySession() != null && progress.getWeeklySession().getInviteId() != null) {
            Long inviteId = progress.getWeeklySession().getInviteId();
            return activityMonitorRepository.findLatestByWeeklySessionIdAndInviteId(weeklySessionId, inviteId)
                    .map(activityMonitor -> StudentActivityResponse.builder()
                            .userId(baseResponse.getUserId())
                            .userName(baseResponse.getUserName())
                            .userEmail(baseResponse.getUserEmail())
                            .studentNum(baseResponse.getStudentNum())
                            .weeklySessionId(baseResponse.getWeeklySessionId())
                            .inviteId(baseResponse.getInviteId())
                            .weekNo(baseResponse.getWeekNo())
                            .progressPct(baseResponse.getProgressPct())
                            .activityStatus(baseResponse.getActivityStatus())
                            .activityStatusDisplay(baseResponse.getActivityStatusDisplay())
                            .lastActivityTime(baseResponse.getLastActivityTime())
                            .testFailCount(baseResponse.getTestFailCount())
                            .completedDt(baseResponse.getCompletedDt())
                            .minutesSinceLastActivity(baseResponse.getMinutesSinceLastActivity())
                            .activityMonitorCreateDt(activityMonitor.getCreateDt())
                            .cycleCount(baseResponse.getCycleCount())
                            .mode(baseResponse.getMode())
                            .build())
                    .orElse(baseResponse);
        }

        return baseResponse;
    }

    // 모든 학생 활동 상태 갱신
    @Override
    @Transactional
    public void refreshAllStudentStatuses(Long weeklySessionId) {

        List<Progress> progresses = progressRepository.findAllByWeeklySessionId(weeklySessionId);

        progresses.forEach(progress -> {
            StudentActivityStatus previousStatus = progress.getActivityStatus();
            progress.updateActivityStatus();

            // 상태가 변경된 경우 WebSocket 브로드캐스트
            if (previousStatus != progress.getActivityStatus()) {
                broadcastStatusChange(progress, previousStatus);
            }
        });

        progressRepository.saveAll(progresses);
        log.info("활동 상태 갱신 완료 - 총 {} 명", progresses.size());
    }

    // 학생 활동 기록
    @Override
    @Transactional
    public void recordActivity(Long weeklySessionId, Long userId) {

        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다."));

        StudentActivityStatus previousStatus = progress.getActivityStatus();
        progress.updateActivity();

        progressRepository.save(progress);

        // 상태가 변경된 경우 WebSocket 브로드캐스트
        if (previousStatus != progress.getActivityStatus()) {
            broadcastStatusChange(progress, previousStatus);
        }
    }


    // 테스트 실패 기록
    @Override
    @Transactional
    public void recordTestFailure(Long weeklySessionId, Long userId) {

        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다."));

        StudentActivityStatus previousStatus = progress.getActivityStatus();
        progress.incrementTestFailCount();

        progressRepository.save(progress);

        // 상태가 변경된 경우 WebSocket 브로드캐스트
        if (previousStatus != progress.getActivityStatus()) {
            broadcastStatusChange(progress, previousStatus);
        }
    }

    // 테스트 성공 기록
    @Override
    @Transactional
    public void recordTestSuccess(Long weeklySessionId, Long userId) {

        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다."));

        StudentActivityStatus previousStatus = progress.getActivityStatus();
        progress.resetTestFailCount();
        progress.updateActivity();

        progressRepository.save(progress);

        // 상태가 변경된 경우 WebSocket 브로드캐스트
        if (previousStatus != progress.getActivityStatus()) {
            broadcastStatusChange(progress, previousStatus);
        }
    }

    // ===== 활동 로그 기록 및 조회 =====

    // 활동 로그 기록
    @Override
    @Transactional
    public ActivityLogResponse recordActivityLog(ActivityLogRequest request) {

        return recordActivityLog(request.getWeeklySessionId(), request.getInviteId(), request.getTaskId(), request.getAction());
    }

    //활동 로그 기록
    @Override
    @Transactional
    public ActivityLogResponse recordActivityLog(Long weeklySessionId, Long inviteId, Long taskId, ActivityAction action) {

        ActivityMonitor activityMonitor = ActivityMonitor.create(weeklySessionId, inviteId, taskId, action);

        ActivityMonitor savedActivityMonitor = activityMonitorRepository.save(activityMonitor);

        return activityMonitorMapper.toDto(savedActivityMonitor);
    }

    // 주차별 수업 모든 활동 로그 조회
    @Override
    public List<ActivityLogResponse> getAllActivityLogs(Long weeklySessionId) {

        List<ActivityMonitor> activityMonitors = activityMonitorRepository.findAllByWeeklySessionId(weeklySessionId);

        return activityMonitorMapper.toDto(activityMonitors);
    }

    // 특정 학생 활동 로그 조회
    @Override
    public List<ActivityLogResponse> getActivityLogsByInviteId(Long weeklySessionId, Long inviteId) {

        List<ActivityMonitor> activityMonitors = activityMonitorRepository
                .findByWeeklySessionIdAndInviteId(weeklySessionId, inviteId);

        return activityMonitorMapper.toDto(activityMonitors);
    }

    // 특정 액션 타입 활동 로그 조회
    @Override
    public List<ActivityLogResponse> getActivityLogsByAction(Long weeklySessionId, ActivityAction action) {

        List<ActivityMonitor> activityMonitors = activityMonitorRepository
                .findByWeeklySessionIdAndAction(weeklySessionId, action);

        return activityMonitorMapper.toDto(activityMonitors);
    }

    // 특정 시간 이후 활동 로그 조회
    @Override
    public List<ActivityLogResponse> getRecentActivityLogs(Long weeklySessionId, LocalDateTime fromTime) {

        List<ActivityMonitor> activityMonitors = activityMonitorRepository
                .findRecentActivities(weeklySessionId, fromTime);

        return activityMonitorMapper.toDto(activityMonitors);
    }

    // 특정 학생의 특정 시간 이후 활동 로그 조회
    @Override
    public List<ActivityLogResponse> getRecentActivityLogsByInviteId(Long weeklySessionId, Long inviteId, LocalDateTime fromTime) {

        List<ActivityMonitor> activityMonitors = activityMonitorRepository
                .findRecentActivitiesByInviteId(weeklySessionId, inviteId, fromTime);

        return activityMonitorMapper.toDto(activityMonitors);
    }

    // 활동 로그 통계 조회
    @Override
    public ActivityLogStatisticsResponse getActivityLogStatistics(Long weeklySessionId, Long inviteId) {

        List<ActivityMonitor> activityMonitors = activityMonitorRepository
                .findByWeeklySessionIdAndInviteId(weeklySessionId, inviteId);

        return activityMonitorMapper.toStatisticsDto(activityMonitors, weeklySessionId, inviteId);
    }

    // 최근 활동 로그 조회
    @Override
    public ActivityLogResponse getLatestActivityLog(Long weeklySessionId, Long inviteId) {

        ActivityMonitor activityMonitor = activityMonitorRepository
                .findLatestByWeeklySessionIdAndInviteId(weeklySessionId, inviteId)
                .orElseThrow(() -> new IllegalArgumentException("활동 로그를 찾을 수 없습니다."));

        return activityMonitorMapper.toDto(activityMonitor);
    }

    // 유저/과제별 에러(테스트 실패) 카운트 조회
    @Override
    public long getErrorCount(Long userId, Long taskId) {
        return activityMonitorRepository.countErrorByUserIdAndTaskId(userId, taskId, ActivityAction.TEST_FAIL);
    }

    // 에러(테스트 실패) 기록
    @Override
    @Transactional
    public ActivityLogResponse recordErrorCount(Long userId, Long weeklySessionId, Long taskId) {
        // userId와 weeklySessionId로 Progress 조회하여 inviteId 찾기
        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다. userId: " + userId + ", weeklySessionId: " + weeklySessionId));

        // WeeklySession에서 inviteId 가져오기
        Long inviteId = progress.getWeeklySession() != null ? progress.getWeeklySession().getInviteId() : null;
        if (inviteId == null) {
            throw new IllegalArgumentException("초대 정보를 찾을 수 없습니다.");
        }

        return recordActivityLog(weeklySessionId, inviteId, taskId, ActivityAction.TEST_FAIL);
    }

    // WebSocket을 통한 활동 상태 변경 브로드캐스트
    private void broadcastStatusChange(Progress progress, StudentActivityStatus previousStatus) {
        try {
            ActivityStatusMessage message = ActivityStatusMessage.from(progress, previousStatus);

            // 주제: /topic/activity-monitor/{weeklySessionId}
            String destination = String.format("/topic/activity-monitor/%d",
                    progress.getWeeklySessionId());

            messagingTemplate.convertAndSend(destination, message);

            log.info("WebSocket 활동 상태 변경 메시지 전송 완료 - destination: {}, userId: {}, {} -> {}",
                    destination, progress.getUser().getUserId(), previousStatus, progress.getActivityStatus());

        } catch (Exception e) {
            log.error("WebSocket 메시지 전송 실패 - userId: {}", progress.getUser().getUserId(), e);
        }
    }
}
