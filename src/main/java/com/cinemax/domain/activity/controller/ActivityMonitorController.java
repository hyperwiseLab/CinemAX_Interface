package com.cinemax.domain.activity.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.activity.dto.ActivityLogResponse;
import com.cinemax.domain.activity.dto.ActivityLogStatisticsResponse;
import com.cinemax.domain.activity.dto.StudentActivityResponse;
import com.cinemax.domain.activity.service.ActivityMonitorService;
import com.cinemax.global.enums.StudentActivityStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 실시간 활동 모니터링 API
 */
@Slf4j
@RestController
@RequestMapping("/activity-monitor")
@RequiredArgsConstructor
@Tag(name = "ActivityMonitor", description = "실시간 수업 모니터링 API")
public class ActivityMonitorController extends BaseController {

    private final ActivityMonitorService activityMonitorService;

    // 주차별 수업의 모든 학생 활동 상태 조회 (교수용)
    @GetMapping("/weekly-session/{weeklySessionId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "모든 학생 활동 상태 조회", description = "주차별 수업의 모든 학생 활동 상태를 실시간으로 조회합니다.")
    public ResponseEntity<ApiResponse<List<StudentActivityResponse>>> getAllStudentActivities(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        List<StudentActivityResponse> responses = activityMonitorService.getAllStudentActivities(weeklySessionId);

        return success(responses, "학생 활동 상태를 조회했습니다.");
    }

    // 모니터링 대시보드 집계 조회 (교수용) - 학생별 활동+최신코드+에러수 + 사이클/과제 + 제출 성공률을 한 번에
    @GetMapping("/weekly-session/{weeklySessionId}/dashboard")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "모니터링 대시보드 집계 조회",
            description = "학생별 활동 상태·최신 코드·에러 수와 주차 사이클/과제, 제출 성공률을 한 번에 조회합니다. 이후 갱신은 웹소켓 이벤트로 처리합니다.")
    public ResponseEntity<ApiResponse<com.cinemax.domain.activity.dto.ActivityDashboardResponse>> getDashboard(
            @Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
            @Parameter(description = "반 ID (세션에서 유추 실패 시 사용)") @RequestParam(required = false) Long classId) {

        return success(activityMonitorService.getDashboard(weeklySessionId, classId), "모니터링 대시보드를 조회했습니다.");
    }

    // 특정 상태의 학생 목록 조회 (교수용)
    @GetMapping("/weekly-session/{weeklySessionId}/status/{status}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "특정 상태 학생 조회", description = "특정 활동 상태의 학생 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<StudentActivityResponse>>> getStudentsByStatus(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                          @Parameter(description = "활동 상태 (ACTIVE, NEED_HELP, IDLE, COMPLETED)") @PathVariable StudentActivityStatus status) {

        List<StudentActivityResponse> responses = activityMonitorService.getStudentsByStatus(weeklySessionId, status);

        return success(responses, status.getDescription() + " 상태의 학생을 조회했습니다.");
    }

    // 도움이 필요한 학생 목록 조회 (교수용)
    @GetMapping("/weekly-session/{weeklySessionId}/need-help")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "도움 필요 학생 조회", description = "🔴 도움이 필요한 학생 목록을 조회합니다. (5분 이상 진도 없음 또는 테스트 3회 실패)")
    public ResponseEntity<ApiResponse<List<StudentActivityResponse>>> getStudentsNeedingHelp(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        List<StudentActivityResponse> responses = activityMonitorService.getStudentsNeedingHelp(weeklySessionId);

        return success(responses, "도움이 필요한 학생을 조회했습니다.");
    }

    // 활동중인 학생 목록 조회 (교수용)
    @GetMapping("/weekly-session/{weeklySessionId}/active")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "활동중 학생 조회", description = "🟢 최근 5분 이내 활동중인 학생 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<StudentActivityResponse>>> getActiveStudents(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        List<StudentActivityResponse> responses = activityMonitorService.getActiveStudents(weeklySessionId);

