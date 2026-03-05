package com.cinemax.domain.qna.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.qna.dto.AnswerRequest;
import com.cinemax.domain.qna.dto.AnswerResponse;
import com.cinemax.domain.qna.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 답변 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
@Tag(name = "Answer", description = "답변 관리 API")
public class AnswerController extends BaseController {

    private final AnswerService answerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN', 'STUDENT')")
    @Operation(summary = "답변 생성", description = "새로운 답변을 생성합니다.")
    public ResponseEntity<ApiResponse<AnswerResponse>> createAnswer(@Valid @RequestBody AnswerRequest request,
                                                                      Authentication authentication) {

        Long userId = extractUserIdFromAuthentication(authentication);
        AnswerResponse response = answerService.createAnswer(request, userId);

        return created(response, "답변이 성공적으로 생성되었습니다.");
    }

    @GetMapping("/{answerId}/{questionId}")
    @Operation(summary = "답변 조회", description = "답변 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<AnswerResponse>> getAnswer(@Parameter(description = "답변 ID") @PathVariable Long answerId,
                                                                 @Parameter(description = "질문 ID") @PathVariable Long questionId) {

        AnswerResponse response = answerService.getAnswer(answerId, questionId);

        return success(response, "답변 조회 성공");
    }

    @GetMapping("/question/{questionId}")
    @Operation(summary = "질문별 답변 목록 조회", description = "특정 질문의 모든 답변을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AnswerResponse>>> getAnswersByQuestionId(@Parameter(description = "질문 ID") @PathVariable Long questionId) {

        List<AnswerResponse> responses = answerService.getAnswersByQuestionId(questionId);

        return success(responses, "질문별 답변 목록 조회 성공");
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 답변 목록 조회", description = "특정 사용자의 모든 답변을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AnswerResponse>>> getAnswersByUserId(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        List<AnswerResponse> responses = answerService.getAnswersByUserId(userId);

        return success(responses, "사용자별 답변 목록 조회 성공");
    }

    @PutMapping("/{answerId}/{questionId}")
    @Operation(summary = "답변 수정", description = "답변 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<AnswerResponse>> updateAnswer(@Parameter(description = "답변 ID") @PathVariable Long answerId,
                                                                    @Parameter(description = "질문 ID") @PathVariable Long questionId,
                                                                    @Valid @RequestBody AnswerRequest request,
                                                                    Authentication authentication) {
        Long userId = extractUserIdFromAuthentication(authentication);
        AnswerResponse response = answerService.updateAnswer(answerId, questionId, request, userId);

        return success(response, "답변이 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{answerId}/{questionId}")
    @Operation(summary = "답변 삭제", description = "답변을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAnswer(@Parameter(description = "답변 ID") @PathVariable Long answerId,
                                                          @Parameter(description = "질문 ID") @PathVariable Long questionId,
                                                          Authentication authentication) {

        Long userId = extractUserIdFromAuthentication(authentication);
        answerService.deleteAnswer(answerId, questionId, userId);

        return success(null, "답변이 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/question/{questionId}/count")
    @Operation(summary = "답변 개수 조회", description = "질문별 답변 개수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> countAnswersByQuestionId(@Parameter(description = "질문 ID") @PathVariable Long questionId) {

        Long count = answerService.countAnswersByQuestionId(questionId);

        return success(count, "답변 개수 조회 성공");
    }

    private Long extractUserIdFromAuthentication(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // TODO: CustomUserDetails에서 userId를 직접 추출하도록 구현

        return 1L; // 임시 값
    }
}
