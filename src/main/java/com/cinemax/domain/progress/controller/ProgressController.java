package com.cinemax.domain.progress.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.progress.dto.ProgressRequest;
import com.cinemax.domain.progress.dto.ProgressResponse;
import com.cinemax.domain.progress.dto.ProgressStatisticsResponse;
import com.cinemax.domain.progress.dto.ProgressStatusUpdateRequest;
import com.cinemax.domain.progress.service.ProgressService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 진도 관리 API
 */
@Slf4j
@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@Tag(name = "Progress", description = "진도 관리 API")
public class ProgressController extends BaseController {

    private final ProgressService progressService;
    private final UserRepository userRepository;

    // 진도 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "진도 생성", description = "새로운 진도를 생성합니다. (일반적으로 수업 참여 시 자동 생성)")
    public ResponseEntity<ApiResponse<ProgressResponse>> createProgress(@Valid @RequestBody ProgressRequest request) {

        ProgressResponse response = progressService.createProgress(request);

        return created(response, "진도가 성공적으로 생성되었습니다.");
    }


    // 특정 학생의 특정 주차 진도 조회
    @GetMapping("/weekly-session/{weeklySessionId}/user/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "학생 진도 조회", description = "특정 학생의 특정 주차 진도를 조회합니다.")
    public ResponseEntity<ApiResponse<ProgressResponse>> getProgress(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                     @Parameter(description = "학생 ID") @PathVariable Long userId) {

        ProgressResponse response = progressService.getProgress(weeklySessionId, userId);

        return success(response, "진도를 조회했습니다.");
    }

    // 내 진도 조회 (학생 본인)
    @GetMapping("/weekly-session/{weeklySessionId}/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "내 진도 조회", description = "현재 로그인한 학생의 진도를 조회합니다.")
    public ResponseEntity<ApiResponse<ProgressResponse>> getMyProgress(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                       @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails,
                                                                       Authentication authentication) {

        Long userId;

        if (userDetails != null) {
            userId = userDetails.getUserId();
        } else if (authentication != null && authentication.getName() != null) {
            userId = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("인증 사용자 정보를 찾을 수 없습니다.")).getUserId();
        } else {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        ProgressResponse response = progressService.getProgress(weeklySessionId, userId);

        return success(response, "내 진도를 조회했습니다.");
    }

    // 주차별 수업의 모든 학생 진도 조회 (교수용)
    @GetMapping("/weekly-session/{weeklySessionId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "주차별 모든 진도 조회", description = "주차별 수업의 모든 학생 진도를 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProgressResponse>>> getAllProgressByWeeklySession(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        List<ProgressResponse> responses = progressService.getAllProgressByWeeklySession(weeklySessionId);

        return success(responses, "주차별 진도를 조회했습니다.");
    }

    // 수업의 모든 학생 진도 조회 (교수용)
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업 모든 진도 조회", description = "수업의 모든 학생 진도를 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProgressResponse>>> getAllProgressByClass(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        List<ProgressResponse> responses = progressService.getAllProgressByClass(classId);

        return success(responses, "수업 진도를 조회했습니다.");
    }

    // 학생의 전체 진도 목록 조회
    @GetMapping("/student/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "학생 전체 진도 조회", description = "학생의 모든 수업 진도를 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProgressResponse>>> getStudentAllProgress(@Parameter(description = "학생 ID") @PathVariable Long userId) {

        List<ProgressResponse> responses = progressService.getStudentAllProgress(userId);

        return success(responses, "학생 전체 진도를 조회했습니다.");
    }

    // 내 전체 진도 목록 조회 (학생 본인)
    @GetMapping("/my-all")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "내 전체 진도 조회", description = "현재 로그인한 학생의 모든 진도를 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProgressResponse>>> getMyAllProgress(@AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails,
                                                                                Authentication authentication) {

        Long userId;

        if (userDetails != null) {
            userId = userDetails.getUserId();
        } else if (authentication != null && authentication.getName() != null) {
            userId = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("인증 사용자 정보를 찾을 수 없습니다.")).getUserId();
        } else {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        List<ProgressResponse> responses = progressService.getStudentAllProgress(userId);

        return success(responses, "내 전체 진도를 조회했습니다.");
    }

    // 진도율 업데이트
    @PutMapping("/weekly-session/{weeklySessionId}/user/{userId}/progress-pct")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "진도율 업데이트", description = "학생의 진도율을 업데이트합니다.")
    public ResponseEntity<ApiResponse<ProgressResponse>> updateProgressPct(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                           @Parameter(description = "학생 ID") @PathVariable Long userId,
                                                                           @Parameter(description = "진도율 (%)") @RequestParam BigDecimal progressPct) {

        ProgressResponse response = progressService.updateProgressPct(weeklySessionId, userId, progressPct);

        return success(response, "진도율이 업데이트되었습니다.");
    }

    // 내 진도율 업데이트 (학생 본인)
    @PutMapping("/weekly-session/{weeklySessionId}/my/progress-pct")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "내 진도율 업데이트", description = "현재 로그인한 학생의 진도율을 업데이트합니다.")
    public ResponseEntity<ApiResponse<ProgressResponse>> updateMyProgressPct(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                             @Parameter(description = "진도율 (%)") @RequestParam BigDecimal progressPct,
                                                                             @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails,
                                                                             Authentication authentication) {

        Long userId;

        if (userDetails != null) {
            userId = userDetails.getUserId();
        } else if (authentication != null && authentication.getName() != null) {
            userId = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("인증 사용자 정보를 찾을 수 없습니다.")).getUserId();
        } else {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        ProgressResponse response = progressService.updateProgressPct(weeklySessionId, userId, progressPct);

        return success(response, "진도율이 업데이트되었습니다.");
    }

    // 진도 완료 처리
    @PostMapping("/weekly-session/{weeklySessionId}/user/{userId}/complete")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "진도 완료 처리", description = "학생의 진도를 완료 처리합니다.")
    public ResponseEntity<ApiResponse<ProgressResponse>> markAsCompleted(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                         @Parameter(description = "학생 ID") @PathVariable Long userId) {

        ProgressResponse response = progressService.markAsCompleted(weeklySessionId, userId);

        return success(response, "진도가 완료 처리되었습니다.");
    }

