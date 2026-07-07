package com.cinemax.infrastructure.openai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * OpenAI 채팅 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiChatResponse {

    // 생성된 텍스트
    private String content;

    // 완료 이유 (STOP, LENGTH, CONTENT_FILTER 등)
    private String finishReason;

    // 사용된 토큰 정보
    private TokenUsageResponse tokenUsage;

    // 모델 이름
    private String model;

    // 응답 생성 시간
    private LocalDateTime timestamp;

    // ChatResponse에서 OpenAiChatResponse로 변환
    public static OpenAiChatResponse from(ChatResponse chatResponse) {
        if (chatResponse == null || chatResponse.getResults().isEmpty()) {
            return OpenAiChatResponse.builder()
                    .content("")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        Generation result = chatResponse.getResult();

        return OpenAiChatResponse.builder()
                .content(result.getOutput().getContent())
                .finishReason(result.getMetadata().getFinishReason())
                .tokenUsage(TokenUsageResponse.from(chatResponse))
                .model(chatResponse.getMetadata() != null ? chatResponse.getMetadata().getModel() : null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
