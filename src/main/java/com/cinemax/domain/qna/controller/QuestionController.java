package com.cinemax.domain.qna.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.qna.dto.QuestionRequest;
import com.cinemax.domain.qna.dto.QuestionResponse;
import com.cinemax.domain.qna.service.QuestionService;
import com.cinemax.global.enums.QuestionStatus;
import com.cinemax.global.enums.QuestionUrgency;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 질문 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@Tag(name = "Question", description = "질문 관리 API")
public class QuestionController extends BaseController {

    private final QuestionService questionService;

    @PostMapping
    @Operation(summary = "질문 생성", description = "새로운 질문을 생성합니다.")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(@Valid @RequestBody QuestionRequest request) {
        QuestionResponse response = questionService.createQuestion(request);
        return created(response, "질문이 성공적으로 생성되었습니다.");
    }

    @GetMapping("/{questionId}/{classId}/{weeklySessionId}/{inviteId}")
    @Operation(summary = "질문 조회", description = "질문 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestion(@Parameter(description = "질문 ID") @PathVariable Long questionId,
                                                                     @Parameter(description = "클래스 ID") @PathVariable Long classId,
                                                                     @Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                                     @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        QuestionResponse response = questionService.getQuestion(questionId);

        return success(response, "질문 조회 성공");
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "클래스별 질문 목록 조회", description = "특정 클래스의 모든 질문을 조회합니다.")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByClassId(@Parameter(description = "클래스 ID") @PathVariable Long classId) {

        List<QuestionResponse> responses = questionService.getQuestionsByClassId(classId);

        return success(responses, "클래스별 질문 목록 조회 성공");
    }

    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}")
    @Operation(summary = "주차별 세션 질문 목록 조회", description = "특정 주차별 세션의 모든 질문을 조회합니다.")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByWeeklySession(@Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                                                           @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        List<QuestionResponse> responses = questionService.getQuestionsByWeeklySession(weeklySessionId);

        return success(responses, "주차별 세션 질문 목록 조회 성공");
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 질문 목록 조회", description = "특정 사용자의 모든 질문을 조회합니다.")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByUserId(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        List<QuestionResponse> responses = questionService.getQuestionsByUserId(userId);

        return success(responses, "사용자별 질문 목록 조회 성공");
    }

    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}/status/{status}")
    @Operation(summary = "상태별 질문 목록 조회", description = "특정 상태의 질문을 조회합니다.")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByStatus(@Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                                                      @Parameter(description = "초대 ID") @PathVariable Long inviteId,
                                                                                      @Parameter(description = "질문 상태") @PathVariable QuestionStatus status) {

        List<QuestionResponse> responses = questionService.getQuestionsByStatus(weeklySessionId, status);

        return success(responses, "상태별 질문 목록 조회 성공");
    }

    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}/urgency/{urgency}")
    @Operation(summary = "긴급도별 질문 목록 조회", description = "특정 긴급도의 질문을 조회합니다.")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByUrgency(@Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                                                       @Parameter(description = "초대 ID") @PathVariable Long inviteId,
                                                                                       @Parameter(description = "긴급도") @PathVariable QuestionUrgency urgency) {

        List<QuestionResponse> responses = questionService.getQuestionsByUrgency(weeklySessionId, urgency);

        return success(responses, "긴급도별 질문 목록 조회 성공");
    }

    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}/unanswered")
    @Operation(summary = "답변되지 않은 질문 목록 조회", description = "답변되지 않은 질문을 조회합니다.")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getUnansweredQuestions(@Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                                                      @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        List<QuestionResponse> responses = questionService.getUnansweredQuestions(weeklySessionId);

        return success(responses, "답변되지 않은 질문 목록 조회 성공");
    }

    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}/high-urgency")
    @Operation(summary = "높은 긴급도 질문 조회", description = "높은 긴급도 질문을 조회합니다.")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getHighUrgencyQuestions(@Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                                                         @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        List<QuestionResponse> responses = questionService.getHighUrgencyQuestions(weeklySessionId);

        return success(responses, "높은 긴급도 질문 조회 성공");
    }

    @GetMapping("/class/{classId}/search")
    @Operation(summary = "질문 검색", description = "제목 또는 내용으로 질문을 검색합니다.")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> searchQuestions(@Parameter(description = "클래스 ID") @PathVariable Long classId,
                                                                                 @Parameter(description = "검색 키워드") @RequestParam String keyword) {
        List<QuestionResponse> responses = questionService.searchQuestions(classId, keyword);

        return success(responses, "질문 검색 성공");
    }

    @GetMapping("/class/{classId}/search/advanced")
    @Operation(summary = "질문 통합 검색", description = "키워드, 태그, 기간으로 질문을 검색합니다. 모든 필터는 선택사항입니다.")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> searchQuestionsAdvanced(
            @Parameter(description = "클래스 ID") @PathVariable Long classId,
            @Parameter(description = "검색 키워드 (선택)") @RequestParam(required = false) String keyword,
            @Parameter(description = "태그 (선택)") @RequestParam(required = false) String tag,
            @Parameter(description = "시작 날짜 (선택, ISO 8601 형식)") @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
            java.time.LocalDateTime startDate,
            @Parameter(description = "종료 날짜 (선택, ISO 8601 형식)") @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
            java.time.LocalDateTime endDate) {

        List<QuestionResponse> responses = questionService.searchQuestionsAdvanced(classId, keyword, tag, startDate, endDate);

        return success(responses, "질문 통합 검색 성공");
    }

    @PutMapping("/{questionId}/{classId}/{weeklySessionId}/{inviteId}")
    @Operation(summary = "질문 수정", description = "질문 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(@Parameter(description = "질문 ID") @PathVariable Long questionId,
                                                                        @Parameter(description = "클래스 ID") @PathVariable Long classId,
                                                                        @Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                                        @Parameter(description = "초대 ID") @PathVariable Long inviteId,
                                                                        @Valid @RequestBody QuestionRequest request) {

        QuestionResponse response = questionService.updateQuestion(questionId, request);

        return success(response, "질문이 성공적으로 수정되었습니다.");
    }

    @PatchMapping("/{questionId}/{classId}/{weeklySessionId}/{inviteId}/status")
    @Operation(summary = "질문 상태 변경", description = "질문 상태를 변경합니다.")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestionStatus(@Parameter(description = "질문 ID") @PathVariable Long questionId,
                                                                              @Parameter(description = "클래스 ID") @PathVariable Long classId,
                                                                              @Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                                              @Parameter(description = "초대 ID") @PathVariable Long inviteId,
                                                                              @Parameter(description = "질문 상태") @RequestParam QuestionStatus status) {

        QuestionResponse response = questionService.updateQuestionStatus(questionId, status);

        return success(response, "질문 상태가 성공적으로 변경되었습니다.");
    }

    @DeleteMapping("/{questionId}/{classId}/{weeklySessionId}/{inviteId}")
    @Operation(summary = "질문 삭제", description = "질문을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@Parameter(description = "질문 ID") @PathVariable Long questionId,
                                                            @Parameter(description = "클래스 ID") @PathVariable Long classId,
                                                            @Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                            @Parameter(description = "초대 ID") @PathVariable Long inviteId,
                                                            Authentication authentication) {

        Long userId = extractUserIdFromAuthentication(authentication);
        questionService.deleteQuestion(questionId, userId);

        return success(null, "질문이 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}/count")
    @Operation(summary = "질문 개수 조회", description = "주차별 세션의 질문 개수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> countQuestionsByWeeklySession(@Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                                           @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        Long count = questionService.countQuestionsByWeeklySession(weeklySessionId);

        return success(count, "질문 개수 조회 성공");
    }

    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}/count/unanswered")
    @Operation(summary = "답변되지 않은 질문 개수 조회", description = "답변되지 않은 질문 개수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> countUnansweredQuestions(@Parameter(description = "주차별 세션 ID") @PathVariable Long weeklySessionId,
                                                                      @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        Long count = questionService.countUnansweredQuestions(weeklySessionId);

        return success(count, "답변되지 않은 질문 개수 조회 성공");
    }

    private Long extractUserIdFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        // UserService를 통해 이메일로 userId를 찾거나, CustomUserDetails에서 직접 추출
        // 여기서는 간단하게 처리하기 위해 email을 사용하거나, UserDetails에서 추출하도록 구현
        // TODO: CustomUserDetails에서 userId를 직접 추출하도록 구현
        return 1L; // 임시 값, 실제로는 authentication에서 userId를 추출해야 함
    }
}
