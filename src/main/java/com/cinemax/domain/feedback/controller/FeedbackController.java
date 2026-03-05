package com.cinemax.domain.feedback.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.feedback.dto.FeedbackRequest;
import com.cinemax.domain.feedback.dto.FeedbackResponse;
import com.cinemax.domain.feedback.service.FeedbackService;
import com.cinemax.global.enums.FeedbackType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;

/**
 * Feedback 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "피드백 관리 API")
public class FeedbackController extends BaseController {

    private final FeedbackService feedbackService;

    // 피드백 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "피드백 생성", description = "새로운 피드백을 생성합니다.")
    public ResponseEntity<ApiResponse<FeedbackResponse>> createFeedback(@Valid @RequestBody FeedbackRequest request) {

        FeedbackResponse response = feedbackService.createFeedback(request);

        return created(response, "피드백이 성공적으로 생성되었습니다.");
    }

    // 피드백 조회 (단일)
    @GetMapping("/{feedbackId}/{taskId}")
    @Operation(summary = "피드백 조회", description = "피드백 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<FeedbackResponse>> getFeedback(@Parameter(description = "Feedback ID") @PathVariable Long feedbackId,
                                                                     @Parameter(description = "Task ID") @PathVariable Long taskId) {

        FeedbackResponse response = feedbackService.getFeedback(feedbackId, taskId);

        return success(response, "피드백 조회 성공");
    }

    // 전체 피드백 조회
    @GetMapping
    @Operation(summary = "전체 피드백 목록 조회", description = "모든 피드백을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getAllFeedbacks() {

        List<FeedbackResponse> responses = feedbackService.getAllFeedbacks();

        return success(responses, "전체 피드백 목록 조회 성공");
    }

    // Task별 피드백 조회
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Task별 피드백 조회", description = "특정 Task의 모든 피드백을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbacksByTaskId(@Parameter(description = "Task ID") @PathVariable Long taskId) {

        List<FeedbackResponse> responses = feedbackService.getFeedbacksByTaskId(taskId);

        return success(responses, "Task별 피드백 조회 성공");
    }

    // FeedbackType별 피드백 조회
    @GetMapping("/type/{feedbackType}")
    @Operation(summary = "FeedbackType별 피드백 조회", description = "특정 FeedbackType의 모든 피드백을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbacksByType(@Parameter(description = "Feedback Type") @PathVariable FeedbackType feedbackType) {

        List<FeedbackResponse> responses = feedbackService.getFeedbacksByType(feedbackType);

        return success(responses, "FeedbackType별 피드백 조회 성공");
    }

    // Curriculum별 피드백 조회
    @GetMapping("/curriculum/{curId}")
    @Operation(summary = "Curriculum별 피드백 조회", description = "특정 Curriculum의 모든 피드백을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbacksByCurId(@Parameter(description = "Curriculum ID") @PathVariable Long curId) {

        List<FeedbackResponse> responses = feedbackService.getFeedbacksByCurId(curId);

        return success(responses, "Curriculum별 피드백 조회 성공");
    }

    // Curriculum과 주차별 피드백 조회
    @GetMapping("/curriculum/{curId}/week/{weekNo}")
    @Operation(summary = "Curriculum과 주차별 피드백 조회", description = "특정 Curriculum의 특정 주차 피드백을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbacksByCurIdAndWeekNo(@Parameter(description = "Curriculum ID") @PathVariable Long curId,
                                                                                            @Parameter(description = "주차 번호") @PathVariable Integer weekNo) {

        List<FeedbackResponse> responses = feedbackService.getFeedbacksByCurIdAndWeekNo(curId, weekNo);

        return success(responses, "Curriculum과 주차별 피드백 조회 성공");
    }

    // 대체: Task + Type
    @GetMapping("/task/{taskId}/type/{feedbackType}")
    @Operation(summary = "Task+Type 피드백 조회", description = "특정 Task의 특정 타입 피드백을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbacksByTaskAndType(@Parameter(description = "Task ID") @PathVariable Long taskId,
                                                                                         @Parameter(description = "Feedback Type") @PathVariable FeedbackType feedbackType) {

        List<FeedbackResponse> responses = feedbackService.getFeedbacksByTaskAndType(taskId, feedbackType);

        return success(responses, "Task+Type 피드백 조회 성공");
    }

    // 대체: Task + 기간
    @GetMapping("/task/{taskId}/range")
    @Operation(summary = "Task+기간 피드백 조회", description = "특정 Task의 기간별 피드백을 조회합니다. ISO-8601 형식의 start/end 사용")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbacksByTaskAndDateRange(@Parameter(description = "Task ID") @PathVariable Long taskId,
                                                                                              @RequestParam LocalDateTime start,
                                                                                              @RequestParam LocalDateTime end) {

        List<FeedbackResponse> responses = feedbackService.getFeedbacksByTaskAndDateRange(taskId, start, end);

        return success(responses, "Task+기간 피드백 조회 성공");
    }

    // 대체: 전체 기간
    @GetMapping("/range")
    @Operation(summary = "기간별 피드백 조회", description = "전체 피드백을 기간으로 조회합니다. ISO-8601 형식의 start/end 사용")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbacksByDateRange(@RequestParam LocalDateTime start,
                                                                                       @RequestParam LocalDateTime end) {

        List<FeedbackResponse> responses = feedbackService.getFeedbacksByDateRange(start, end);

        return success(responses, "기간별 피드백 조회 성공");
    }

    // 피드백 수정
    @PutMapping("/{feedbackId}/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "피드백 수정", description = "피드백 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<FeedbackResponse>> updateFeedback(@Parameter(description = "Feedback ID") @PathVariable Long feedbackId,
                                                                        @Parameter(description = "Task ID") @PathVariable Long taskId,
                                                                        @Valid @RequestBody FeedbackRequest request) {

        FeedbackResponse response = feedbackService.updateFeedback(feedbackId, taskId, request);

        return success(response, "피드백이 성공적으로 수정되었습니다.");
    }

    // 피드백 삭제
    @DeleteMapping("/{feedbackId}/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "피드백 삭제", description = "피드백을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(@Parameter(description = "Feedback ID") @PathVariable Long feedbackId,
                                                            @Parameter(description = "Task ID") @PathVariable Long taskId) {

        feedbackService.deleteFeedback(feedbackId, taskId);

        return success(null, "피드백이 성공적으로 삭제되었습니다.");
    }
}
