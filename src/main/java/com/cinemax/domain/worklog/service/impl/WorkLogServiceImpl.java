package com.cinemax.domain.worklog.service.impl;

import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.domain.worklog.dto.CycleScoreRequest;
import com.cinemax.domain.worklog.dto.ProfessorFeedbackRequest;
import com.cinemax.domain.worklog.dto.WeeklyFeedbackResponse;
import com.cinemax.domain.worklog.dto.WorkHourStatisticsResponse;
import com.cinemax.domain.worklog.dto.WorkLogRequest;
import com.cinemax.domain.worklog.dto.WorkLogResponse;
import com.cinemax.domain.worklog.dto.WorkLogStatisticsResponse;
import com.cinemax.domain.worklog.entity.WorkLog;
import com.cinemax.domain.worklog.entity.WorkLogCycleScore;
import com.cinemax.domain.worklog.mapper.WorkLogMapper;
import com.cinemax.domain.worklog.repository.WorkLogRepository;
import com.cinemax.domain.worklog.service.WorkLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 업무일지 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogRepository workLogRepository;
    private final UserRepository userRepository;
    private final WorkLogMapper workLogMapper;


    // 업무일지 생성
    @Override
    @Transactional
    public WorkLogResponse createWorkLog(WorkLogRequest request) {
        log.info("업무일지 생성 요청 - userId: {}, weeklySessionId: {}, logDate: {}",
                request.getUserId(), request.getWeeklySessionId(), request.getLogDate());

        // 업무일지가 이미 존재하는지 확인
        if (existsWorkLog(request.getUserId(), request.getWeeklySessionId(), request.getLogDate())) {
            throw new IllegalStateException("해당 날짜에 이미 업무일지가 존재합니다.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        WorkLog workLog = WorkLog.create(request.getUserId(), request.getWeeklySessionId(), request.getLogDate(), user,
                                         request.getContent(), request.getWorkHours(), request.getAchievements());

        // 새로운 필드들 설정 (proficiencyLevel, notes, meaningfulContent, difficultContent, questionContent)
        workLog.update(
                request.getContent(),
                request.getWorkHours(),
                request.getAchievements(),
                request.getDifficultyLevel(),
                request.getProficiencyLevel(),
                request.getNotes(),
                request.getMeaningfulContent(),
                request.getDifficultContent(),
                request.getQuestionContent()
        );

        applyCycleScores(workLog, request.getCycleScores());

        WorkLog savedWorkLog = workLogRepository.save(workLog);
        log.info("업무일지 생성 완료 - workLogId: {}, userId: {}, weeklySessionId: {}",
                savedWorkLog.getWorkLogId(), savedWorkLog.getUserId(), savedWorkLog.getWeeklySessionId());

        return workLogMapper.toDto(savedWorkLog);
    }

    // 업무일지 수정
    @Override
    @Transactional
    public WorkLogResponse updateWorkLog(WorkLogRequest request) {

        WorkLog workLog = workLogRepository.findByUserIdAndWeeklySessionIdAndLogDate(
                request.getUserId(), request.getWeeklySessionId(), request.getLogDate())
                .orElseThrow(() -> new IllegalArgumentException("업무일지를 찾을 수 없습니다."));

        workLog.update(
                request.getContent(),
                request.getWorkHours(),
                request.getAchievements(),
                request.getDifficultyLevel(),
                request.getProficiencyLevel(),
                request.getNotes(),
                request.getMeaningfulContent(),
                request.getDifficultContent(),
                request.getQuestionContent()
        );

        applyCycleScores(workLog, request.getCycleScores());

        WorkLog updatedWorkLog = workLogRepository.save(workLog);

        return workLogMapper.toDto(updatedWorkLog);
    }

    /**
     * 사이클별 점수를 저장하고 주차 단위 점수(difficultyLevel/proficiencyLevel)를 그 평균으로 다시 계산한다.
     *
     * 평균을 클라이언트가 보낸 값 대신 서버가 파생시키는 이유:
     * 원본(사이클별)과 요약(주차 평균)이 어긋나면 화면마다 다른 점수가 보이기 때문이다.
     * 요청에 사이클 점수가 없으면(구버전 클라이언트) 기존 동작 그대로 둔다.
     */
    private void applyCycleScores(WorkLog workLog, List<CycleScoreRequest> cycleScores) {
        if (cycleScores == null || cycleScores.isEmpty()) {
            return;
        }

        List<WorkLogCycleScore> scores = new ArrayList<>();
        int order = 1;
        for (CycleScoreRequest req : cycleScores) {
            scores.add(WorkLogCycleScore.create(
                    req.getCycleId(),
                    req.getConceptScore(),
                    req.getApplicationScore(),
                    req.getOrderNo() != null ? req.getOrderNo() : order
            ));
            order++;
        }

        workLog.replaceCycleScores(scores);
        workLog.recalculateLevelsFromCycleScores();
    }

    // 업무일지 조회
    @Override
    public WorkLogResponse getWorkLog(Long userId, Long weeklySessionId, LocalDate logDate) {

        WorkLog workLog = workLogRepository.findByUserIdAndWeeklySessionIdAndLogDate(userId, weeklySessionId, logDate)
                .orElseThrow(() -> new IllegalArgumentException("업무일지를 찾을 수 없습니다."));

        return workLogMapper.toDto(workLog);
    }

    // 사용자의 주차별 업무일지 목록 조회
    @Override
    public List<WorkLogResponse> getWorkLogsByUserAndWeeklySession(Long userId, Long weeklySessionId) {
        log.info("업무일지 목록 조회 요청 - userId: {}, weeklySessionId: {}", userId, weeklySessionId);

        List<WorkLog> workLogs = workLogRepository.findByUserIdAndWeeklySessionId(userId, weeklySessionId);
        log.info("업무일지 목록 조회 결과 - userId: {}, weeklySessionId: {}, 결과 수: {}",
                userId, weeklySessionId, workLogs.size());

        return workLogMapper.toDto(workLogs);
    }

    // 사용자의 모든 업무일지 조회
    @Override
    public List<WorkLogResponse> getWorkLogsByUser(Long userId) {

        List<WorkLog> workLogs = workLogRepository.findByUserId(userId);

        return workLogMapper.toDto(workLogs);
    }

    // inviteId 기반 전체 업무일지 조회
    @Override
    public List<WorkLogResponse> getMyAllWorkLogsByInviteId(Long userId, Long inviteId) {
        log.info("inviteId 기반 업무일지 조회 요청 - userId: {}, inviteId: {}", userId, inviteId);

        List<WorkLog> workLogs = workLogRepository.findByUserIdAndInviteId(userId, inviteId);

        log.info("inviteId 기반 업무일지 조회 결과 - userId: {}, inviteId: {}, 결과 수: {}",
                userId, inviteId, workLogs.size());

        return workLogMapper.toDto(workLogs);
    }

    // 주차 별 수업의 모든 업무일지 조회
    @Override
    public List<WorkLogResponse> getWorkLogsByWeeklySession(Long weeklySessionId) {

        List<WorkLog> workLogs = workLogRepository.findByWeeklySessionId(weeklySessionId);

        return workLogMapper.toDto(workLogs);
    }

    // 기간 별 업무일지 조회
    @Override
    public List<WorkLogResponse> getWorkLogsByDateRange(Long userId, Long weeklySessionId, 
                                                         LocalDate startDate, LocalDate endDate) {

        List<WorkLog> workLogs = workLogRepository.findByUserIdAndWeeklySessionIdAndDateBetween(
                userId, weeklySessionId, startDate, endDate);

        return workLogMapper.toDto(workLogs);
    }

    // 업무일지 통계 조회
    @Override
    public WorkLogStatisticsResponse getStatistics(Long userId, Long weeklySessionId) {

        List<WorkLog> workLogs = workLogRepository.findByUserIdAndWeeklySessionId(userId, weeklySessionId);

        if (workLogs.isEmpty()) {
            return WorkLogStatisticsResponse.builder()
                    .userId(userId)
                    .weeklySessionId(weeklySessionId)
                    .totalLogCount(0L)
                    .totalWorkHours(BigDecimal.ZERO)
                    .averageWorkHours(BigDecimal.ZERO)
                    .averageDifficulty(BigDecimal.ZERO)
                    .weeklyLogCount(0L)
                    .weeklyWorkHours(BigDecimal.ZERO)
                    .build();
        }

        // 전체 통계
        long totalLogCount = workLogs.size();
        BigDecimal totalWorkHours = workLogs.stream()
                .map(WorkLog::getWorkHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageWorkHours = totalWorkHours.divide(BigDecimal.valueOf(totalLogCount), 2, RoundingMode.HALF_UP);

        BigDecimal averageDifficulty = workLogs.stream()
                .map(w -> BigDecimal.valueOf(w.getDifficultyLevel() != null ? w.getDifficultyLevel() : 3))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(totalLogCount), 2, RoundingMode.HALF_UP);

        // 주간 통계 (최근 7일)
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        List<WorkLog> weeklyLogs = workLogs.stream()
                .filter(w -> !w.getLogDate().isBefore(weekAgo))
                .toList();

        long weeklyLogCount = weeklyLogs.size();
        BigDecimal weeklyWorkHours = weeklyLogs.stream()
                .map(WorkLog::getWorkHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 최근 로그 정보
        WorkLog latestLog = workLogs.get(0);
        Integer weekNo = latestLog.getWeeklySession() != null ? latestLog.getWeeklySession().getWeekNo() : null;

        return WorkLogStatisticsResponse.builder()
                .userId(userId)
                .weeklySessionId(weeklySessionId)
                .weekNo(weekNo)
                .totalLogCount(totalLogCount)
                .totalWorkHours(totalWorkHours)
                .averageWorkHours(averageWorkHours)
                .averageDifficulty(averageDifficulty)
                .weeklyLogCount(weeklyLogCount)
                .weeklyWorkHours(weeklyWorkHours)
                .latestContent(latestLog.getContent())
                .latestAchievements(latestLog.getAchievements())
                .build();
    }

    // 업무일지 삭제
    @Override
    @Transactional
    public void deleteWorkLog(Long userId, Long weeklySessionId, LocalDate logDate) {

        WorkLog workLog = workLogRepository.findByUserIdAndWeeklySessionIdAndLogDate(userId, weeklySessionId, logDate)
                .orElseThrow(() -> new IllegalArgumentException("업무일지를 찾을 수 없습니다."));

        workLogRepository.delete(workLog);
    }

    @Override
    public boolean existsWorkLog(Long userId, Long weeklySessionId, LocalDate logDate) {
        return workLogRepository.existsByUserIdAndWeeklySessionIdAndLogDate(userId, weeklySessionId, logDate);
    }

    // 특정 사용자의 특정 주차 학습 시간 통계 조회
    @Override
    public WorkHourStatisticsResponse getWorkHourStatisticsByUser(Long userId, Long weeklySessionId) {

        BigDecimal totalHours = workLogRepository.sumWorkHoursByUserIdAndWeeklySessionId(userId, weeklySessionId);

        WorkHourStatisticsResponse response = WorkHourStatisticsResponse.of(
                totalHours,
                totalHours,
                totalHours,
                totalHours
        );

        return response;
    }

    // 특정 주차의 전체 학생 학습 시간 통계 조회 (교수용)
    @Override
    public WorkHourStatisticsResponse getWorkHourStatisticsByWeeklySession(Long weeklySessionId) {

        BigDecimal totalHours = workLogRepository.sumWorkHoursByWeeklySessionId(weeklySessionId);
        BigDecimal avgHours = workLogRepository.avgWorkHoursByWeeklySessionId(weeklySessionId);
        BigDecimal maxHours = workLogRepository.maxWorkHoursByWeeklySessionId(weeklySessionId);
        BigDecimal minHours = workLogRepository.minWorkHoursByWeeklySessionId(weeklySessionId);

        WorkHourStatisticsResponse response = WorkHourStatisticsResponse.of(
                totalHours,
                avgHours,
                maxHours,
                minHours
        );

        return response;
    }

    // 교수 피드백 추가
    @Override
    @Transactional
    public WorkLogResponse addProfessorFeedback(Long userId, Long weeklySessionId, LocalDate logDate, ProfessorFeedbackRequest request) {
        WorkLog workLog = workLogRepository.findByUserIdAndWeeklySessionIdAndLogDate(userId, weeklySessionId, logDate)
                .orElseThrow(() -> new IllegalArgumentException("업무일지를 찾을 수 없습니다."));

        workLog.addProfessorFeedback(request.getFeedback(), request.getScore());
        WorkLog saved = workLogRepository.save(workLog);

        return workLogMapper.toDto(saved);
    }

    // 교수 피드백 수정
    @Override
    @Transactional
    public WorkLogResponse updateProfessorFeedback(Long userId, Long weeklySessionId, LocalDate logDate, ProfessorFeedbackRequest request) {
        WorkLog workLog = workLogRepository.findByUserIdAndWeeklySessionIdAndLogDate(userId, weeklySessionId, logDate)
                .orElseThrow(() -> new IllegalArgumentException("업무일지를 찾을 수 없습니다."));

        workLog.updateProfessorFeedback(request.getFeedback(), request.getScore());
        WorkLog saved = workLogRepository.save(workLog);

        return workLogMapper.toDto(saved);
    }

    // Week별 피드백 조회
    @Override
    public WeeklyFeedbackResponse getWeeklyFeedback(Long userId, Integer weekNo) {
        List<Object[]> results = workLogRepository.findWeeklyFeedbackByUserIdAndWeekNo(userId, weekNo);

        List<WeeklyFeedbackResponse.FeedbackDetail> feedbacks = results.stream()
                .map(result -> WeeklyFeedbackResponse.FeedbackDetail.builder()
                        .workLogId(((Number) result[0]).longValue())
                        .logDate((LocalDate) result[1])
                        .meaningfulContent((String) result[2])
                        .difficultContent((String) result[3])
                        .questionContent((String) result[4])
                        .build())
                .collect(Collectors.toList());

        return WeeklyFeedbackResponse.builder()
                .weekNo(weekNo)
                .feedbacks(feedbacks)
                .build();
    }

}
