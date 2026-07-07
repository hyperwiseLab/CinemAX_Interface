package com.cinemax.infrastructure.openai.service;

import com.cinemax.infrastructure.openai.dto.request.ChatMessage;
import com.cinemax.infrastructure.openai.dto.request.OpenAiChatRequest;
import com.cinemax.infrastructure.openai.dto.response.OpenAiChatResponse;
import com.cinemax.infrastructure.openai.dto.response.OpenAiStreamResponse;
import com.cinemax.infrastructure.openai.dto.response.StructuredAnalysisResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * OpenAI 서비스 인터페이스
 */
public interface OpenAiService {

    // 단순 텍스트 생성
    String generate(String prompt);

    // 채팅 요청
    OpenAiChatResponse chat(OpenAiChatRequest request);

    // 스트리밍 채팅 요청
    Flux<OpenAiStreamResponse> chatStream(OpenAiChatRequest request);

    // 컨텍스트 기반 대화
    OpenAiChatResponse chatWithContext(List<ChatMessage> history, String userMessage);

    // 코드 리뷰 생성
    String reviewCode(String code, String language);

    // 과제 피드백 생성
    String generateFeedback(String studentCode, String expectedOutput, String rubric);

    // 구조화된 분석 결과 생성 (JSON 응답)
    StructuredAnalysisResponse generateStructuredAnalysis(String studentCode, String expectedOutput, String rubric) throws JsonProcessingException;
}
