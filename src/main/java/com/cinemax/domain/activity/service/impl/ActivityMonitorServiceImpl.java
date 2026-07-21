package com.cinemax.domain.activity.service.impl;

import com.cinemax.domain.activity.dto.*;
import com.cinemax.domain.activity.entity.ActivityMonitor;
import com.cinemax.domain.activity.mapper.ActivityMonitorMapper;
import com.cinemax.domain.activity.repository.ActivityMonitorRepository;
import com.cinemax.domain.activity.service.ActivityMonitorService;
import com.cinemax.domain.classes.entity.ClassSubmit;
import com.cinemax.domain.classes.repository.ClassEnrollRepository;
import com.cinemax.domain.classes.repository.ClassSubmitRepository;
import com.cinemax.domain.codesnapshot.entity.CodeSnapshot;
import com.cinemax.domain.codesnapshot.repository.CodeSnapshotRepository;
import com.cinemax.domain.curriculum.entity.CurriculumWeek;
import com.cinemax.domain.curriculum.repository.CurriculumWeekRepository;
import com.cinemax.domain.cycle.repository.CycleRepository;
import com.cinemax.domain.progress.entity.Progress;
import com.cinemax.domain.progress.repository.ProgressRepository;
import com.cinemax.domain.task.entity.Task;
import com.cinemax.domain.task.repository.TaskRepository;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.domain.weeklySession.repository.WeeklySessionRepository;
import com.cinemax.global.enums.ActivityAction;
import com.cinemax.global.enums.StudentActivityStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
    private final WeeklySessionRepository weeklySessionRepository;
    private final CurriculumWeekRepository curriculumWeekRepository;
    private final CycleRepository cycleRepository;
    private final TaskRepository taskRepository;
    private final CodeSnapshotRepository codeSnapshotRepository;
    private final ClassSubmitRepository classSubmitRepository;
    private final ClassEnrollRepository classEnrollRepository;

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

    // 모니터링 대시보드 집계 조회
    // 기존에 프론트가 학생마다 반복 호출하던 반→커리큘럼→사이클→과제→최신코드→에러수 체인을 한 번에 조인
    @Override
    public ActivityDashboardResponse getDashboard(Long weeklySessionId, Long classId) {

        List<StudentActivityResponse> activities = getAllStudentActivities(weeklySessionId);

        // 세션 → 반 → 커리큘럼 (세션 단위 1회)
        WeeklySession session = weeklySessionRepository.findById(weeklySessionId)
                .orElseThrow(() -> new IllegalArgumentException("주차 수업을 찾을 수 없습니다. ID: " + weeklySessionId));
        Integer weekNo = session.getWeekNo();
        Long curId = null;
        Long resolvedClassId = classId;
        if (session.getClassInvite() != null && session.getClassInvite().getClassEntity() != null) {
            resolvedClassId = session.getClassInvite().getClassEntity().getClassId();
            if (session.getClassInvite().getClassEntity().getCurriculum() != null) {
                curId = session.getClassInvite().getClassEntity().getCurriculum().getCurId();
            }
        }

        // 주차의 사이클 + 사이클별 과제 (세션 단위 1회)
        final Long finalCurId = curId;
        List<ActivityDashboardResponse.CycleInfo> cycles = List.of();
        if (curId != null && weekNo != null) {
            cycles = curriculumWeekRepository.findByCurIdAndWeekNo(curId, weekNo)
                    .map(CurriculumWeek::getCurWeekId)
                    .map(cycleRepository::findByCurWeekId)
                    .orElse(List.of())
                    .stream()
                    .map(cycle -> ActivityDashboardResponse.CycleInfo.builder()
                            .cycleId(cycle.getCycleId())
                            .cycleTitle(cycle.getCycleTitle())
                            .tasks(taskRepository.findByCurIdAndCycleId(finalCurId, cycle.getCycleId()).stream()
                                    .map(task -> ActivityDashboardResponse.TaskInfo.builder()
                                            .taskId(task.getTaskId())
                                            .taskTitle(task.getTaskTitle())
                                            .taskMode(task.getTaskMode())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build())
                    .collect(Collectors.toList());
        }

        // 학생별 최신 코드 스냅샷 → 과제 제목은 배치 조회
        Map<Long, CodeSnapshot> snapshotByUser = activities.stream()
                .map(activity -> codeSnapshotRepository
                        .findLatestByWeeklySessionAndUser(weeklySessionId, activity.getUserId())
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(CodeSnapshot::getUserId, Function.identity(), (a, b) -> a));

        Map<Long, String> taskTitleById = taskRepository.findAllById(
                        snapshotByUser.values().stream()
                                .map(CodeSnapshot::getTaskId)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(Task::getTaskId, Task::getTaskTitle));

        List<ActivityDashboardResponse.StudentDashboard> students = activities.stream()
                .map(activity -> {
                    CodeSnapshot snapshot = snapshotByUser.get(activity.getUserId());
                    ActivityDashboardResponse.LatestCode latestCode = null;
                    long errorCount = 0;
                    if (snapshot != null) {
                        latestCode = ActivityDashboardResponse.LatestCode.builder()
                                .taskId(snapshot.getTaskId())
                                .cycleId(snapshot.getCycleId())
                                .taskTitle(taskTitleById.get(snapshot.getTaskId()))
                                .content(snapshot.getContent())
                                .saveAt(snapshot.getSaveAt())
                                .build();
                        if (snapshot.getTaskId() != null) {
                            errorCount = getErrorCount(activity.getUserId(), snapshot.getTaskId());
                        }
                    }
                    return ActivityDashboardResponse.StudentDashboard.builder()
                            .activity(activity)
                            .latestCode(latestCode)
                            .errorCount(errorCount)
                            .build();
                })
                .collect(Collectors.toList());

        // 과제 성공률 (result=true 비율, 제출 없으면 0)
        int submissionTotal = 0;
        int submissionSuccess = 0;
        if (resolvedClassId != null) {
            List<ClassSubmit> submissions = classSubmitRepository.findAllByWeeklySession(weeklySessionId, resolvedClassId);
            submissionTotal = submissions.size();
            submissionSuccess = (int) submissions.stream()
                    .filter(submit -> Boolean.TRUE.equals(submit.getResult()))
                    .count();
        }
        int totalStudents = resolvedClassId != null
                ? classEnrollRepository.countActiveEnrollmentsByClassId(resolvedClassId).intValue()
                : 0;

        int successRate = submissionTotal > 0
                ? (int) Math.round(submissionSuccess * 100.0 / submissionTotal)
                : 0;

        return ActivityDashboardResponse.builder()
                .weeklySessionId(weeklySessionId)
                .classId(resolvedClassId)
                .curId(curId)
                .weekNo(weekNo)
                .cycles(cycles)
                .students(students)
                .totalStudents(totalStudents)
                .successRate(successRate)
                .submissionTotal(submissionTotal)
                .submissionSuccess(submissionSuccess)
                .build();
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
