package com.cinemax.domain.weeklySession.service.impl;

import com.cinemax.core.exception.BusinessException;
import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.core.error.enums.ErrorCode;
import com.cinemax.domain.classes.entity.ClassInvite;
import com.cinemax.domain.classes.repository.ClassInviteRepository;
import com.cinemax.domain.curriculum.entity.CurriculumWeek;
import com.cinemax.domain.curriculum.repository.CurriculumWeekRepository;
import com.cinemax.domain.weeklySession.dto.WeeklySessionProgressResponse;
import com.cinemax.domain.weeklySession.dto.WeeklySessionRequest;
import com.cinemax.domain.weeklySession.dto.WeeklySessionResponse;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.domain.weeklySession.mapper.WeeklySessionMapper;
import com.cinemax.domain.weeklySession.repository.WeeklySessionRepository;
import com.cinemax.domain.weeklySession.service.WeeklySessionService;
import com.cinemax.global.enums.WeeklySessionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 주차별 수업 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklySessionServiceImpl implements WeeklySessionService {

    // 메시지는 대부분 상수처리
    private String NOT_FOUND_WEEKLY_SESSION = "주차별 수업을 찾을 수 없습니다. inviteId: ";
    private String STILL_WEEKLY_SESSION = "이미 진행 중인 주차별 수업이 있습니다. 주차: ";
    private String STARTED_WEEKLY_SESSION = " 이미 시작되었거나 종료된 수업입니다. 현재 상태: ";
    private String NOT_STILL_WEEKLY_SESSION = "진행 중인 수업이 아닙니다. 현재 상태: ";

    private final WeeklySessionRepository weeklySessionRepository;
    private final ClassInviteRepository classInviteRepository;
    private final CurriculumWeekRepository curriculumWeekRepository;
    private final WeeklySessionMapper weeklySessionMapper;

    // 주차별 수업 시작 요청
    @Override
    @Transactional
    public WeeklySession startWeeklySession(Long inviteId, Integer weekNo) {
        // 동시 진행 제한 확인 - 이미 진행 중인 수업이 있는지 확인
        WeeklySession existingInProgress = weeklySessionRepository.findInProgressSessionByInviteId(inviteId)
                .orElse(null);

        if (existingInProgress != null) {
            throw new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION, NOT_FOUND_WEEKLY_SESSION + existingInProgress.getWeekNo());
        }

        // 해당 주차별 수업 조회
        WeeklySession weeklySession = weeklySessionRepository.findByInviteIdAndWeekNo(inviteId, weekNo)
                .orElseThrow(() -> new ResourceNotFoundException(STILL_WEEKLY_SESSION + inviteId + ", weekNo: " + weekNo));

        // 상태 확인 - 시작 전 상태인지 확인
        if (weeklySession.getStatus() != WeeklySessionStatus.PENDING) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, STARTED_WEEKLY_SESSION + weeklySession.getStatus().getDescription());
        }

        // 수업 시작
        weeklySession.startSession();
        WeeklySession savedSession = weeklySessionRepository.save(weeklySession);

        return savedSession;
    }

    // 주차별 수업 종료 요청
    @Override
    @Transactional
    public WeeklySession endWeeklySession(Long inviteId, Integer weekNo) {
        // 해당 주차별 수업 조회
        WeeklySession weeklySession = weeklySessionRepository.findByInviteIdAndWeekNo(inviteId, weekNo)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_WEEKLY_SESSION + inviteId + ", weekNo: " + weekNo));

        // 상태 확인 - 진행 중 상태인지 확인
        if (weeklySession.getStatus() != WeeklySessionStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, NOT_STILL_WEEKLY_SESSION + weeklySession.getStatus().getDescription());
        }

        // 수업 종료
        weeklySession.endSession(false); // 수동 종료
        WeeklySession savedSession = weeklySessionRepository.save(weeklySession);

        return savedSession;
    }

    // 만료된 주차 별 수업 자동 종료
    @Override
    @Transactional
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행 (3600초 = 1시간)
    public void autoEndExpiredSessions() {

        // 24시간 전 시간 계산
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);

        // 24시간 이전에 시작된 진행 중인 수업 조회
        List<WeeklySession> expiredSessions = weeklySessionRepository.findInProgressSessionsBeforeTime(cutoffTime);

        for (WeeklySession session : expiredSessions) {
            session.endSession(true); // 자동 종료
            weeklySessionRepository.save(session);
        }
    }

    // 진행 중인 주차 별 수업 조회
    @Override
    public WeeklySession getInProgressSession(String inviteCd) {
        // ClassInvite 조회하여 inviteId 가져오기
        ClassInvite classInvite = classInviteRepository.findByInviteCd(inviteCd)
                .orElseThrow(() -> new ResourceNotFoundException("초대 코드를 찾을 수 없습니다: " + inviteCd));
        Long inviteId = classInvite.getInviteId();
        
        return weeklySessionRepository.findInProgressSessionByInviteId(inviteId)
                .orElse(null);
    }

    // 주차 별 수업 상태 조회
    @Override
    @Transactional
    public WeeklySessionStatus getSessionStatus(Long inviteId, Integer weekNo) {
        WeeklySession weeklySession = weeklySessionRepository.findByInviteIdAndWeekNo(inviteId, weekNo)
                .orElseGet(() -> {
                    // WeeklySession이 없으면 자동 생성 후 다시 조회
                    log.info("WeeklySession이 없어 자동 생성합니다. inviteId={}, weekNo={}", inviteId, weekNo);
                    createWeeklySessionsForInvite(inviteId);
                    return weeklySessionRepository.findByInviteIdAndWeekNo(inviteId, weekNo)
                            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_WEEKLY_SESSION + inviteId + ", weekNo: " + weekNo));
                });

        return weeklySession.getStatus();
    }

    // 모든 주차별 수업 조회
    @Override
    @Transactional
    public List<WeeklySession> getAllSessions(Long inviteId) {
        List<WeeklySession> sessions = weeklySessionRepository.findByInviteIdOrderByWeekNo(inviteId);
        
        // WeeklySession이 없으면 자동 생성
        if (sessions.isEmpty()) {
            log.info("WeeklySession이 없어 자동 생성합니다. inviteId={}", inviteId);
            createWeeklySessionsForInvite(inviteId);
            sessions = weeklySessionRepository.findByInviteIdOrderByWeekNo(inviteId);
        }
        
        return sessions;
    }

    // 상태에 따른 주차 별 수업 조회
    @Override
    public List<WeeklySession> getSessionsByStatus(Long inviteId, WeeklySessionStatus status) {
        return weeklySessionRepository.findByInviteIdAndStatus(inviteId, status);
    }

    // 상태만으로 주차 별 수업 조회 (inviteId 제외)
    @Override
    public List<WeeklySession> getSessionsByStatusOnly(WeeklySessionStatus status) {
        return weeklySessionRepository.findByStatus(status);
    }

    // 주차 별 수업 상세 조회
    @Override
    @Transactional
    public WeeklySession getSession(Long inviteId, Integer weekNo) {
        return weeklySessionRepository.findByInviteIdAndWeekNo(inviteId, weekNo)
                .orElseGet(() -> {
                    // WeeklySession이 없으면 자동 생성 후 다시 조회
                    log.info("WeeklySession이 없어 자동 생성합니다. inviteId={}, weekNo={}", inviteId, weekNo);
                    createWeeklySessionsForInvite(inviteId);
                    return weeklySessionRepository.findByInviteIdAndWeekNo(inviteId, weekNo)
                            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_WEEKLY_SESSION + inviteId + ", weekNo: " + weekNo));
                });
    }

    // 수업 진행률 조회
    @Override
    public double getProgressRate(Long inviteId) {

        Long totalWeeks = weeklySessionRepository.countByInviteId(inviteId);
        Long completedWeeks = weeklySessionRepository.countCompletedSessionsByInviteId(inviteId);

        if (totalWeeks == 0) {
            return 0.0;
        }

        return (double) completedWeeks / totalWeeks * 100;
    }

    // 수업 진행률 상세 조회
    @Override
    public WeeklySessionProgressResponse getProgress(String inviteCd) {

        // ClassInvite 조회하여 inviteId 가져오기
        ClassInvite classInvite = classInviteRepository.findByInviteCd(inviteCd)
                .orElseThrow(() -> new ResourceNotFoundException("초대 코드를 찾을 수 없습니다: " + inviteCd));
        Long inviteId = classInvite.getInviteId();

        List<WeeklySession> allSessions = getAllSessions(inviteId);
        List<WeeklySessionResponse> sessionResponses = allSessions.stream()
                .map(weeklySessionMapper::toResponse)
                .toList();

        int totalWeeks = allSessions.size();
        int completedWeeks = (int) allSessions.stream()
                .filter(session -> session.getStatus() == WeeklySessionStatus.COMPLETED)
                .count();
        int inProgressWeeks = (int) allSessions.stream()
                .filter(session -> session.getStatus() == WeeklySessionStatus.IN_PROGRESS)
                .count();
        int pendingWeeks = (int) allSessions.stream()
                .filter(session -> session.getStatus() == WeeklySessionStatus.PENDING)
                .count();

        return WeeklySessionProgressResponse.of(inviteId, inviteCd, totalWeeks, completedWeeks,
                inProgressWeeks, pendingWeeks, sessionResponses);
    }

    // 초대 코드에 대한 주차별 수업 일괄 생성
    @Override
    @Transactional
    public void createWeeklySessionsForInvite(Long inviteId) {
        // ClassInvite 존재 확인 및 수업 정보 가져오기
        ClassInvite classInvite = classInviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("초대 코드를 찾을 수 없습니다: " + inviteId));

        // 수업의 커리큘럼 ID 가져오기
        Long curId = classInvite.getClassEntity().getCurriculum().getCurId();

        // 해당 커리큘럼의 CurriculumWeek 목록 조회 (주차 순으로 정렬)
        List<CurriculumWeek> curriculumWeeks = curriculumWeekRepository.findByCurIdOrderByWeekNo(curId);

        if (curriculumWeeks.isEmpty()) {
            log.warn("커리큘럼에 주차 정보가 없습니다. curId={}", curId);
            return;
        }

        // 기존 WeeklySession 조회
        List<WeeklySession> existingSessions = weeklySessionRepository.findByInviteIdOrderByWeekNo(inviteId);
        
        // 이미 생성된 주차 번호 추출
        Set<Integer> existingWeekNos = existingSessions.stream()
                .map(WeeklySession::getWeekNo)
                .collect(Collectors.toSet());

        // CurriculumWeek의 week_no를 사용하여 WeeklySession 생성 (기존 것 제외)
        for (CurriculumWeek curriculumWeek : curriculumWeeks) {
            Integer weekNo = curriculumWeek.getWeekNo();
            
            if (!existingWeekNos.contains(weekNo)) {
                WeeklySession weeklySession = WeeklySession.builder()
                        .inviteId(inviteId)
                        .weekNo(weekNo)
                        .status(WeeklySessionStatus.PENDING)
                        .autoClosed(true)
                        .build();
                
                weeklySessionRepository.save(weeklySession);
                log.info("WeeklySession 생성 완료: inviteId={}, weekNo={} (CurriculumWeek에서 가져옴)", inviteId, weekNo);
            } else {
                log.debug("WeeklySession이 이미 존재합니다. inviteId={}, weekNo={}", inviteId, weekNo);
            }
        }
    }
}