        return success(responses, "활동중인 학생을 조회했습니다.");
    }

    // 완료한 학생 목록 조회 (교수용)
    @GetMapping("/weekly-session/{weeklySessionId}/completed")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "완료 학생 조회", description = "✅ 해당 주차를 완료한 학생 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<StudentActivityResponse>>> getCompletedStudents(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        List<StudentActivityResponse> responses = activityMonitorService.getCompletedStudents(weeklySessionId);

        return success(responses, "완료한 학생을 조회했습니다.");
    }

    // 특정 학생의 활동 상태 조회
    @GetMapping("/weekly-session/{weeklySessionId}/student/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "학생 활동 상태 조회", description = "특정 학생의 활동 상태를 조회합니다.")
    public ResponseEntity<ApiResponse<StudentActivityResponse>> getStudentActivity(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                   @Parameter(description = "학생 ID") @PathVariable Long userId) {

        StudentActivityResponse response = activityMonitorService.getStudentActivity(weeklySessionId, userId);

        return success(response, "학생 활동 상태를 조회했습니다.");
    }

    // 모든 학생 활동 상태 강제 갱신 (관리자/교수용)
    @PostMapping("/weekly-session/{weeklySessionId}/refresh")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "활동 상태 강제 갱신", description = "모든 학생의 활동 상태를 강제로 갱신합니다.")
    public ResponseEntity<ApiResponse<Void>> refreshAllStudentStatuses(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        activityMonitorService.refreshAllStudentStatuses(weeklySessionId);

        return success(null, "활동 상태를 갱신했습니다.");
    }

    // 활동 기록 (내부 API - 코드 저장 시 자동 호출)
    @PostMapping("/weekly-session/{weeklySessionId}/activity/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "활동 기록", description = "학생의 활동을 기록합니다. (코드 저장, 페이지 이동 등)")
    public ResponseEntity<ApiResponse<Void>> recordActivity(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                            @Parameter(description = "학생 ID") @PathVariable Long userId) {

        activityMonitorService.recordActivity(weeklySessionId, userId);

        return success(null, "활동이 기록되었습니다.");
    }

    // 테스트 실패 기록 (내부 API - 테스트 실행 시 자동 호출)
    @PostMapping("/weekly-session/{weeklySessionId}/test-failure/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "테스트 실패 기록", description = "테스트 실패를 기록합니다.")
    public ResponseEntity<ApiResponse<Void>> recordTestFailure(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                               @Parameter(description = "학생 ID") @PathVariable Long userId) {

        activityMonitorService.recordTestFailure(weeklySessionId, userId);

        return success(null, "테스트 실패가 기록되었습니다.");
    }

    // 테스트 성공 기록 (내부 API - 테스트 성공 시 자동 호출)
    @PostMapping("/weekly-session/{weeklySessionId}/test-success/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "테스트 성공 기록", description = "테스트 성공을 기록합니다.")
    public ResponseEntity<ApiResponse<Void>> recordTestSuccess(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                               @Parameter(description = "학생 ID") @PathVariable Long userId) {

        activityMonitorService.recordTestSuccess(weeklySessionId, userId);

        return success(null, "테스트 성공이 기록되었습니다.");
    }

    // ===== 활동 로그 기록 및 조회 API =====

    // 활동 로그 기록
    @PostMapping("/activity-log")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "활동 로그 기록", description = "학생의 활동 로그를 기록합니다.")
    public ResponseEntity<ApiResponse<com.cinemax.domain.activity.dto.ActivityLogResponse>> recordActivityLog(@Parameter(description = "활동 로그 요청") @RequestBody com.cinemax.domain.activity.dto.ActivityLogRequest request) {

        ActivityLogResponse response = activityMonitorService.recordActivityLog(request);

        return created(response, "활동 로그가 기록되었습니다.");
    }

    // 주차별 수업의 모든 활동 로그 조회
    @GetMapping("/activity-log/weekly-session/{weeklySessionId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "모든 활동 로그 조회", description = "주차별 수업의 모든 활동 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<com.cinemax.domain.activity.dto.ActivityLogResponse>>> getAllActivityLogs(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId) {

        List<ActivityLogResponse> responses = activityMonitorService.getAllActivityLogs(weeklySessionId);

        return success(responses, "활동 로그를 조회했습니다.");
    }

    // 특정 학생의 활동 로그 조회
    @GetMapping("/activity-log/weekly-session/{weeklySessionId}/invite/{inviteId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "학생 활동 로그 조회", description = "특정 학생의 활동 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<com.cinemax.domain.activity.dto.ActivityLogResponse>>> getActivityLogsByInviteId(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                                                             @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        List<ActivityLogResponse> responses = activityMonitorService.getActivityLogsByInviteId(weeklySessionId, inviteId);

        return success(responses, "학생 활동 로그를 조회했습니다.");
    }

    // 특정 액션 타입의 활동 로그 조회
    @GetMapping("/activity-log/weekly-session/{weeklySessionId}/action/{action}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "액션별 활동 로그 조회", description = "특정 액션 타입의 활동 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<com.cinemax.domain.activity.dto.ActivityLogResponse>>> getActivityLogsByAction(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                                                           @Parameter(description = "활동 액션") @PathVariable com.cinemax.global.enums.ActivityAction action) {

        List<ActivityLogResponse> responses = activityMonitorService.getActivityLogsByAction(weeklySessionId, action);

        return success(responses, action.getDescription() + " 활동 로그를 조회했습니다.");
    }

    // 최근 활동 로그 조회 (특정 시간 이후)
    @GetMapping("/activity-log/weekly-session/{weeklySessionId}/recent")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "최근 활동 로그 조회", description = "특정 시간 이후의 활동 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<com.cinemax.domain.activity.dto.ActivityLogResponse>>> getRecentActivityLogs(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                                                         @Parameter(description = "조회 시작 시간 (분)") @RequestParam(defaultValue = "60") int minutes) {

        LocalDateTime fromTime = java.time.LocalDateTime.now().minusMinutes(minutes);
        List<ActivityLogResponse> responses = activityMonitorService.getRecentActivityLogs(weeklySessionId, fromTime);

        return success(responses, "최근 " + minutes + "분간의 활동 로그를 조회했습니다.");
    }

    // 특정 학생의 최근 활동 로그 조회
    @GetMapping("/activity-log/weekly-session/{weeklySessionId}/invite/{inviteId}/recent")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "학생 최근 활동 로그 조회", description = "특정 학생의 최근 활동 로그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<com.cinemax.domain.activity.dto.ActivityLogResponse>>> getRecentActivityLogsByInviteId(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                                                                   @Parameter(description = "초대 ID") @PathVariable Long inviteId,
                                                                                                                                   @Parameter(description = "조회 시작 시간 (분)") @RequestParam(defaultValue = "60") int minutes) {

        java.time.LocalDateTime fromTime = java.time.LocalDateTime.now().minusMinutes(minutes);
        List<ActivityLogResponse> responses = activityMonitorService.getRecentActivityLogsByInviteId(weeklySessionId, inviteId, fromTime);

        return success(responses, "최근 " + minutes + "분간의 학생 활동 로그를 조회했습니다.");
    }

    // 활동 로그 통계 조회
    @GetMapping("/activity-log/weekly-session/{weeklySessionId}/invite/{inviteId}/statistics")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "활동 로그 통계 조회", description = "특정 학생의 활동 로그 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<com.cinemax.domain.activity.dto.ActivityLogStatisticsResponse>> getActivityLogStatistics(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                                                                @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        ActivityLogStatisticsResponse response = activityMonitorService.getActivityLogStatistics(weeklySessionId, inviteId);

        return success(response, "활동 로그 통계를 조회했습니다.");
    }

    // 최근 활동 조회
    @GetMapping("/activity-log/weekly-session/{weeklySessionId}/invite/{inviteId}/latest")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "최근 활동 조회", description = "특정 학생의 가장 최근 활동을 조회합니다.")
    public ResponseEntity<ApiResponse<com.cinemax.domain.activity.dto.ActivityLogResponse>> getLatestActivityLog(@Parameter(description = "주차별 수업 ID") @PathVariable Long weeklySessionId,
                                                                                                                  @Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        ActivityLogResponse response = activityMonitorService.getLatestActivityLog(weeklySessionId, inviteId);

        return success(response, "최근 활동을 조회했습니다.");
    }

    // 유저/과제별 에러(테스트 실패) 카운트 조회
    @GetMapping("/error-count")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "에러 카운트 조회", description = "유저 ID와 과제 ID 기준으로 테스트 실패 횟수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> getErrorCount(@Parameter(description = "유저 ID") @RequestParam Long userId,
                                                           @Parameter(description = "과제 ID") @RequestParam Long taskId) {

        long count = activityMonitorService.getErrorCount(userId, taskId);

        return success(count, "에러 카운트를 조회했습니다.");
    }

    // 에러(테스트 실패) 카운트 생성
    @PostMapping("/error-count")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
            summary = "에러 카운트 생성",
            description = """
                    테스트 실패를 기록하여 에러 카운트를 증가시킵니다.

                    교수는 학생별로 에러 카운트를 조회할 수 있으며, 이 API는 GET /error-count와 일관성 있게 userId를 사용합니다.

                    ## Query Parameters
                    - **userId**: 유저 ID (필수) - 교수가 학생별로 구분하여 조회할 수 있습니다
                    - **weeklySessionId**: 주차별 수업 ID (필수)
                    - **taskId**: 과제 ID (선택)

                    ## 사용 예시
                    POST /api/v1/activity-monitor/error-count?userId=123&weeklySessionId=1&taskId=1

                    ## 조회 방법
                    GET /api/v1/activity-monitor/error-count?userId=123&taskId=1
                    """
    )
    public ResponseEntity<ApiResponse<ActivityLogResponse>> recordErrorCount(
            @Parameter(description = "유저 ID") @RequestParam Long userId,
            @Parameter(description = "주차별 수업 ID") @RequestParam Long weeklySessionId,
            @Parameter(description = "과제 ID") @RequestParam(required = false) Long taskId) {

        ActivityLogResponse response = activityMonitorService.recordErrorCount(userId, weeklySessionId, taskId);

        return created(response, "에러 카운트가 기록되었습니다.");
    }
}
