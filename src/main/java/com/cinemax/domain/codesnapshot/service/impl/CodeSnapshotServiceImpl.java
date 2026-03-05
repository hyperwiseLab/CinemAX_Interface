package com.cinemax.domain.codesnapshot.service.impl;

import com.cinemax.domain.codesnapshot.dto.CodeMonitorMessage;
import com.cinemax.domain.codesnapshot.dto.CodeSaveRequest;
import com.cinemax.domain.codesnapshot.dto.CodeSnapshotResponse;
import com.cinemax.domain.codesnapshot.entity.CodeSnapshot;
import com.cinemax.domain.codesnapshot.repository.CodeSnapshotRepository;
import com.cinemax.domain.codesnapshot.service.CodeSnapshotService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.global.enums.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 코드 스냅샷 서비스 구현체
 * 코드 자동저장 및 불러오기 기능 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeSnapshotServiceImpl implements CodeSnapshotService {

    private final CodeSnapshotRepository codeSnapshotRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 코드 저장 시작
    @Override
    @Transactional
    public CodeSnapshotResponse saveCode(CodeSaveRequest request, Long userId) {

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 기존 코드 스냅샷 조회
        Optional<CodeSnapshot> existingSnapshot = codeSnapshotRepository.findLatestByTaskAndUser(
                request.getTaskId(),
                userId,
                request.getWeeklySessionId()
        );

        CodeSnapshot savedSnapshot;
        EventType eventType;

        if (existingSnapshot.isPresent()) {
            CodeSnapshot snapshot = existingSnapshot.get();
            snapshot.updateContent(request.getContent(), LocalDateTime.now());
            savedSnapshot = codeSnapshotRepository.save(snapshot);
            eventType = EventType.CODE_UPDATED;
        } else {
            // 신규 코드 생성
            CodeSnapshot newSnapshot = CodeSnapshot.create(
                    request.getTaskId(),
                    request.getWeeklySessionId(),
                    request.getCycleId(),
                    userId,
                    request.getLang(),
                    request.getContent()
            );
            savedSnapshot = codeSnapshotRepository.save(newSnapshot);
            eventType = EventType.CODE_SAVED;
        }

        // WebSocket을 통한 실시간 브로드캐스트
        broadcastCodeUpdate(savedSnapshot, user, eventType);

        return CodeSnapshotResponse.from(savedSnapshot);
    }

    // 최근 작성된 코드 불러오기
    @Override
    public CodeSnapshotResponse getLatestCode(Long weeklySessionId, Long userId) {
        CodeSnapshot snapshot = codeSnapshotRepository.findLatestByWeeklySessionAndUser(weeklySessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 코드를 찾을 수 없습니다."));

        return CodeSnapshotResponse.from(snapshot);
    }

    // 코드 이력 불러오기
    @Override
    public List<CodeSnapshotResponse> getCodeHistory(Long taskId, Long userId, Long weeklySessionId, Long cycleId) {

        List<CodeSnapshot> snapshots = codeSnapshotRepository.findAllByTaskAndUser(taskId, userId, weeklySessionId);

        return snapshots.stream()
                .map(CodeSnapshotResponse::from)
                .collect(Collectors.toList());
    }

    // 코드 삭제
    @Override
    @Transactional
    public void deleteCode(Long taskId, Long userId, Long weeklySessionId, Long cycleId) {
        codeSnapshotRepository.deleteByTaskIdAndUserIdAndWeeklySessionId(taskId, userId, weeklySessionId);
    }

    // 모든 학생들이 작성한 코드 조회
    @Override
    public List<CodeSnapshotResponse> getAllStudentCodes(Long weeklySessionId, Long taskId) {
        List<CodeSnapshot> snapshots = codeSnapshotRepository.findAllByWeeklySessionAndTask(weeklySessionId, taskId);

        return snapshots.stream()
                .map(CodeSnapshotResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 학생 코드 조회 (userId 필터링)
    @Override
    public List<CodeSnapshotResponse> getStudentCodesByUserId(Long weeklySessionId, Long taskId, Long userId) {
        List<CodeSnapshot> snapshots = codeSnapshotRepository.findAllByWeeklySessionAndTaskAndUser(weeklySessionId, taskId, userId);

        return snapshots.stream()
                .map(CodeSnapshotResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasCode(Long taskId, Long userId, Long weeklySessionId, Long cycleId) {
        return codeSnapshotRepository.findLatestByTaskAndUser(taskId, userId, weeklySessionId).isPresent();
    }

    // 코드 ID 생성 (타임스탬프 기반)
    private Long generateCodeId(Long taskId, Long userId) {
        // 현재 시간 기반 ID 생성 (나노초 포함)
        return System.currentTimeMillis() * 1000 + System.nanoTime() % 1000;
    }

    // WebSocket을 통한 실시간 코드 업데이트 브로드캐스트
    private void broadcastCodeUpdate(CodeSnapshot codeSnapshot, User user, EventType eventType) {
        try {
            // 메시지 생성
            CodeMonitorMessage message = CodeMonitorMessage.from(
                    codeSnapshot,
                    user.getName(),
                    user.getEmail(),
                    eventType
            );

            // 1. 전체 모니터링 채널 브로드캐스트
            // 주제: /topic/code-monitor/{weeklySessionId}/{taskId}
            String monitorDestination = String.format("/topic/code-monitor/%d/%d",
                    codeSnapshot.getWeeklySessionId(),
                    codeSnapshot.getTaskId());

            messagingTemplate.convertAndSend(monitorDestination, message);

            log.info("WebSocket 코드 모니터링 메시지 전송 완료 - destination: {}, userId: {}, eventType: {}",
                    monitorDestination, user.getUserId(), eventType);

            // 2. Code Peek 채널 브로드캐스트 (교수가 특정 학생 코드 구독)
            // 주제: /topic/code-peek/{weeklySessionId}/{userId}
            String codePeekDestination = String.format("/topic/code-peek/%d/%d",
                    codeSnapshot.getWeeklySessionId(),
                    user.getUserId());

            messagingTemplate.convertAndSend(codePeekDestination, message);

            log.info("WebSocket Code Peek 메시지 전송 완료 - destination: {}, userId: {}, taskId: {}",
                    codePeekDestination, user.getUserId(), codeSnapshot.getTaskId());

        } catch (Exception e) {
            log.error("WebSocket 메시지 전송 실패 - userId: {}", user.getUserId(), e);
            // 브로드캐스트 실패가 전체 저장 로직에 영향을 주지 않도록 예외를 삼킴
        }
    }
}
