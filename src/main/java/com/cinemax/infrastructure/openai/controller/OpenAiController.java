package com.cinemax.infrastructure.openai.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.infrastructure.openai.dto.request.OpenAiChatRequest;
import com.cinemax.infrastructure.openai.dto.response.OpenAiChatResponse;
import com.cinemax.infrastructure.openai.dto.response.OpenAiStreamResponse;
import com.cinemax.infrastructure.openai.dto.response.StructuredAnalysisResponse;
import com.cinemax.infrastructure.openai.service.OpenAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * OpenAI API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/openai")
@RequiredArgsConstructor
@Tag(name = "OpenAI", description = "OpenAI 관련 API")
public class OpenAiController extends BaseController {

    private final OpenAiService openAiService;

    // 단순 텍스트 생성
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "텍스트 생성", description = "프롬프트를 기반으로 텍스트를 생성합니다.")
    public ResponseEntity<ApiResponse<String>> generate(@Parameter(description = "프롬프트") @RequestParam String prompt) {

        String result = openAiService.generate(prompt);

        return success(result, "텍스트 생성 완료");
    }

    // 채팅 요청
    @PostMapping("/chat")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "채팅", description = "OpenAI와 채팅합니다.")
    public ResponseEntity<ApiResponse<OpenAiChatResponse>> chat(@Valid @RequestBody OpenAiChatRequest request) {

        OpenAiChatResponse response = openAiService.chat(request);

        return success(response, "채팅 완료");
    }

    // 스트리밍 채팅 요청
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "스트리밍 채팅", description = "OpenAI와 스트리밍 채팅합니다.")
    public Flux<ServerSentEvent<OpenAiStreamResponse>> chatStream(@Valid @RequestBody OpenAiChatRequest request) {

        return openAiService.chatStream(request)
                .map(response -> ServerSentEvent.<OpenAiStreamResponse>builder()
                        .data(response)
                        .build())
                .doOnComplete(() -> log.info("Streaming completed"))
                .doOnError(e -> log.error("Streaming error", e));
    }

    // 코드 리뷰 생성
    @PostMapping("/code-review")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "코드 리뷰", description = "코드를 리뷰하고 피드백을 제공합니다.")
    public ResponseEntity<ApiResponse<String>> reviewCode(@Parameter(description = "소스 코드") @RequestParam String code,
                                                          @Parameter(description = "프로그래밍 언어") @RequestParam String language) {

        String review = openAiService.reviewCode(code, language);

        return success(review, "코드 리뷰 완료");
    }

    // 과제 피드백 생성
    @PostMapping("/feedback")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN', 'STUDENT')")
    @Operation(summary = "과제 피드백 생성", description = "학생 코드에 대한 피드백을 생성합니다.")
    public ResponseEntity<ApiResponse<String>> generateFeedback(@Parameter(description = "학생 코드") @RequestParam String studentCode,
                                                                @Parameter(description = "기대 출력") @RequestParam String expectedOutput,
                                                                @Parameter(description = "채점 기준 (선택)") @RequestParam(required = false) String rubric) {

        String feedback = openAiService.generateFeedback(studentCode, expectedOutput, rubric);

        return success(feedback, "피드백 생성 완료");
    }

    // 구조화된 분석 결과 생성 (JSON 응답)
    @PostMapping("/structured-analysis")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN', 'STUDENT')")
    @Operation(summary = "구조화된 분석 결과 생성", description = "학생 코드에 대한 구조화된 분석 결과를 JSON 형식으로 생성합니다.")
    public ResponseEntity<ApiResponse<StructuredAnalysisResponse>> generateStructuredAnalysis(@Parameter(description = "학생 코드") @RequestParam String studentCode,
                                                                                                                                              @Parameter(description = "기대 출력") @RequestParam String expectedOutput,
                                                                                                                                              @Parameter(description = "채점 기준 (선택)") @RequestParam(required = false) String rubric) throws JsonProcessingException {

        StructuredAnalysisResponse analysis = openAiService.generateStructuredAnalysis(studentCode, expectedOutput, rubric);

        return success(analysis, "구조화된 분석 완료");
    }

    // 간단한 테스트 엔드포인트 (인증 불필요)
    @PostMapping("/test")
    @Operation(summary = "OpenAI API 테스트", description = "OpenAI API가 정상 작동하는지 간단히 테스트합니다.")
    public ResponseEntity<ApiResponse<String>> test(@Parameter(description = "테스트 메시지") @RequestParam(defaultValue = "안녕하세요! 간단한 인사 부탁드립니다.") String message) {

        String result = openAiService.generate(message);

        return success(result, "OpenAI API 테스트 완료");
    }
}
