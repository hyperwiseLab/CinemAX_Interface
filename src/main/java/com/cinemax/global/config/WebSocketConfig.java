package com.cinemax.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정
 * 실시간 코드 모니터링을 위한 STOMP over WebSocket 설정
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커 설정
     * - /topic: 1:N 브로드캐스트 (교수가 전체 학생 모니터링)
     * - /app: 클라이언트에서 서버로 메시지 전송 시 사용할 prefix
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 구독 경로 prefix 설정
        config.enableSimpleBroker("/topic", "/queue");

        // 클라이언트에서 메시지 전송 시 prefix 설정
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * WebSocket 엔드포인트 설정
     * 클라이언트가 WebSocket 연결을 맺을 경로 설정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 엔드포인트 등록
        registry.addEndpoint("/ws/code-monitor")
                .setAllowedOriginPatterns("*") // CORS 설정 (프로덕션에서는 구체적으로 지정)
                .withSockJS(); // SockJS fallback 지원
    }
}
