package com.cinemax.infrastructure.openai.service.impl;

import com.cinemax.infrastructure.openai.dto.request.ChatMessage;
import com.cinemax.infrastructure.openai.dto.request.OpenAiChatRequest;
import com.cinemax.infrastructure.openai.dto.response.OpenAiChatResponse;
import com.cinemax.infrastructure.openai.dto.response.OpenAiStreamResponse;
import com.cinemax.infrastructure.openai.dto.response.StructuredAnalysisResponse;
import com.cinemax.infrastructure.openai.exception.OpenAiException;
import com.cinemax.infrastructure.openai.service.OpenAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OpenAI 서비스 구현체
 */
@Slf4j
@Service
public class OpenAiServiceImpl implements OpenAiService {

    @Autowired(required = false)
    private ChatModel chatModel;

    private final ObjectMapper objectMapper;

    public OpenAiServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private void checkChatModel() {
        if (chatModel == null) {
            throw new OpenAiException("AI 서비스를 사용할 수 없습니다. OpenAI API 키(openai.api-key) 설정을 확인하세요.");
        }
    }

    @Override
    public String generate(String prompt) {
        checkChatModel();
        Prompt chatPrompt = new Prompt(prompt);
        ChatResponse response = chatModel.call(chatPrompt);
        String content = response.getResult().getOutput().getContent();

        return content;
    }

    @Override
    public OpenAiChatResponse chat(OpenAiChatRequest request) {
        checkChatModel();
        List<Message> messages = buildMessages(request);

        // 요청별 옵션 설정 (gpt-5 계열은 temperature 커스텀 값 미지원 → maxCompletionTokens만 적용)
        OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder();
        if (request.getMaxTokens() != null) {
            optionsBuilder.withMaxCompletionTokens(request.getMaxTokens());
        }

        Prompt prompt = new Prompt(messages, optionsBuilder.build());
        ChatResponse response = chatModel.call(prompt);

        OpenAiChatResponse chatResponse = OpenAiChatResponse.from(response);

        return chatResponse;
    }

    @Override
    public Flux<OpenAiStreamResponse> chatStream(OpenAiChatRequest request) {
        checkChatModel();
        List<Message> messages = buildMessages(request);
        Prompt prompt = new Prompt(messages);

        return chatModel.stream(prompt)
                .map(OpenAiStreamResponse::from)
                .doOnComplete(() -> log.debug("Streaming completed"))
                .doOnError(e -> log.error("Streaming error", e));
    }

    @Override
    public OpenAiChatResponse chatWithContext(List<ChatMessage> history, String userMessage) {
        checkChatModel();
        List<Message> messages = new ArrayList<>();

        // 히스토리 추가
        if (history != null && !history.isEmpty()) {
            messages.addAll(history.stream()
                    .map(this::convertToMessage)
                    .collect(Collectors.toList()));
        }

        // 새 사용자 메시지 추가
        messages.add(new UserMessage(userMessage));

        Prompt prompt = new Prompt(messages);
        ChatResponse response = chatModel.call(prompt);

        return OpenAiChatResponse.from(response);
    }

    /*
    프롬프트 먹일 내용
    1. 과제의 의도와 부합한지 ******중요******
    2. 학생이 작성한 코드, 작성한 코드에 대한 컴파일 결과값
    3. 잠재적 버그나 문제점
    4. 결과값에 대한 특정 포맷 컨벤션 부합 일치 여부 확인
    5. 사소한 문법(스페이스바, 단락 처리의 경우) 답변과 비교 분석
     */
    @Override
    public String reviewCode(String code, String language) {
        checkChatModel();
        String promptText = String.format(
                """
                다음 %s 코드를 리뷰해주세요.

                코드:
                ```%s
                %s
                ```

                다음 항목만 간결하게 요약해주세요 (각 항목 1-2줄):
                1. 과제 의도 부합 여부
                2. 주요 버그나 문제점
                3. 핵심 개선 사항

                **반드시 300자 이내로 간결하게 작성해주세요.**
                """,
                language, language, code
        );

        Prompt prompt = new Prompt(promptText);
        ChatResponse response = chatModel.call(prompt);
        String content = response.getResult().getOutput().getContent();

        return content;
    }

