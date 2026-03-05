package com.cinemax.domain.progress.service.impl;

import com.cinemax.domain.progress.dto.ProgressRequest;
import com.cinemax.domain.progress.dto.ProgressResponse;
import com.cinemax.domain.progress.dto.ProgressStatisticsResponse;
import com.cinemax.domain.progress.entity.Progress;
import com.cinemax.domain.progress.repository.ProgressRepository;
import com.cinemax.domain.progress.service.ProgressService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.global.enums.StudentActivityStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 진도 관리 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ProgressResponse createProgress(ProgressRequest request) {

        // 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 존재하는지 확인
        if (existsProgress(request.getWeeklySessionId(), request.getUserId())) {
            // 이미 존재하면 mode만 업데이트
            Progress existingProgress = progressRepository.findByWeeklySessionIdAndUserId(
                    request.getWeeklySessionId(), request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다."));

            existingProgress.updateMode(request.getMode());
            Progress savedProgress = progressRepository.save(existingProgress);

            return ProgressResponse.from(savedProgress);
        }

        // Progress 생성
        Progress progress = Progress.create(
                request.getClassId(),
                request.getWeeklySessionId(),
                user,
                request.getProgressPct(),
                request.getCycleCount(),
                request.getMode()
        );

        Progress savedProgress = progressRepository.save(progress);

        return ProgressResponse.from(savedProgress);
    }

    // 진도 조회
    @Override
    public ProgressResponse getProgress(Long weeklySessionId, Long userId) {

        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다."));

        return ProgressResponse.from(progress);
    }

    // 주차별 수업 모든 진도 조회
    @Override
    public List<ProgressResponse> getAllProgressByWeeklySession(Long weeklySessionId) {

        List<Progress> progresses = progressRepository.findAllByWeeklySessionId(weeklySessionId);

        return progresses.stream()
                .map(ProgressResponse::from)
                .collect(Collectors.toList());
    }

    // 수업 모든 진도 조회
    @Override
    public List<ProgressResponse> getAllProgressByClass(Long classId) {

        List<Progress> progresses = progressRepository.findAllByClassId(classId);

        return progresses.stream()
                .map(ProgressResponse::from)
                .collect(Collectors.toList());
    }

    // 진도율 업데이트
    @Override
    @Transactional
    public ProgressResponse updateProgressPct(Long weeklySessionId, Long userId, BigDecimal progressPct) {

        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다."));

        progress.updateProgress(progressPct);

        Progress savedProgress = progressRepository.save(progress);

        // 진도 통계 WebSocket 브로드캐스트
        broadcastProgressStatistics(weeklySessionId);

        return ProgressResponse.from(savedProgress);
    }

    // 진도 완료 처리
    @Override
    @Transactional
    public ProgressResponse markAsCompleted(Long weeklySessionId, Long userId) {

        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다."));

        progress.markAsCompleted();

        Progress savedProgress = progressRepository.save(progress);

        // 진도 통계 WebSocket 브로드캐스트
        broadcastProgressStatistics(weeklySessionId);

        return ProgressResponse.from(savedProgress);
    }

    // 활동 상태 업데이트
    @Override
    @Transactional
    public ProgressResponse updateActivityStatus(Long weeklySessionId, Long userId, StudentActivityStatus activityStatus) {

        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다."));

        progress.setActivityStatus(activityStatus);

        Progress savedProgress = progressRepository.save(progress);

        // 진도 통계 WebSocket 브로드캐스트
        broadcastProgressStatistics(weeklySessionId);

        return ProgressResponse.from(savedProgress);
    }

    // 모드 업데이트
    @Override
    @Transactional
    public ProgressResponse updateMode(Long weeklySessionId, Long userId, com.cinemax.global.enums.TaskMode mode) {

        Progress progress = progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("진도 정보를 찾을 수 없습니다."));

        progress.updateMode(mode);

        Progress savedProgress = progressRepository.save(progress);

        return ProgressResponse.from(savedProgress);
    }

    // 진도 통계 조회
    @Override
    public ProgressStatisticsResponse getStatistics(Long weeklySessionId) {

        List<Progress> progresses = progressRepository.findAllByWeeklySessionId(weeklySessionId);

        if (progresses.isEmpty()) {
            return ProgressStatisticsResponse.builder()
                    .weeklySessionId(weeklySessionId)
                    .totalStudents(0)
                    .activeStudents(0)
                    .studentsNeedingHelp(0)
                    .idleStudents(0)
                    .completedStudents(0)
                    .averageProgress(BigDecimal.ZERO)
                    .minProgress(BigDecimal.ZERO)
                    .maxProgress(BigDecimal.ZERO)
                    .completionRate(BigDecimal.ZERO)
                    .build();
        }

        int totalStudents = progresses.size();
        int activeStudents = (int) progresses.stream()
                .filter(p -> p.getActivityStatus() == StudentActivityStatus.ACTIVE)
                .count();
        int studentsNeedingHelp = (int) progresses.stream()
                .filter(p -> p.getActivityStatus() == StudentActivityStatus.NEED_HELP)
                .count();
        int idleStudents = (int) progresses.stream()
                .filter(p -> p.getActivityStatus() == StudentActivityStatus.IDLE)
                .count();
        int completedStudents = (int) progresses.stream()
                .filter(p -> p.getActivityStatus() == StudentActivityStatus.COMPLETED)
                .count();

        BigDecimal averageProgress = progresses.stream()
                .map(Progress::getProgressPct)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(totalStudents), 2, RoundingMode.HALF_UP);

        BigDecimal minProgress = progresses.stream()
                .map(Progress::getProgressPct)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxProgress = progresses.stream()
                .map(Progress::getProgressPct)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal completionRate = BigDecimal.valueOf(completedStudents)
                .divide(BigDecimal.valueOf(totalStudents), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        Integer weekNo = progresses.get(0).getWeeklySession() != null
                ? progresses.get(0).getWeeklySession().getWeekNo()
                : null;

        return ProgressStatisticsResponse.builder()
                .weeklySessionId(weeklySessionId)
                .weekNo(weekNo)
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .studentsNeedingHelp(studentsNeedingHelp)
                .idleStudents(idleStudents)
                .completedStudents(completedStudents)
                .averageProgress(averageProgress)
                .minProgress(minProgress)
                .maxProgress(maxProgress)
                .completionRate(completionRate)
                .build();
    }

    // 학생 전체 진도 조회
    @Override
    public List<ProgressResponse> getStudentAllProgress(Long userId) {

        List<Progress> progresses = progressRepository.findAllByUserId(userId);

        return progresses.stream()
                .map(ProgressResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsProgress(Long weeklySessionId, Long userId) {

        return progressRepository.findByWeeklySessionIdAndUserId(weeklySessionId, userId).isPresent();
    }

    // 진도 삭제
    @Override
    @Transactional
    public void deleteProgress(Long weeklySessionId, Long userId) {

        progressRepository.deleteByWeeklySessionIdAndUserId(weeklySessionId, userId);
    }

    /**
     * WebSocket을 통한 진도 통계 브로드캐스트
     */
    private void broadcastProgressStatistics(Long weeklySessionId) {
        try {
            ProgressStatisticsResponse statistics = getStatistics(weeklySessionId);

            // 주제: /topic/progress-statistics/{weeklySessionId}
            String destination = String.format("/topic/progress-statistics/%d", weeklySessionId);

            messagingTemplate.convertAndSend(destination, statistics);

            log.info("WebSocket 진도 통계 메시지 전송 완료 - destination: {}, 총 학생 수: {}, 평균 진도: {}%",
                    destination, statistics.getTotalStudents(), statistics.getAverageProgress());

        } catch (Exception e) {
            log.error("WebSocket 진도 통계 메시지 전송 실패 - weeklySessionId: {}", weeklySessionId, e);
            // 브로드캐스트 실패가 전체 로직에 영향을 주지 않도록 예외를 삼킴
        }
    }
}
