package com.cinemax.domain.auth.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.auth.dto.AuthLogRequest;
import com.cinemax.domain.auth.dto.AuthLogResponse;
import com.cinemax.domain.auth.dto.AuthLogStatisticsResponse;
import com.cinemax.domain.auth.service.AuthLogService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.global.enums.AuthEventType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 인증 로그 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/auth-logs")
@RequiredArgsConstructor
@Tag(name = "AuthLog", description = "인증 로그 관리 API")
public class AuthLogController extends BaseController {

    private final AuthLogService authLogService;

    // 인증 로그 생성
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "인증 로그 생성", description = "새로운 인증 로그를 생성합니다.")
    public ResponseEntity<ApiResponse<AuthLogResponse>> createAuthLog(@Valid @RequestBody AuthLogRequest request) {

        AuthLogResponse response = authLogService.createAuthLog(request);
        
        return created(response, "인증 로그가 성공적으로 생성되었습니다.");
    }

    // 인증 로그 조회
    @GetMapping("/{authLogId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "인증 로그 조회", description = "인증 로그 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<AuthLogResponse>> getAuthLog(@Parameter(description = "인증 로그 ID") @PathVariable Long authLogId) {

        AuthLogResponse response = authLogService.getAuthLog(authLogId);
        
        return success(response, "인증 로그 조회 성공");
    }

    // 사용자별 인증 로그 조회
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "사용자별 인증 로그 조회", description = "특정 사용자의 인증 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<AuthLogResponse>>> getAuthLogsByUserId(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        List<AuthLogResponse> responses = authLogService.getAuthLogsByUserId(userId);
        
        return success(responses, "사용자별 인증 로그 조회 성공");
    }

     // 사용자별 인증 로그 조회 (페이징)
    @GetMapping("/user/{userId}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "사용자별 인증 로그 조회 (페이징)", description = "특정 사용자의 인증 로그를 페이징으로 조회합니다.")
    public ResponseEntity<ApiResponse<Page<AuthLogResponse>>> getAuthLogsByUserIdWithPaging(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                            Pageable pageable) {
        Page<AuthLogResponse> responses = authLogService.getAuthLogsByUserId(userId, pageable);
        
        return success(responses, "사용자별 인증 로그 조회 성공");
    }

    // 이메일별 인증 로그 조회
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "이메일별 인증 로그 조회", description = "특정 이메일의 인증 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<AuthLogResponse>>> getAuthLogsByEmail(@Parameter(description = "이메일") @PathVariable String email) {

        List<AuthLogResponse> responses = authLogService.getAuthLogsByEmail(email);
        
        return success(responses, "이메일별 인증 로그 조회 성공");
    }

    // 이벤트 타입별 인증 로그 조회
    @GetMapping("/event/{eventType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "이벤트 타입별 인증 로그 조회", description = "특정 이벤트 타입의 인증 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<AuthLogResponse>>> getAuthLogsByEventType(@Parameter(description = "이벤트 타입")
                                                                                         @PathVariable AuthEventType eventType) {
        List<AuthLogResponse> responses = authLogService.getAuthLogsByEventType(eventType);
        
        return success(responses, "이벤트 타입별 인증 로그 조회 성공");
    }

    // 기간별 인증 로그 조회
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "기간별 인증 로그 조회", description = "특정 기간의 인증 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<AuthLogResponse>>> getAuthLogsByDateRange(@Parameter(description = "시작 날짜") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                                     @Parameter(description = "종료 날짜") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuthLogResponse> responses = authLogService.getAuthLogsByDateRange(startDate, endDate);
        
        return success(responses, "기간별 인증 로그 조회 성공");
    }

    // 최근 로그인 성공 로그 조회
    @GetMapping("/user/{userId}/latest-login")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "최근 로그인 성공 로그 조회", description = "사용자의 최근 로그인 성공 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<AuthLogResponse>> getLatestLoginSuccess(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        AuthLogResponse response = authLogService.getLatestLoginSuccess(userId);
        
        return success(response, "최근 로그인 성공 로그 조회 성공");
    }

    // 최근 로그인 실패 로그 조회
    @GetMapping("/user/{userId}/recent-failures")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "최근 로그인 실패 로그 조회", description = "사용자의 최근 로그인 실패 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<AuthLogResponse>>> getRecentLoginFailures(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        List<AuthLogResponse> responses = authLogService.getRecentLoginFailures(userId);
        
        return success(responses, "최근 로그인 실패 로그 조회 성공");
    }

    // 로그인 실패 횟수 조회
    @GetMapping("/user/{userId}/failure-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "로그인 실패 횟수 조회", description = "사용자의 특정 시간 이후 로그인 실패 횟수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> getLoginFailureCount(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                  @Parameter(description = "기준 시간") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {

        Long count = authLogService.getLoginFailureCountSince(userId, since);
        
        return success(count, "로그인 실패 횟수 조회 성공");
    }

    // 사용자별 인증 로그 통계 조회
    @GetMapping("/user/{userId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "사용자별 인증 로그 통계 조회", description = "사용자의 인증 로그 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<AuthLogStatisticsResponse>> getAuthLogStatisticsByUserId(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        AuthLogStatisticsResponse response = authLogService.getAuthLogStatisticsByUserId(userId);
        
        return success(response, "사용자별 인증 로그 통계 조회 성공");
    }

    // 전체 인증 로그 통계 조회
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "전체 인증 로그 통계 조회", description = "전체 인증 로그 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<AuthLogStatisticsResponse>> getOverallAuthLogStatistics() {

        AuthLogStatisticsResponse response = authLogService.getOverallAuthLogStatistics();
        
        return success(response, "전체 인증 로그 통계 조회 성공");
    }

    // 기간별 인증 로그 통계 조회
    @GetMapping("/statistics/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "기간별 인증 로그 통계 조회", description = "특정 기간의 인증 로그 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<AuthLogStatisticsResponse>> getAuthLogStatisticsByDateRange(@Parameter(description = "시작 날짜") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                                                  @Parameter(description = "종료 날짜") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        AuthLogStatisticsResponse response = authLogService.getAuthLogStatisticsByDateRange(startDate, endDate);
        
        return success(response, "기간별 인증 로그 통계 조회 성공");
    }

    // 의심스러운 활동 감지
    @GetMapping("/user/{userId}/suspicious")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "의심스러운 활동 감지", description = "사용자의 의심스러운 활동을 감지합니다.")
    public ResponseEntity<ApiResponse<List<AuthLogResponse>>> detectSuspiciousActivity(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                       @Parameter(description = "기준 시간") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {

        List<AuthLogResponse> responses = authLogService.detectSuspiciousActivity(userId, since);
        
        return success(responses, "의심스러운 활동 감지 완료");
    }

    // 인증 로그 삭제
    @DeleteMapping("/{authLogId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "인증 로그 삭제", description = "인증 로그를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAuthLog(@Parameter(description = "인증 로그 ID") @PathVariable Long authLogId) {

        authLogService.deleteAuthLog(authLogId);
        
        return success(null, "인증 로그가 성공적으로 삭제되었습니다.");
    }

    // 사용자별 인증 로그 삭제
    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자별 인증 로그 삭제", description = "특정 사용자의 모든 인증 로그를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAuthLogsByUserId(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        authLogService.deleteAuthLogsByUserId(userId);
        
        return success(null, "사용자별 인증 로그가 성공적으로 삭제되었습니다.");
    }

    // 기간별 인증 로그 삭제
    @DeleteMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "기간별 인증 로그 삭제", description = "특정 기간의 인증 로그를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAuthLogsByDateRange(@Parameter(description = "시작 날짜") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                       @Parameter(description = "종료 날짜") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        authLogService.deleteAuthLogsByDateRange(startDate, endDate);
        
        return success(null, "기간별 인증 로그가 성공적으로 삭제되었습니다.");
    }
}