    @Override
    public String generateFeedback(String studentCode, String expectedOutput, String rubric) {
        checkChatModel();
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("학생이 작성한 코드에 대한 피드백을 생성해주세요.\n\n");
        promptBuilder.append("학생 코드:\n```\n").append(studentCode).append("\n```\n\n");
        promptBuilder.append("기대 출력:\n").append(expectedOutput).append("\n\n");

        if (rubric != null && !rubric.isBlank()) {
            promptBuilder.append("채점 기준:\n").append(rubric).append("\n\n");
        }

        promptBuilder.append("""
                다음 항목만 간결하게 작성해주세요 (각 항목 1-2줄):
                1. 요구사항 충족 여부
                2. 잘한 점 (핵심 1가지)
                3. 개선 필요 사항 (핵심 1가지)

                **반드시 300자 이내로 간결하게 작성해주세요.**
                """);

        Prompt prompt = new Prompt(promptBuilder.toString());
        ChatResponse response = chatModel.call(prompt);
        String content = response.getResult().getOutput().getContent();

        return content;
    }

    @Override
    public StructuredAnalysisResponse generateStructuredAnalysis(String studentCode, String expectedOutput, String rubric) throws JsonProcessingException {
        checkChatModel();
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("학생이 작성한 코드를 분석하고 **반드시 JSON 형식으로만** 응답해주세요.\n\n");
        promptBuilder.append("학생 코드:\n```\n").append(studentCode).append("\n```\n\n");
        promptBuilder.append("기대 출력:\n").append(expectedOutput).append("\n\n");

        if (rubric != null && !rubric.isBlank()) {
            promptBuilder.append("채점 기준:\n").append(rubric).append("\n\n");
        }

        promptBuilder.append("""
                다음 JSON 형식으로 **정확하게** 응답해주세요. 추가 설명 없이 JSON만 반환하세요:

                {
                  "requirementsMet": true 또는 false (코드가 요구사항을 충족하는지),
                  "codeQualityPass": true 또는 false (코드 품질이 기준을 통과하는지),
                  "hasLogicError": true 또는 false (논리 오류가 있는지),
                  "hasSecurityIssue": true 또는 false (보안 이슈가 있는지),
                  "needsImprovement": true 또는 false (개선이 필요한지),
                  "feedback": "전체 피드백 텍스트",
                  "strengths": "잘한 점",
                  "improvements": "개선이 필요한 점",
                  "advice": "학습 조언"
                }

                주의: JSON 형식만 반환하고, 다른 텍스트나 마크다운은 포함하지 마세요.
                """);

        // gpt-5 계열은 temperature 커스텀 값 미지원 → 전역 옵션(모델 기본 설정) 사용
        Prompt prompt = new Prompt(promptBuilder.toString());
        ChatResponse response = chatModel.call(prompt);

        String content = response.getResult().getOutput().getContent();

        // JSON 파싱 전 전처리 (마크다운 코드 블록 제거)
        String jsonContent = extractJsonFromResponse(content);

        // JSON 파싱
        StructuredAnalysisResponse structuredResponse = objectMapper.readValue(jsonContent, StructuredAnalysisResponse.class);

        return structuredResponse;
    }

    // OpenAI 응답에서 JSON 추출
    private String extractJsonFromResponse(String response) {
        // 마크다운 코드 블록 제거
        String cleaned = response.trim();

        // ```json ... ``` 형식 제거
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        cleaned = cleaned.trim();

        // JSON 객체 시작 찾기
        int jsonStart = cleaned.indexOf('{');
        int jsonEnd = cleaned.lastIndexOf('}');

        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            cleaned = cleaned.substring(jsonStart, jsonEnd + 1);
        }

        return cleaned;
    }

    // OpenAiChatRequest로부터 Message 리스트 생성
    private List<Message> buildMessages(OpenAiChatRequest request) {
        List<Message> messages = new ArrayList<>();

        // 시스템 프롬프트 추가
        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
            messages.add(new SystemMessage(request.getSystemPrompt()));
        }

        // 히스토리 추가
        if (request.getHistory() != null && !request.getHistory().isEmpty()) {
            messages.addAll(request.getHistory().stream()
                    .map(this::convertToMessage)
                    .collect(Collectors.toList()));
        }

        // 사용자 메시지 추가
        messages.add(new UserMessage(request.getMessage()));

        return messages;
    }

    // ChatMessage를 Spring AI Message로 변환
    private Message convertToMessage(ChatMessage chatMessage) {
        return switch (chatMessage.getRole().toLowerCase()) {
            case "system" -> new SystemMessage(chatMessage.getContent());
            case "user" -> new UserMessage(chatMessage.getContent());
            case "assistant" -> new AssistantMessage(chatMessage.getContent());
            default -> new UserMessage(chatMessage.getContent());
        };
    }
}
