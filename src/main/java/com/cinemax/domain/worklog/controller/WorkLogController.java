package com.cinemax.domain.worklog.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.worklog.dto.CycleStatisticsResponse;
import com.cinemax.domain.worklog.dto.ProfessorFeedbackRequest;
import com.cinemax.domain.worklog.dto.WeeklyFeedbackResponse;
import com.cinemax.domain.worklog.dto.WorkHourStatisticsResponse;
import com.cinemax.domain.worklog.dto.WorkLogRequest;
import com.cinemax.domain.worklog.dto.WorkLogResponse;
import com.cinemax.domain.worklog.dto.WorkLogStatisticsResponse;
import com.cinemax.domain.worklog.service.WorkLogService;
import com.cinemax.global.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 업무일지 API
 */
@Slf4j
@RestController
@RequestMapping("/work-logs")
@RequiredArgsConstructor
@Tag(name = "WorkLog", description = "업무일지 관리 API")
public class WorkLogController extends BaseController {

    private final WorkLogService workLogService;

    // 업무일지 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "업무일지 생성", description = "새로운 업무일지를 작성합니다.")
    public ResponseEntity<ApiResponse<WorkLogResponse>> createWorkLog(@Valid @RequestBody WorkLogRequest request) {
        log.info("업무일지 생성 요청 (Controller) - userId: {}, weeklySessionId: {}, logDate: {}",
                request.getUserId(), request.getWeeklySessionId(), request.getLogDate());

        WorkLogResponse response = workLogService.createWorkLog(request);
        log.info("업무일지 생성 완료 (Controller) - userId: {}, weeklySessionId: {}, logDate: {}",
                response.getUserId(), response.getWeeklySessionId(), response.getLogDate());

        return created(response, "업무일지가 성공적으로 생성되었습니다.");
    }

