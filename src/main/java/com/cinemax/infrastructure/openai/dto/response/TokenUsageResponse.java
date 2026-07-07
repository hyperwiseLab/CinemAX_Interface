package com.cinemax.infrastructure.openai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;

/**
 * 토큰 사용량 정보
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenUsageResponse {

    // 프롬프트 토큰 수
    private Integer promptTokens;

    // 생성 토큰 수
    private Integer completionTokens;

    // 전체 토큰 수
    private Integer totalTokens;

    public static TokenUsageResponse from(ChatResponse chatResponse) {
        if (chatResponse == null || chatResponse.getMetadata() == null) {
            return TokenUsageResponse.builder().build();
        }

        var usage = chatResponse.getMetadata().getUsage();
        if (usage == null) {
            return TokenUsageResponse.builder().build();
        }

        return TokenUsageResponse.builder()
                .promptTokens(usage.getPromptTokens().intValue())
                .completionTokens(usage.getGenerationTokens().intValue())
                .totalTokens(usage.getTotalTokens().intValue())
                .build();
    }
}