package com.cinemax.infrastructure.openai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OpenAI 채팅 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiChatRequest {

    // 사용자 메시지
    @NotBlank(message = "메시지는 필수입니다")
    private String message;

    // 대화 히스토리 (선택사항)
    private List<ChatMessage> history;

    // 시스템 프롬프트 (선택사항)
    private String systemPrompt;

    // 온도 설정 (선택사항, 0.0~2.0)
    private Double temperature;

    // 최대 토큰 수 (선택사항)
    private Integer maxTokens;
}