    // 업무일지 수정
    @PutMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "업무일지 수정", description = "기존 업무일지를 수정합니다.")
    public ResponseEntity<ApiResponse<WorkLogResponse>> updateWorkLog(@Valid @RequestBody WorkLogRequest request) {

        WorkLogResponse response = workLogService.updateWorkLog(request);

        return success(response, "업무일지가 수정되었습니다.");
    }

    // 특정 날짜 업무일지 조회
    @GetMapping("/user/{userId}/weekly-session/{weeklySessionId}/date/{logDate}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "특정 날짜 업무일지 조회", description = "특정 날짜의 업무일지를 조회합니다.")
    public ResponseEntity<ApiResponse<WorkLogResponse>> getWorkLog(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                   @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                   @Parameter(description = "작성 날짜") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate) {

        WorkLogResponse response = workLogService.getWorkLog(userId, weeklySessionId, logDate);

        return success(response, "업무일지를 조회했습니다.");
    }

    // 사용자의 주차별 업무일지 목록 조회
    @GetMapping("/user/{userId}/weekly-session/{weeklySessionId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "주차별 업무일지 목록 조회", description = "특정 사용자의 주차별 업무일지 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<WorkLogResponse>>> getWorkLogsByUserAndWeeklySession(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                                @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        List<WorkLogResponse> responses = workLogService.getWorkLogsByUserAndWeeklySession(userId, weeklySessionId);

        return success(responses, "업무일지 목록을 조회했습니다.");
    }

    // 내 주차별 업무일지 목록 조회 (학생 본인)
    @GetMapping("/my/weekly-session/{weeklySessionId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "내 주차별 업무일지 조회", description = "현재 로그인한 학생의 주차별 업무일지를 조회합니다.")
    public ResponseEntity<ApiResponse<List<WorkLogResponse>>> getMyWorkLogs(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                            @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {
        log.info("내 주차별 업무일지 조회 - userDetails: {}, userId: {}, weeklySessionId: {}",
                userDetails, userDetails != null ? userDetails.getUserId() : null, weeklySessionId);

        List<WorkLogResponse> responses = workLogService.getWorkLogsByUserAndWeeklySession(userDetails.getUserId(), weeklySessionId);
        log.info("내 주차별 업무일지 조회 결과 - 결과 수: {}", responses.size());

        return success(responses, "내 업무일지 목록을 조회했습니다.");
    }

    // 사용자의 모든 업무일지 조회
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "사용자 전체 업무일지 조회", description = "특정 사용자의 모든 업무일지를 조회합니다.")
    public ResponseEntity<ApiResponse<List<WorkLogResponse>>> getWorkLogsByUser(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        List<WorkLogResponse> responses = workLogService.getWorkLogsByUser(userId);

        return success(responses, "사용자의 전체 업무일지를 조회했습니다.");
    }

    // 내 전체 업무일지 조회 (학생 본인)
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "내 전체 업무일지 조회", description = "현재 로그인한 학생의 모든 업무일지를 조회합니다.")
    public ResponseEntity<ApiResponse<List<WorkLogResponse>>> getMyAllWorkLogs(@AuthenticationPrincipal CustomUserDetailsService userDetails) {

        List<WorkLogResponse> responses = workLogService.getWorkLogsByUser(userDetails.getUserId());

        return success(responses, "내 전체 업무일지를 조회했습니다.");
    }

    @GetMapping("/my/invite/{inviteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @Operation(summary = "초대코드 기반 내 전체 업무일지 조회", description = "특정 Class(inviteId)의 모든 업무일지를 조회합니다.")
    public ResponseEntity<ApiResponse<List<WorkLogResponse>>> getMyAllWorkLogsByInviteId(@Parameter(description = "초대 코드 ID") @PathVariable Long inviteId,
                                                                                         @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {

        List<WorkLogResponse> responses = workLogService.getMyAllWorkLogsByInviteId(userDetails.getUserId(), inviteId);

        return success(responses, "inviteId 기반 내 전체 업무일지를 조회했습니다.");
    }


    // 주차별 수업의 모든 업무일지 조회 (교수용)
    @GetMapping("/weekly-session/{weeklySessionId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "주차별 수업 전체 업무일지 조회", description = "주차별 수업의 모든 학생 업무일지를 조회합니다.")
    public ResponseEntity<ApiResponse<List<WorkLogResponse>>> getWorkLogsByWeeklySession(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        List<WorkLogResponse> responses = workLogService.getWorkLogsByWeeklySession(weeklySessionId);

        return success(responses, "주차별 수업의 업무일지를 조회했습니다.");
    }

    // 기간별 업무일지 조회
    @GetMapping("/user/{userId}/weekly-session/{weeklySessionId}/date-range")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "기간별 업무일지 조회", description = "특정 기간의 업무일지를 조회합니다.")
    public ResponseEntity<ApiResponse<List<WorkLogResponse>>> getWorkLogsByDateRange(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                     @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                     @Parameter(description = "시작 날짜") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                                     @Parameter(description = "종료 날짜") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<WorkLogResponse> responses = workLogService.getWorkLogsByDateRange(userId, weeklySessionId, startDate, endDate);

        return success(responses, "기간별 업무일지를 조회했습니다.");
    }

    // 업무일지 통계 조회
    @GetMapping("/user/{userId}/weekly-session/{weeklySessionId}/statistics")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "업무일지 통계 조회", description = "사용자의 업무일지 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<WorkLogStatisticsResponse>> getStatistics(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        WorkLogStatisticsResponse response = workLogService.getStatistics(userId, weeklySessionId);

        return success(response, "업무일지 통계를 조회했습니다.");
    }

    // 내 업무일지 통계 조회 (학생 본인)
    @GetMapping("/my/weekly-session/{weeklySessionId}/statistics")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "내 업무일지 통계 조회", description = "현재 로그인한 학생의 업무일지 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<WorkLogStatisticsResponse>> getMyStatistics(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                  @AuthenticationPrincipal com.cinemax.global.security.CustomUserDetailsService userDetails) {

        WorkLogStatisticsResponse response = workLogService.getStatistics(userDetails.getUserId(), weeklySessionId);

        return success(response, "내 업무일지 통계를 조회했습니다.");
    }

    // 업무일지 삭제
    @DeleteMapping("/user/{userId}/weekly-session/{weeklySessionId}/date/{logDate}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "업무일지 삭제", description = "업무일지를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteWorkLog(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                           @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                           @Parameter(description = "작성 날짜") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate) {

        workLogService.deleteWorkLog(userId, weeklySessionId, logDate);

        return success(null, "업무일지가 삭제되었습니다.");
    }

    // 업무일지 존재 여부 확인
    @GetMapping("/user/{userId}/weekly-session/{weeklySessionId}/date/{logDate}/exists")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "업무일지 존재 여부 확인", description = "특정 날짜의 업무일지 존재 여부를 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> existsWorkLog(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                              @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                              @Parameter(description = "작성 날짜") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate) {

        boolean exists = workLogService.existsWorkLog(userId, weeklySessionId, logDate);

        return success(exists, "업무일지 존재 여부를 확인했습니다.");
    }

    // 사용자 학습 시간 통계 조회
    @GetMapping("/user/{userId}/weekly-session/{weeklySessionId}/work-hour-statistics")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "사용자 학습 시간 통계 조회", description = "특정 사용자의 특정 주차 학습 시간 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<WorkHourStatisticsResponse>> getUserWorkHourStatistics(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                             @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        WorkHourStatisticsResponse response = workLogService.getWorkHourStatisticsByUser(userId, weeklySessionId);

        return success(response, "학습 시간 통계 조회 성공");
    }

    // 주차별 학습 시간 통계 조회 (교수용)
    @GetMapping("/weekly-session/{weeklySessionId}/work-hour-statistics")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "주차별 학습 시간 통계 조회", description = "주차별 수업의 전체 학생 학습 시간 통계를 조회합니다. (교수 전용)")
    public ResponseEntity<ApiResponse<WorkHourStatisticsResponse>> getWeeklySessionWorkHourStatistics( @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        WorkHourStatisticsResponse response = workLogService.getWorkHourStatisticsByWeeklySession(weeklySessionId);

        return success(response, "주차별 학습 시간 통계 조회 성공");
    }

    // 교수 피드백 추가 (교수 전용)
    @PostMapping("/user/{userId}/weekly-session/{weeklySessionId}/date/{logDate}/feedback")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "업무일지 피드백 추가", description = "교수가 학생의 업무일지에 피드백을 추가합니다. (교수 전용)")
    public ResponseEntity<ApiResponse<WorkLogResponse>> addProfessorFeedback(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                             @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                             @Parameter(description = "작성 날짜") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
            @Valid @RequestBody ProfessorFeedbackRequest request) {

        WorkLogResponse response = workLogService.addProfessorFeedback(userId, weeklySessionId, logDate, request);

        return success(response, "피드백이 성공적으로 추가되었습니다.");
    }

    // 교수 피드백 수정 (교수 전용)
    @PutMapping("/user/{userId}/weekly-session/{weeklySessionId}/date/{logDate}/feedback")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "업무일지 피드백 수정", description = "교수가 학생의 업무일지 피드백을 수정합니다. (교수 전용)")
    public ResponseEntity<ApiResponse<WorkLogResponse>> updateProfessorFeedback(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                @Parameter(description = "작성 날짜") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
                                                                                @Valid @RequestBody ProfessorFeedbackRequest request) {

        WorkLogResponse response = workLogService.updateProfessorFeedback(userId, weeklySessionId, logDate, request);

        return success(response, "피드백이 성공적으로 수정되었습니다.");
    }

    // Cycle별 통계 조회 (사용자별)
    @GetMapping("/user/{userId}/curriculum/{curId}/cycle-statistics")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "Cycle별 통계 조회", description = "특정 사용자의 특정 커리큘럼에 대한 Cycle별 WorkLog 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<List<CycleStatisticsResponse>>> getCycleStatistics(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                         @Parameter(description = "커리큘럼 ID") @PathVariable Long curId) {

        List<CycleStatisticsResponse> responses = workLogService.getCycleStatistics(userId, curId);

        return success(responses, "Cycle별 통계를 조회했습니다.");
    }

    // 내 Cycle별 통계 조회 (학생 본인)
    @GetMapping("/my/curriculum/{curId}/cycle-statistics")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "내 Cycle별 통계 조회", description = "현재 로그인한 학생의 특정 커리큘럼에 대한 Cycle별 WorkLog 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<List<CycleStatisticsResponse>>> getMyCycleStatistics(@Parameter(description = "커리큘럼 ID") @PathVariable Long curId,
                                                                                           @AuthenticationPrincipal CustomUserDetailsService userDetails) {

        List<CycleStatisticsResponse> responses = workLogService.getCycleStatistics(userDetails.getUserId(), curId);

        return success(responses, "내 Cycle별 통계를 조회했습니다.");
    }

    // Week별 피드백 조회
    @GetMapping("/user/{userId}/week/{weekNo}/feedback")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "Week별 피드백 조회", description = "특정 사용자의 특정 주차에 대한 피드백(의미있었던 내용, 어려웠던 내용, 궁금한 점)을 조회합니다.")
    public ResponseEntity<ApiResponse<WeeklyFeedbackResponse>> getWeeklyFeedback(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                                                 @Parameter(description = "주차 번호") @PathVariable Integer weekNo) {

        WeeklyFeedbackResponse response = workLogService.getWeeklyFeedback(userId, weekNo);

        return success(response, "Week별 피드백을 조회했습니다.");
    }

    // 내 Week별 피드백 조회 (학생 본인)
    @GetMapping("/my/week/{weekNo}/feedback")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "내 Week별 피드백 조회", description = "현재 로그인한 학생의 특정 주차에 대한 피드백을 조회합니다.")
    public ResponseEntity<ApiResponse<WeeklyFeedbackResponse>> getMyWeeklyFeedback(@Parameter(description = "주차 번호") @PathVariable Integer weekNo,
                                                                                   @AuthenticationPrincipal CustomUserDetailsService userDetails) {

        WeeklyFeedbackResponse response = workLogService.getWeeklyFeedback(userDetails.getUserId(), weekNo);

        return success(response, "내 Week별 피드백을 조회했습니다.");
    }

    // Class별 Cycle 통계 조회 (교수용)
    @GetMapping("/class/{classId}/cycle-statistics")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Class별 Cycle 통계 조회", description = "특정 Class의 전체 학생에 대한 Cycle별 WorkLog 통계를 조회합니다. (교수 전용)")
    public ResponseEntity<ApiResponse<List<CycleStatisticsResponse>>> getCycleStatisticsByClass(@Parameter(description = "Class ID") @PathVariable Long classId) {

        List<CycleStatisticsResponse> responses = workLogService.getCycleStatisticsByClass(classId);

        return success(responses, "Class별 Cycle 통계를 조회했습니다.");
    }
}
