package com.cinemax.domain.quiz.service;

import com.cinemax.core.exception.BusinessException;
import com.cinemax.core.error.enums.ErrorCode;
import com.cinemax.domain.quiz.dto.GeneratedQuestion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cinemax.infrastructure.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 시나리오 텍스트 + 문제 개수/유형을 받아 AI로 문항을 생성하고 파싱한다.
 * OpenAiService.generate() 를 사용하고, JSON 응답을 자체 파싱한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuizAiGenerator {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    /**
     * 한 주차의 시나리오로 객관식 multipleCount개 + O/X oxCount개를 생성.
     * 재료가 비었거나 개수가 0이면 빈 목록.
     */
    public List<GeneratedQuestion> generate(String scenarioText, int multipleCount, int oxCount) {
        if ((multipleCount <= 0 && oxCount <= 0) || scenarioText == null || scenarioText.isBlank()) {
            return Collections.emptyList();
        }

        String prompt = buildPrompt(scenarioText, multipleCount, oxCount);
        String raw = openAiService.generate(prompt); // ChatModel null 시 OpenAiException 발생
        String json = extractJson(raw);

        try {
            return objectMapper.readValue(json, new TypeReference<List<GeneratedQuestion>>() {});
        } catch (Exception e) {
            log.error("퀴즈 AI 응답 파싱 실패. raw={}", raw, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "AI가 생성한 퀴즈 형식을 해석하지 못했습니다.");
        }
    }

    private String buildPrompt(String scenarioText, int multipleCount, int oxCount) {
        return """
                당신은 프로그래밍 교육용 퀴즈 출제자입니다.
                아래 [학습 시나리오]를 바탕으로 학습 내용을 점검하는 퀴즈를 만드세요.

                요구 사항:
                - 객관식(4지선다) %d문항, O/X %d문항을 만드세요.
                - 시나리오에 실제로 등장한 개념만 사용하고, 지어내지 마세요.
                - 각 문항에는 해설(explanation)을 반드시 포함하세요.
                - 반드시 아래 JSON 배열 형식으로만 응답하세요. 다른 설명 텍스트는 절대 넣지 마세요.

                JSON 스키마 (배열):
                [
                  {
                    "type": "MULTIPLE",
                    "content": "문제 지문",
                    "options": ["보기1", "보기2", "보기3", "보기4"],
                    "answerIndex": 0,
                    "explanation": "해설"
                  },
                  {
                    "type": "OX",
                    "content": "문제 지문",
                    "answer": "O",
                    "explanation": "해설"
                  }
                ]

                [학습 시나리오]
                %s
                """.formatted(multipleCount, oxCount, scenarioText);
    }

    /**
     * 마크다운 코드펜스 제거 후 첫 '[' ~ 마지막 ']' 범위를 JSON 배열로 슬라이싱.
     */
    private String extractJson(String content) {
        if (content == null) {
            return "[]";
        }
        String cleaned = content
                .replaceAll("(?s)```json", "")
                .replaceAll("(?s)```", "")
                .trim();
        int start = cleaned.indexOf('[');
        int end = cleaned.lastIndexOf(']');
        if (start >= 0 && end > start) {
            return cleaned.substring(start, end + 1);
        }
        return cleaned;
    }
}
