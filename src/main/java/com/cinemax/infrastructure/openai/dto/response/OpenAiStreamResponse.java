package com.cinemax.infrastructure.openai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;

import java.time.LocalDateTime;

/**
 * OpenAI 스트리밍 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiStreamResponse {

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
     * ChatResponse에서 OpenAiStreamResponse로 변환
     */
    public static OpenAiStreamResponse from(ChatResponse chatResponse) {
        if (chatResponse == null || chatResponse.getResults().isEmpty()) {
            return OpenAiStreamResponse.builder()
                    .content("")
                    .done(true)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        var result = chatResponse.getResult();
        String finishReason = result.getMetadata().getFinishReason();

        return OpenAiStreamResponse.builder()
                .content(result.getOutput().getContent())
                .done(finishReason != null && !finishReason.isEmpty())
                .finishReason(finishReason)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
