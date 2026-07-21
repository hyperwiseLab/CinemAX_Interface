package com.cinemax.domain.codesnapshot.controller;

import com.cinemax.domain.codesnapshot.dto.CodeLiveMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * 실시간 코드 타이핑 릴레이 (STOMP).
 * 학생 에디터의 타이핑을 DB 저장 없이 교수 코드보기 화면으로 즉시 중계한다.
 * 수신: /app/code-live/{weeklySessionId} → 발행: /topic/code-live/{weeklySessionId}/{userId}
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CodeLiveRelayController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/code-live/{weeklySessionId}")
    public void relayLiveCode(@DestinationVariable Long weeklySessionId, @Payload CodeLiveMessage message) {
        if (weeklySessionId == null || message == null || message.getUserId() == null) {
            return;
        }
        String destination = String.format("/topic/code-live/%d/%d", weeklySessionId, message.getUserId());
        messagingTemplate.convertAndSend(destination, message);
    }
}
