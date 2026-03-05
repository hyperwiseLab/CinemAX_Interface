package com.cinemax.infrastructure.gemini.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;

import java.time.LocalDateTime;

/**
 * Gemini 스트리밍 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiStreamResponse {

    // 생성된 텍스트 청크
    private String content;

    // 스트림 완료 여부
    private boolean done;

    // 완료 이유
    private String finishReason;

    /**
     * 응답 생성 시간
     */
    private LocalDateTime timestamp;

    /**
     * ChatResponse에서 GeminiStreamResponse로 변환
     */
    public static GeminiStreamResponse from(ChatResponse chatResponse) {
        if (chatResponse == null || chatResponse.getResults().isEmpty()) {
            return GeminiStreamResponse.builder()
                    .content("")
                    .done(true)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        var result = chatResponse.getResult();
        String finishReason = result.getMetadata().getFinishReason();

        return GeminiStreamResponse.builder()
                .content(result.getOutput().getContent())
                .done(finishReason != null && !finishReason.isEmpty())
                .finishReason(finishReason)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
