package com.cinemax.domain.codesnapshot.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.codesnapshot.dto.CodePeekPenaltyRequest;
import com.cinemax.domain.codesnapshot.dto.CodePeekPenaltyResponse;
import com.cinemax.domain.codesnapshot.entity.CodePeekPenalty;
import com.cinemax.domain.codesnapshot.repository.CodePeekPenaltyRepository;
import com.cinemax.domain.codesnapshot.service.CodePeekPenaltyService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.codesnapshot.dto.CodePeekPenaltyMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.domain.weeklySession.repository.WeeklySessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 코드 중간 검사 제재 서비스 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodePeekPenaltyServiceImpl implements CodePeekPenaltyService {

    private final CodePeekPenaltyRepository codePeekPenaltyRepository;
    private final UserRepository userRepository;
    private final WeeklySessionRepository weeklySessionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 제재 기록 생성 또는 중간 검사 횟수 증가
    @Override
    @Transactional
    public CodePeekPenaltyResponse recordPeek(CodePeekPenaltyRequest request) {

        // 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", request.getUserId()));

        // 주차 세션 조회
        WeeklySession weeklySession = weeklySessionRepository.findById(request.getWeeklySessionId())
                .orElseThrow(() -> new ResourceNotFoundException("WeeklySession", "weeklySessionId", request.getWeeklySessionId()));

        // 기존 제재 기록 조회 또는 생성
        CodePeekPenalty penalty = codePeekPenaltyRepository
                .findByUserIdAndWeeklySessionIdAndInviteId(request.getUserId(), request.getWeeklySessionId(), request.getInviteId())
                .orElseGet(() -> CodePeekPenalty.create(
                        request.getUserId(),
                        request.getWeeklySessionId(),
                        request.getInviteId(),
                        user,
                        weeklySession
                ));

        // 엿보기 횟수 증가 및 제재 점수 계산
        penalty.incrementPeek();
        CodePeekPenalty saved = codePeekPenaltyRepository.save(penalty);

        // 브로드캐스트: 증가 이벤트
        broadcastPenaltyUpdate(saved, "INCREMENT");

        return CodePeekPenaltyResponse.from(saved);
    }

    /**
     * 제재 기록 조회
     */
    @Override
    public CodePeekPenaltyResponse getPenalty(Long userId, Long weeklySessionId, Long inviteId) {

        CodePeekPenalty penalty = codePeekPenaltyRepository.findByUserIdAndWeeklySessionIdAndInviteId(userId, weeklySessionId, inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("CodePeekPenalty not found"));

        return CodePeekPenaltyResponse.from(penalty);
    }

    // 사용자 ID로 제재 목록 조회
    @Override
    public List<CodePeekPenaltyResponse> getPenaltiesByUserId(Long userId) {

        List<CodePeekPenalty> penalties = codePeekPenaltyRepository.findByUserId(userId);

        return penalties.stream()
                .map(CodePeekPenaltyResponse::from)
                .collect(Collectors.toList());
    }

    // 주차 세션별 제재 목록 조회
    @Override
    public List<CodePeekPenaltyResponse> getPenaltiesByWeeklySession(Long weeklySessionId, Long inviteId) {

        List<CodePeekPenalty> penalties = codePeekPenaltyRepository.findByWeeklySessionIdAndInviteId(weeklySessionId, inviteId);

        return penalties.stream()
                .map(CodePeekPenaltyResponse::from)
                .collect(Collectors.toList());
    }

    // 주차 세션별 제재 점수 상위 목록 조회
    @Override
    public List<CodePeekPenaltyResponse> getTopPenaltiesByWeeklySession(Long weeklySessionId, Long inviteId) {

        List<CodePeekPenalty> penalties = codePeekPenaltyRepository.findByWeeklySessionIdAndInviteIdOrderByPenaltyPointsDesc(weeklySessionId, inviteId);

        return penalties.stream()
                .map(CodePeekPenaltyResponse::from)
                .collect(Collectors.toList());
    }

    // 제재 점수가 특정 값 이상인 사용자 조회
    @Override
    public List<CodePeekPenaltyResponse> getPenaltiesByMinPoints(Integer minPoints) {

        List<CodePeekPenalty> penalties = codePeekPenaltyRepository.findByPenaltyPointsGreaterThanEqual(minPoints);

        return penalties.stream()
                .map(CodePeekPenaltyResponse::from)
                .collect(Collectors.toList());
    }

    // 제재 기록 초기화
    @Override
    @Transactional
    public void resetPenalty(Long userId, Long weeklySessionId, Long inviteId) {

        CodePeekPenalty penalty = codePeekPenaltyRepository.findByUserIdAndWeeklySessionIdAndInviteId(userId, weeklySessionId, inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("CodePeekPenalty not found"));

        penalty.resetPeekCount();
        CodePeekPenalty saved = codePeekPenaltyRepository.save(penalty);

        // 브로드캐스트: 초기화 이벤트
        broadcastPenaltyUpdate(saved, "RESET");
    }

    // 제재 기록 삭제
    @Override
    @Transactional
    public void deletePenalty(Long userId, Long weeklySessionId, Long inviteId) {

        CodePeekPenalty penalty = codePeekPenaltyRepository.findByUserIdAndWeeklySessionIdAndInviteId(userId, weeklySessionId, inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("CodePeekPenalty not found"));

        codePeekPenaltyRepository.delete(penalty);

        // 브로드캐스트: 삭제 이벤트
        broadcastPenaltyUpdate(penalty, "DELETE");
    }

    // WebSocket 브로드캐스트 헬퍼
    private void broadcastPenaltyUpdate(CodePeekPenalty penalty, String eventType) {
        try {
            CodePeekPenaltyMessage message = CodePeekPenaltyMessage.from(penalty, eventType);

            // 1) 세션 전체 채널: /topic/code-peek-penalty/{weeklySessionId}
            String sessionDestination = String.format("/topic/code-peek-penalty/%d",
                    penalty.getWeeklySessionId());
            messagingTemplate.convertAndSend(sessionDestination, message);

            // 2) 특정 사용자 채널: /topic/code-peek-penalty/{weeklySessionId}/{userId}
            String userDestination = String.format("/topic/code-peek-penalty/%d/%d",
                    penalty.getWeeklySessionId(), penalty.getUserId());
            messagingTemplate.convertAndSend(userDestination, message);

            log.info("WebSocket CodePeekPenalty 메시지 전송 - type: {}, dest1: {}, dest2: {}",
                    eventType, sessionDestination, userDestination);
        } catch (Exception e) {
            log.error("WebSocket CodePeekPenalty 메시지 전송 실패 - userId: {}, weeklySessionId: {}",
                    penalty.getUserId(), penalty.getWeeklySessionId(), e);
        }
    }
}