    // 활동 상태 업데이트
    @PutMapping("/weekly-session/{weeklySessionId}/user/{userId}/activity-status")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "활동 상태 업데이트", description = "학생의 활동 상태를 업데이트합니다.")
    public ResponseEntity<ApiResponse<ProgressResponse>> updateActivityStatus(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                               @Parameter(description = "학생 ID") @PathVariable Long userId,
                                                                               @Valid @RequestBody ProgressStatusUpdateRequest request) {

        ProgressResponse response = progressService.updateActivityStatus(weeklySessionId, userId, request.getActivityStatus());

        return success(response, "활동 상태가 업데이트되었습니다.");
    }

    // 모드 업데이트
    @PutMapping("/weekly-session/{weeklySessionId}/user/{userId}/mode")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "모드 업데이트", description = "학생의 과제 모드를 업데이트합니다.")
    public ResponseEntity<ApiResponse<ProgressResponse>> updateMode(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                     @Parameter(description = "학생 ID") @PathVariable Long userId,
                                                                     @Parameter(description = "과제 모드") @RequestParam com.cinemax.global.enums.TaskMode mode) {

        ProgressResponse response = progressService.updateMode(weeklySessionId, userId, mode);

        return success(response, "모드가 업데이트되었습니다.");
    }

    // 진도 통계 조회 (교수용)
    @GetMapping("/weekly-session/{weeklySessionId}/statistics")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "진도 통계 조회", description = "주차별 수업의 진도 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<ProgressStatisticsResponse>> getStatistics(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        ProgressStatisticsResponse response = progressService.getStatistics(weeklySessionId);

        return success(response, "진도 통계를 조회했습니다.");
    }

    // 진도 존재 여부 확인
    @GetMapping("/weekly-session/{weeklySessionId}/user/{userId}/exists")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "진도 존재 여부 확인", description = "학생의 진도가 존재하는지 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> existsProgress(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                               @Parameter(description = "학생 ID") @PathVariable Long userId) {

        boolean exists = progressService.existsProgress(weeklySessionId, userId);

        return success(exists, "진도 존재 여부를 확인했습니다.");
    }

    // 진도 삭제
    @DeleteMapping("/weekly-session/{weeklySessionId}/user/{userId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "진도 삭제", description = "학생의 진도를 삭제합니다. (수강 취소 시 사용)")
    public ResponseEntity<ApiResponse<Void>> deleteProgress(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                            @Parameter(description = "학생 ID") @PathVariable Long userId) {

        progressService.deleteProgress(weeklySessionId, userId);

        return success(null, "진도가 삭제되었습니다.");
    }
}
