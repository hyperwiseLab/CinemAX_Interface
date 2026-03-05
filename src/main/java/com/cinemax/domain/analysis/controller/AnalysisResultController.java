package com.cinemax.domain.analysis.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.analysis.dto.*;
import com.cinemax.domain.analysis.service.AnalysisResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * AI 분석 결과 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/analysis-results")
@RequiredArgsConstructor
@Tag(name = "Analysis Result", description = "AI 분석 결과 관리 API")
public class AnalysisResultController extends BaseController {

    private final AnalysisResultService analysisResultService;

    // 분석 결과 저장
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "분석 결과 저장", description = "AI 분석 결과를 데이터베이스에 저장합니다.")
    public ResponseEntity<ApiResponse<AnalysisResultResponse>> saveAnalysisResult(@Valid @RequestBody AnalysisResultRequest request) {

        AnalysisResultResponse response = analysisResultService.saveAnalysisResult(request);

        return success(response, "분석 결과가 저장되었습니다.");
    }

    // 분석 결과 상세 조회
    @GetMapping("/{analysisId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "분석 결과 상세 조회", description = "특정 분석 결과의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<AnalysisResultResponse>> getAnalysisResult(@Parameter(description = "분석 ID") @PathVariable Long analysisId) {

        AnalysisResultResponse response = analysisResultService.getAnalysisResult(analysisId);

        return success(response, "분석 결과 조회 완료");
    }

    // 사용자별 분석 히스토리 조회 (복습 기능)
    @GetMapping("/user/{userId}/history")
    @PreAuthorize("hasRole('STUDENT') and #userId == authentication.principal.userId or hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "사용자 분석 히스토리 조회", description = "학생의 AI 분석 히스토리를 조회합니다 (복습용).")
    public ResponseEntity<ApiResponse<Page<AnalysisHistoryResponse>>> getUserHistory(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                     @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
                                                                                     @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
                                                                                     @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "requestedAt") String sortBy,
                                                                                     @Parameter(description = "정렬 방향") @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<AnalysisHistoryResponse> response = analysisResultService.getUserHistory(userId, pageable);

        return success(response, "분석 히스토리 조회 완료");
    }

    // 사용자 + 과제별 분석 히스토리 조회
    @GetMapping("/user/{userId}/task/{taskId}/history")
    @PreAuthorize("hasRole('STUDENT') and #userId == authentication.principal.userId or hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "과제별 분석 히스토리 조회", description = "특정 과제에 대한 학생의 분석 히스토리를 조회합니다.")
    public ResponseEntity<ApiResponse<Page<AnalysisHistoryResponse>>> getUserHistoryByTask(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                           @Parameter(description = "과제 ID") @PathVariable Long taskId,
                                                                                           @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
                                                                                           @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestedAt"));
        Page<AnalysisHistoryResponse> response = analysisResultService.getUserHistoryByTask(userId, taskId, pageable);

        return success(response, "과제별 분석 히스토리 조회 완료");
    }

    // 사용자 + 사이클별 분석 히스토리 조회
    @GetMapping("/user/{userId}/cycle/{cycleId}/history")
    @PreAuthorize("hasRole('STUDENT') and #userId == authentication.principal.userId or hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "사이클별 분석 히스토리 조회", description = "특정 사이클에 대한 학생의 분석 히스토리를 조회합니다.")
    public ResponseEntity<ApiResponse<Page<AnalysisHistoryResponse>>> getUserHistoryByCycle(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                            @Parameter(description = "사이클 ID") @PathVariable Long cycleId,
                                                                                            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
                                                                                            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestedAt"));
        Page<AnalysisHistoryResponse> response = analysisResultService.getUserHistoryByCycle(userId, cycleId, pageable);

        return success(response, "사이클별 분석 히스토리 조회 완료");
    }

    // 기간별 분석 히스토리 조회
    @GetMapping("/user/{userId}/history/date-range")
    @PreAuthorize("hasRole('STUDENT') and #userId == authentication.principal.userId or hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "기간별 분석 히스토리 조회", description = "특정 기간 내 학생의 분석 히스토리를 조회합니다.")
    public ResponseEntity<ApiResponse<Page<AnalysisHistoryResponse>>> getUserHistoryByDateRange(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                                @Parameter(description = "시작일") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                                                @Parameter(description = "종료일") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                                                @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
                                                                                                @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AnalysisHistoryResponse> response = analysisResultService.getUserHistoryByDateRange(userId, startDate, endDate, pageable);

        return success(response, "기간별 분석 히스토리 조회 완료");
    }

    // 제출 ID로 분석 결과 조회
    @GetMapping("/submit/{submitId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "제출별 분석 결과 조회", description = "특정 제출에 대한 분석 결과를 조회합니다.")
    public ResponseEntity<ApiResponse<AnalysisResultResponse>> getAnalysisResultBySubmitId(@Parameter(description = "제출 ID") @PathVariable Long submitId) {

        AnalysisResultResponse response = analysisResultService.getAnalysisResultBySubmitId(submitId);

        return success(response, "제출별 분석 결과 조회 완료");
    }

    // 과제별 통계 조회 (교수용)
    @GetMapping("/statistics/task/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "과제별 통계 조회", description = "특정 과제에 대한 AI 분석 통계를 조회합니다 (교수용).")
    public ResponseEntity<ApiResponse<AnalysisStatisticsResponse>> getTaskStatistics(@Parameter(description = "과제 ID") @PathVariable Long taskId) {

        AnalysisStatisticsResponse response = analysisResultService.getTaskStatistics(taskId);

        return success(response, "과제별 통계 조회 완료");
    }

    // 수업별 통계 조회 (교수용)
    @GetMapping("/statistics/class/{classId}/cycle/{cycleId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업별 통계 조회", description = "특정 수업 및 사이클에 대한 AI 분석 통계를 조회합니다 (교수용).")
    public ResponseEntity<ApiResponse<AnalysisStatisticsResponse>> getClassStatistics(@Parameter(description = "수업 ID") @PathVariable Long classId,
                                                                                      @Parameter(description = "사이클 ID") @PathVariable Long cycleId) {

        AnalysisStatisticsResponse response = analysisResultService.getClassStatistics(classId, cycleId);

        return success(response, "수업별 통계 조회 완료");
    }

    // 분석 결과 플래그 업데이트
    @PatchMapping("/{analysisId}/flags")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "분석 결과 플래그 업데이트", description = "분석 결과의 T/F 플래그를 수동으로 업데이트합니다.")
    public ResponseEntity<ApiResponse<AnalysisResultResponse>> updateAnalysisFlags(@Parameter(description = "분석 ID") @PathVariable Long analysisId,
                                                                                   @Parameter(description = "요구사항 충족 여부") @RequestParam(required = false) Boolean requirementsMet,
                                                                                   @Parameter(description = "코드 품질 통과 여부") @RequestParam(required = false) Boolean codeQualityPass,
                                                                                   @Parameter(description = "논리 오류 존재 여부") @RequestParam(required = false) Boolean hasLogicError,
                                                                                   @Parameter(description = "보안 이슈 존재 여부") @RequestParam(required = false) Boolean hasSecurityIssue,
                                                                                   @Parameter(description = "개선 필요 여부") @RequestParam(required = false) Boolean needsImprovement) {

        AnalysisResultResponse response = analysisResultService.updateAnalysisFlags(
                analysisId,
                requirementsMet,
                codeQualityPass,
                hasLogicError,
                hasSecurityIssue,
                needsImprovement
        );

        return success(response, "분석 결과 플래그 업데이트 완료");
    }

    // 분석 결과 삭제
    @DeleteMapping("/{analysisId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "분석 결과 삭제", description = "분석 결과를 삭제합니다 (관리자 전용).")
    public ResponseEntity<ApiResponse<Void>> deleteAnalysisResult(@Parameter(description = "분석 ID") @PathVariable Long analysisId) {

        analysisResultService.deleteAnalysisResult(analysisId);

        return success(null, "분석 결과가 삭제되었습니다.");
    }

    // 토큰 사용량 통계 조회 - 기간별 (관리자 전용)
    @GetMapping("/admin/token-usage")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "토큰 사용량 통계 조회 (관리자)", description = "기간별 토큰 사용량 및 비용 통계를 조회합니다 (관리자 전용).")
    public ResponseEntity<ApiResponse<TokenUsageStatisticsResponse>> getTokenUsageStatistics(@Parameter(description = "시작일") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                                             @Parameter(description = "종료일") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                                             @Parameter(description = "수업 ID (선택)") @RequestParam(required = false) Long classId,
                                                                                             @Parameter(description = "사이클 ID (선택)") @RequestParam(required = false) Long cycleId) {

        TokenUsageStatisticsResponse response = analysisResultService.getTokenUsageStatistics(startDate, endDate, classId, cycleId);

        return success(response, "토큰 사용량 통계 조회 완료");
    }

    // 전체 토큰 사용량 통계 조회 (관리자 전용)
    @GetMapping("/admin/token-usage/total")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "전체 토큰 사용량 통계 조회 (관리자)", description = "전체 기간 토큰 사용량 및 비용 통계를 조회합니다 (관리자 전용).")
    public ResponseEntity<ApiResponse<TokenUsageStatisticsResponse>> getTotalTokenUsageStatistics() {

        TokenUsageStatisticsResponse response = analysisResultService.getTotalTokenUsageStatistics();

        return success(response, "전체 토큰 사용량 통계 조회 완료");
    }
}
