package com.cinemax.domain.quiz.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.quiz.dto.QuizClassResultResponse;
import com.cinemax.domain.quiz.dto.QuizGenerateRequest;
import com.cinemax.domain.quiz.dto.QuizPlayResponse;
import com.cinemax.domain.quiz.dto.QuizResponse;
import com.cinemax.domain.quiz.dto.QuizResultResponse;
import com.cinemax.domain.quiz.dto.QuizSubmitRequest;
import com.cinemax.domain.quiz.dto.QuizUpdateRequest;
import com.cinemax.domain.quiz.service.QuizService;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.global.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 주차별 퀴즈 관리 API Controller (관리자 CRUD + AI 생성)
 */
@Slf4j
@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "주차별 퀴즈 관리 API")
public class QuizController extends BaseController {

    private final QuizService quizService;
    private final UserRepository userRepository;

    // 인증 principal 에서 userId 취득 (바디 신뢰 금지). ProgressController 패턴.
    private Long resolveUserId(CustomUserDetailsService userDetails, Authentication authentication) {
        if (userDetails != null) {
            return userDetails.getUserId();
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("인증 사용자 정보를 찾을 수 없습니다."))
                .getUserId();
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "주차별 퀴즈 AI 생성",
            description = "반이 선택한 커리큘럼 시나리오로 주차별 퀴즈를 AI 생성합니다(DRAFT). 시나리오는 백엔드가 aggregate.")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> generateQuizzes(
            @Valid @RequestBody QuizGenerateRequest request,
            @AuthenticationPrincipal CustomUserDetailsService userDetails,
            Authentication authentication) {
        Long createdBy = resolveUserId(userDetails, authentication);
        List<QuizResponse> responses = quizService.generateQuizzes(request, createdBy);
        return created(responses, "퀴즈가 생성되었습니다.");
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "반의 퀴즈 목록", description = "특정 반의 주차별 퀴즈 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> getQuizzesByClass(
            @Parameter(description = "반 ID") @PathVariable Long classId) {
        List<QuizResponse> responses = quizService.getQuizzesByClass(classId);
        return success(responses, "반의 퀴즈 목록 조회 성공");
    }

    @GetMapping("/{quizId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "퀴즈 상세", description = "퀴즈 문항·보기를 포함한 상세를 조회합니다.")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuiz(
            @Parameter(description = "퀴즈 ID") @PathVariable Long quizId) {
        QuizResponse response = quizService.getQuiz(quizId);
        return success(response, "퀴즈 조회 성공");
    }

    @PutMapping("/{quizId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "퀴즈 전체 수정", description = "문항·보기·정답·해설을 통째로 수정합니다. 공개된 퀴즈는 수정 불가.")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(
            @Parameter(description = "퀴즈 ID") @PathVariable Long quizId,
            @Valid @RequestBody QuizUpdateRequest request) {
        QuizResponse response = quizService.updateQuiz(quizId, request);
        return success(response, "퀴즈가 수정되었습니다.");
    }

    @PatchMapping("/{quizId}/publish")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "퀴즈 확정(공개)", description = "퀴즈를 PUBLISHED 상태로 확정해 학생에게 공개합니다.")
    public ResponseEntity<ApiResponse<QuizResponse>> publishQuiz(
            @Parameter(description = "퀴즈 ID") @PathVariable Long quizId) {
        QuizResponse response = quizService.publishQuiz(quizId);
        return success(response, "퀴즈가 공개되었습니다.");
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "퀴즈 삭제", description = "퀴즈를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(
            @Parameter(description = "퀴즈 ID") @PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return success(null, "퀴즈가 삭제되었습니다.");
    }

    // ===== 학생 응시 / 결과 =====

    @GetMapping("/class/{classId}/week/{weekNo}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "응시용 퀴즈 조회", description = "반+주차의 공개 퀴즈를 정답 없이 조회합니다.")
    public ResponseEntity<ApiResponse<List<QuizPlayResponse>>> getPlayableQuizzes(
            @Parameter(description = "반 ID") @PathVariable Long classId,
            @Parameter(description = "주차 번호") @PathVariable Integer weekNo) {
        List<QuizPlayResponse> responses = quizService.getPlayableQuizzes(classId, weekNo);
        return success(responses, "응시용 퀴즈 조회 성공");
    }

    @PostMapping("/{quizId}/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "퀴즈 제출·채점", description = "답안을 제출해 자동채점합니다. 최초 제출만 기록됩니다.")
    public ResponseEntity<ApiResponse<QuizResultResponse>> submit(
            @Parameter(description = "퀴즈 ID") @PathVariable Long quizId,
            @Valid @RequestBody QuizSubmitRequest request,
            @AuthenticationPrincipal CustomUserDetailsService userDetails,
            Authentication authentication) {
        Long userId = resolveUserId(userDetails, authentication);
        QuizResultResponse response = quizService.submit(quizId, request, userId);
        return success(response, "채점이 완료되었습니다.");
    }

    @GetMapping("/{quizId}/result")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "본인 결과 조회", description = "본인의 퀴즈 채점 결과(점수·정답·해설)를 조회합니다.")
    public ResponseEntity<ApiResponse<QuizResultResponse>> getMyResult(
            @Parameter(description = "퀴즈 ID") @PathVariable Long quizId,
            @AuthenticationPrincipal CustomUserDetailsService userDetails,
            Authentication authentication) {
        Long userId = resolveUserId(userDetails, authentication);
        QuizResultResponse response = quizService.getMyResult(quizId, userId);
        return success(response, "결과 조회 성공");
    }

    @GetMapping("/{quizId}/results")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "반 전체 결과 집계", description = "반 전체 결과와 평균·랭킹을 조회합니다.")
    public ResponseEntity<ApiResponse<QuizClassResultResponse>> getClassResults(
            @Parameter(description = "퀴즈 ID") @PathVariable Long quizId) {
        QuizClassResultResponse response = quizService.getClassResults(quizId);
        return success(response, "반 결과 집계 조회 성공");
    }
}
