package com.cinemax.domain.cbt.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.cbt.dto.*;
import com.cinemax.domain.cbt.service.CbtService;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.global.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 자격증 모의 CBT API (반 = 자격증 1개, 과목별 랜덤 출제 / 과락·합불 판정 / 회차별 기록)
 */
@Slf4j
@RestController
@RequestMapping("/cbt")
@RequiredArgsConstructor
@Tag(name = "CBT", description = "자격증 모의 CBT API")
public class CbtController extends BaseController {

    private final CbtService cbtService;
    private final UserRepository userRepository;

    // 인증 principal 에서 userId 취득 (바디 신뢰 금지)
    private Long resolveUserId(CustomUserDetailsService userDetails, Authentication authentication) {
        if (userDetails != null) {
            return userDetails.getUserId();
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("인증 사용자 정보를 찾을 수 없습니다."))
                .getUserId();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    // ===== 관리자: 과목 설정 =====

    @GetMapping("/classes/{classId}/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "과목 설정 조회", description = "반의 CBT 과목 설정(배점/과락/합격점)과 문제 보유 수를 조회합니다.")
    public ResponseEntity<ApiResponse<CbtSubjectsResponse>> getSubjects(
            @Parameter(description = "반 ID") @PathVariable Long classId) {
        return success(cbtService.getSubjects(classId), "과목 설정 조회 성공");
    }

    @PutMapping("/classes/{classId}/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "과목 설정 저장",
            description = "과목 목록과 합격점수를 일괄 저장합니다. 활성 과목 배점 합계가 정확히 100점이어야 합니다.")
    public ResponseEntity<ApiResponse<CbtSubjectsResponse>> saveSubjects(
            @Parameter(description = "반 ID") @PathVariable Long classId,
            @Valid @RequestBody CbtSubjectSaveRequest request) {
        return success(cbtService.saveSubjects(classId, request), "과목 설정이 저장되었습니다.");
    }

    // ===== 관리자: 문제 은행 =====

    @PostMapping("/questions/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "문제 JSON 대량 등록",
            description = "문제 배열을 일괄 등록합니다. 미등록 과목명은 자동 생성되며 문항별 성공/실패 리포트를 반환합니다.")
    public ResponseEntity<ApiResponse<CbtBulkResultResponse>> bulkCreateQuestions(
            @Valid @RequestBody CbtQuestionBulkRequest request,
            @AuthenticationPrincipal CustomUserDetailsService userDetails,
            Authentication authentication) {
        Long createdBy = resolveUserId(userDetails, authentication);
        return created(cbtService.bulkCreateQuestions(request, createdBy), "문제 등록이 완료되었습니다.");
    }

    @GetMapping("/questions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "문제 목록", description = "반의 문제 은행을 페이징 조회합니다 (과목/키워드 필터).")
    public ResponseEntity<ApiResponse<Page<CbtQuestionResponse>>> getQuestions(
            @Parameter(description = "반 ID") @RequestParam Long classId,
            @Parameter(description = "과목 ID 필터") @RequestParam(required = false) Long subjectId,
            @Parameter(description = "지문 검색 키워드") @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CbtQuestionResponse> result = cbtService.getQuestions(
                classId, subjectId, keyword, PageRequest.of(page, size));
        return success(result, "문제 목록 조회 성공");
    }

    @GetMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "문제 상세", description = "문제 상세(정답/해설 포함)를 조회합니다.")
    public ResponseEntity<ApiResponse<CbtQuestionResponse>> getQuestion(
            @Parameter(description = "문제 ID") @PathVariable Long questionId) {
        return success(cbtService.getQuestion(questionId), "문제 조회 성공");
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "문제 수정", description = "지문/해설/과목/보기를 수정합니다 (보기 전체 교체).")
    public ResponseEntity<ApiResponse<CbtQuestionResponse>> updateQuestion(
            @Parameter(description = "문제 ID") @PathVariable Long questionId,
            @Valid @RequestBody CbtQuestionUpdateRequest request) {
        return success(cbtService.updateQuestion(questionId, request), "문제가 수정되었습니다.");
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "문제 삭제", description = "문제를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @Parameter(description = "문제 ID") @PathVariable Long questionId) {
        cbtService.deleteQuestion(questionId);
        return success(null, "문제가 삭제되었습니다.");
    }

    // ===== 관리자: 응시 현황 =====

    @GetMapping("/attempts/class/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "반 응시 현황", description = "반 학생별 회차별 점수/과락/합불 현황을 조회합니다.")
    public ResponseEntity<ApiResponse<CbtClassAttemptsResponse>> getClassAttempts(
            @Parameter(description = "반 ID") @PathVariable Long classId) {
        return success(cbtService.getClassAttempts(classId), "응시 현황 조회 성공");
    }

    // ===== 학생 =====

    @GetMapping("/classes/{classId}/info")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "CBT 시험 정보", description = "시험 구성(과목/배점/과락/합격 기준)과 내 회차별 기록을 조회합니다.")
    public ResponseEntity<ApiResponse<CbtInfoResponse>> getInfo(
            @Parameter(description = "반 ID") @PathVariable Long classId,
            @AuthenticationPrincipal CustomUserDetailsService userDetails,
            Authentication authentication) {
        Long userId = resolveUserId(userDetails, authentication);
        return success(cbtService.getInfo(classId, userId), "CBT 정보 조회 성공");
    }

    @GetMapping("/classes/{classId}/practice")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "응시 문제 세트", description = "과목별 출제 수만큼 랜덤 추출한 문제 세트를 반환합니다 (정답/해설 숨김).")
    public ResponseEntity<ApiResponse<CbtPracticeResponse>> getPracticeSet(
            @Parameter(description = "반 ID") @PathVariable Long classId) {
        return success(cbtService.getPracticeSet(classId), "응시 문제 세트 조회 성공");
    }

    @PostMapping("/classes/{classId}/attempts")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "답안 제출·채점",
            description = "답안을 제출하면 자동 채점 후 과목별 점수/과락/합불이 판정되고 회차 기록으로 저장됩니다.")
    public ResponseEntity<ApiResponse<CbtAttemptResultResponse>> submit(
            @Parameter(description = "반 ID") @PathVariable Long classId,
            @Valid @RequestBody CbtSubmitRequest request,
            @AuthenticationPrincipal CustomUserDetailsService userDetails,
            Authentication authentication) {
        Long userId = resolveUserId(userDetails, authentication);
        return created(cbtService.submit(classId, userId, request), "채점이 완료되었습니다.");
    }

    @GetMapping("/attempts/{attemptId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "회차 상세 복기", description = "과거 회차의 문항별 답안/정답/해설을 조회합니다 (본인 것만, 관리자는 전체).")
    public ResponseEntity<ApiResponse<CbtAttemptResultResponse>> getAttempt(
            @Parameter(description = "응시 ID") @PathVariable Long attemptId,
            @AuthenticationPrincipal CustomUserDetailsService userDetails,
            Authentication authentication) {
        Long userId = resolveUserId(userDetails, authentication);
        return success(cbtService.getAttempt(attemptId, userId, isAdmin(authentication)), "회차 상세 조회 성공");
    }
}
