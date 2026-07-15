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

import java.util.ArrayList;
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

        // 1차: 배열 통째 파싱
        try {
            return objectMapper.readValue(json, new TypeReference<List<GeneratedQuestion>>() {});
        } catch (Exception e) {
            log.warn("퀴즈 AI 응답 배열 파싱 실패 - 문항 단위 복구 시도. cause={}", e.getMessage());
        }

        // 2차: 문항({ ... })을 하나씩 잘라 개별 파싱 (일부 깨져도 나머지 살림)
        List<GeneratedQuestion> recovered = recoverQuestions(json);
        if (!recovered.isEmpty()) {
            log.info("퀴즈 AI 응답 문항 단위 복구 성공: {}개", recovered.size());
            return recovered;
        }

        log.error("퀴즈 AI 응답 파싱 완전 실패. raw={}", raw);
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                "AI가 생성한 퀴즈 형식을 해석하지 못했습니다.");
    }

    // JSON 배열 문자열에서 최상위 { ... } 객체들을 하나씩 분리해 개별 파싱한다.
    private List<GeneratedQuestion> recoverQuestions(String json) {
        List<GeneratedQuestion> result = new ArrayList<>();
        int depth = 0;
        int start = -1;
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }
            if (c == '"') {
                inString = true;
            } else if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    String objJson = sanitizeObject(json.substring(start, i + 1));
                    try {
                        result.add(objectMapper.readValue(objJson, GeneratedQuestion.class));
                    } catch (Exception ex) {
                        log.warn("문항 개별 파싱 실패 - 건너뜀: {}", ex.getMessage());
                    }
                    start = -1;
                }
            }
        }
        return result;
    }

    // LLM 이 흔히 넣는 오류를 보정: 백틱으로 감싼 값, 스마트 따옴표 등
    private String sanitizeObject(String obj) {
        return obj
                // 값 자리를 백틱으로 감싼 경우 큰따옴표로 교체: : `...` -> : "..."
                .replaceAll(":\\s*`([^`]*)`", ": \"$1\"")
                // 남은 백틱 제거
                .replace("`", "")
                // 스마트 따옴표 정규화
                .replace("“", "\"").replace("”", "\"")
                .replace("‘", "'").replace("’", "'");
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

                JSON 형식 규칙 (매우 중요, 반드시 지킬 것):
                - 모든 문자열은 큰따옴표(")로만 감싸세요. 백틱(`)이나 작은따옴표(')로 문자열을 감싸지 마세요.
                - 문자열 값 안에서 코드나 함수명을 언급할 때 백틱(`)을 쓰지 말고 그냥 일반 텍스트로 쓰세요. 예: range(n) 은 0부터...
                - 불필요한 역슬래시 이스케이프(\\")를 넣지 마세요. 일반 큰따옴표만 쓰세요.
                - 순수하고 유효한 JSON만 출력하세요.

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
