package com.cinemax.domain.codesnapshot.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.codesnapshot.dto.CodePeekPenaltyRequest;
import com.cinemax.domain.codesnapshot.dto.CodePeekPenaltyResponse;
import com.cinemax.domain.codesnapshot.service.CodePeekPenaltyService;
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

/**
 * 코드 교수자 감시 및 제재 관리 API
 */
@Slf4j
@RestController
@RequestMapping("/code-peek-penalties")
@RequiredArgsConstructor
@Tag(name = "CodePeekPenalty", description = "코드 엿보기 제재 관리 API")
public class CodePeekPenaltyController extends BaseController {

    private final CodePeekPenaltyService codePeekPenaltyService;

    // 감시 기록 또는 횟수 증가
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "감시 기록", description = "코드 감시 기록하고 제재 점수를 계산합니다.")
    public ResponseEntity<ApiResponse<CodePeekPenaltyResponse>> recordPeek(@Valid @RequestBody CodePeekPenaltyRequest request) {

        CodePeekPenaltyResponse response = codePeekPenaltyService.recordPeek(request);

        return created(response, "엿보기 기록이 완료되었습니다.");
    }

    // 제재 기록 조회
    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "제재 기록 조회", description = "특정 사용자의 주차별 제재 기록을 조회합니다.")
    public ResponseEntity<ApiResponse<CodePeekPenaltyResponse>> getPenalty(@Parameter(description = "사용자 ID") @RequestParam Long userId,
                                                                           @Parameter(description = "주차 세션 ID") @RequestParam Long weeklySessionId,
                                                                           @Parameter(description = "초대 ID") @RequestParam Long inviteId) {

        CodePeekPenaltyResponse response = codePeekPenaltyService.getPenalty(userId, weeklySessionId, inviteId);

        return success(response, "제재 기록 조회 성공");
    }

    // 사용자별 제재 목록 조회
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "사용자별 제재 목록 조회", description = "특정 사용자의 모든 제재 기록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CodePeekPenaltyResponse>>> getPenaltiesByUserId(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        List<CodePeekPenaltyResponse> responses = codePeekPenaltyService.getPenaltiesByUserId(userId);

        return success(responses, "사용자별 제재 목록 조회 성공");
    }

    // 주차 세션별 제재 목록 조회
    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "주차 세션별 제재 목록 조회", description = "특정 주차 세션의 모든 제재 기록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CodePeekPenaltyResponse>>> getPenaltiesByWeeklySession(@Parameter(description = "주차 세션 ID") @PathVariable Long weeklySessionId,
                                                                                                  @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        List<CodePeekPenaltyResponse> responses = codePeekPenaltyService.getPenaltiesByWeeklySession(weeklySessionId, inviteId);

        return success(responses, "주차 세션별 제재 목록 조회 성공");
    }

    // 주차 세션별 제재 점수 상위 목록 조회
    @GetMapping("/weekly-session/{weeklySessionId}/invite/{inviteId}/top")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "주차 세션별 제재 점수 상위 목록", description = "특정 주차 세션의 제재 점수 상위 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CodePeekPenaltyResponse>>> getTopPenaltiesByWeeklySession(@Parameter(description = "주차 세션 ID") @PathVariable Long weeklySessionId,
                                                                                                     @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        List<CodePeekPenaltyResponse> responses = codePeekPenaltyService.getTopPenaltiesByWeeklySession(weeklySessionId, inviteId);

        return success(responses, "제재 점수 상위 목록 조회 성공");
    }

    // 제재 점수 이상 사용자 조회
    @GetMapping("/min-points/{minPoints}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "제재 점수 이상 사용자 조회", description = "제재 점수가 특정 값 이상인 사용자를 조회합니다.")
    public ResponseEntity<ApiResponse<List<CodePeekPenaltyResponse>>> getPenaltiesByMinPoints(@Parameter(description = "최소 제재 점수") @PathVariable Integer minPoints) {

        List<CodePeekPenaltyResponse> responses = codePeekPenaltyService.getPenaltiesByMinPoints(minPoints);

        return success(responses, "제재 점수 이상 사용자 조회 성공");
    }

    // 제재 기록 초기화
    @PatchMapping("/reset")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "제재 기록 초기화", description = "특정 사용자의 제재 기록을 초기화합니다.")
    public ResponseEntity<ApiResponse<Void>> resetPenalty(@Parameter(description = "사용자 ID") @RequestParam Long userId,
                                                          @Parameter(description = "주차 세션 ID") @RequestParam Long weeklySessionId,
                                                          @Parameter(description = "초대 ID") @RequestParam Long inviteId) {

        codePeekPenaltyService.resetPenalty(userId, weeklySessionId, inviteId);

        return success(null, "제재 기록이 초기화되었습니다.");
    }

    // 제재 기록 삭제
    @DeleteMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "제재 기록 삭제", description = "특정 사용자의 제재 기록을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deletePenalty(@Parameter(description = "사용자 ID") @RequestParam Long userId,
                                                           @Parameter(description = "주차 세션 ID") @RequestParam Long weeklySessionId,
                                                           @Parameter(description = "초대 ID") @RequestParam Long inviteId) {

        codePeekPenaltyService.deletePenalty(userId, weeklySessionId, inviteId);

        return success(null, "제재 기록이 삭제되었습니다.");
    }
}
