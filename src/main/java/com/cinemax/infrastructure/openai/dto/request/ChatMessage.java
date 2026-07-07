package com.cinemax.infrastructure.openai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 채팅 메시지 내부 클래스
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    // 역할 (user, assistant, system)
    private String role;

    // 메시지 내용
    private String content;
}