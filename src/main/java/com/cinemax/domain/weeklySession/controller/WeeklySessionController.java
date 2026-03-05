package com.cinemax.domain.weeklySession.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.weeklySession.dto.WeeklySessionProgressResponse;
import com.cinemax.domain.weeklySession.dto.WeeklySessionRequest;
import com.cinemax.domain.weeklySession.dto.WeeklySessionResponse;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.domain.weeklySession.mapper.WeeklySessionMapper;
import com.cinemax.domain.weeklySession.service.WeeklySessionService;
import com.cinemax.global.enums.WeeklySessionStatus;
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

@Slf4j
@RestController
@RequestMapping("/weekly-sessions")
@RequiredArgsConstructor
@Tag(name = "WeeklySession", description = "주차별 수업 관리 API")
public class WeeklySessionController extends BaseController {

    private final WeeklySessionService weeklySessionService;
    private final WeeklySessionMapper weeklySessionMapper;

    // 주차별 수업 시작
    @PostMapping("/start")
    @PreAuthorize("hasRole('PROFESSOR')")
    @Operation(summary = "주차별 수업 시작", description = "특정 주차별 수업을 시작합니다. 동시 진행 제한: 하나의 수업에서 동시에 2개 이상의 주차별 수업을 진행할 수 없습니다.")
    public ResponseEntity<ApiResponse<WeeklySessionResponse>> startWeeklySession(@Valid @RequestBody WeeklySessionRequest request) {

        WeeklySession weeklySession = weeklySessionService.startWeeklySession(request.getInviteId(), request.getWeekNo());
        WeeklySessionResponse response = weeklySessionMapper.toResponse(weeklySession);

        return success(response, "주차별 수업이 성공적으로 시작되었습니다.");
    }

    // 주차별 수업 종료
    @PostMapping("/end")
    @PreAuthorize("hasRole('PROFESSOR')")
    @Operation(summary = "주차별 수업 종료", description = "진행 중인 주차별 수업을 수동으로 종료합니다.")
    public ResponseEntity<ApiResponse<WeeklySessionResponse>> endWeeklySession(@Valid @RequestBody WeeklySessionRequest request) {

        WeeklySession weeklySession = weeklySessionService.endWeeklySession(request.getInviteId(), request.getWeekNo());
        WeeklySessionResponse response = weeklySessionMapper.toResponse(weeklySession);

        return success(response, "주차별 수업이 성공적으로 종료되었습니다.");
    }

    // 주차별 수업 조회
    @GetMapping("/{inviteId}/{weekNo}")
    @Operation(summary = "주차별 수업 조회", description = "특정 주차별 수업 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<WeeklySessionResponse>> getWeeklySession(@Parameter(description = "초대 코드 ID") @PathVariable Long inviteId,
                                                                               @Parameter(description = "주차 번호") @PathVariable Integer weekNo) {

        WeeklySession weeklySession = weeklySessionService.getSession(inviteId, weekNo);
        WeeklySessionResponse response = weeklySessionMapper.toResponse(weeklySession);

        return success(response, "주차별 수업 조회 성공");
    }

    // 수업별 주차별 수업 목록 조회
    @GetMapping("/invite/{inviteId}")
    @Operation(summary = "수업별 주차별 수업 목록", description = "특정 수업의 모든 주차별 수업 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<WeeklySessionResponse>>> getAllWeeklySessions(@Parameter(description = "초대 코드 ID") @PathVariable Long inviteId) {

        List<WeeklySession> weeklySessions = weeklySessionService.getAllSessions(inviteId);
        List<WeeklySessionResponse> responses = weeklySessions.stream()
                .map(weeklySessionMapper::toResponse)
                .toList();

        return success(responses, "수업별 주차별 수업 목록 조회 성공");
    }

    // 진행 중인 주차별 수업 조회
    @GetMapping("/invite/{inviteCd}/in-progress")
    @Operation(summary = "진행 중인 주차별 수업", description = "특정 수업의 현재 진행 중인 주차별 수업을 조회합니다.")
    public ResponseEntity<ApiResponse<WeeklySessionResponse>> getInProgressWeeklySession(@Parameter(description = "초대 코드") @PathVariable String inviteCd) {

        WeeklySession weeklySession = weeklySessionService.getInProgressSession(inviteCd);
        WeeklySessionResponse response = weeklySessionMapper.toResponse(weeklySession);

        return success(response, "진행 중인 주차별 수업 조회 성공");
    }

    // 주차별 수업 상태 조회
    @GetMapping("/{inviteId}/{weekNo}/status")
    @Operation(summary = "주차별 수업 상태 조회", description = "특정 주차별 수업의 현재 상태를 조회합니다.")
    public ResponseEntity<ApiResponse<String>> getWeeklySessionStatus(@Parameter(description = "초대 코드 ID") @PathVariable Long inviteId,
                                                                      @Parameter(description = "주차 번호") @PathVariable Integer weekNo) {

        WeeklySessionStatus status = weeklySessionService.getSessionStatus(inviteId, weekNo);
        
        return success(status.getDescription(), "주차별 수업 상태 조회 성공");
    }

    // 주차별 수업 진행 상황 조회
    @GetMapping("/invite/{inviteCd}/progress")
    @Operation(summary = "주차별 수업 진행 상황", description = "특정 수업의 주차별 수업 진행 상황을 조회합니다.")
    public ResponseEntity<ApiResponse<WeeklySessionProgressResponse>> getWeeklySessionProgress(@Parameter(description = "초대 코드") @PathVariable String inviteCd) {

        WeeklySessionProgressResponse progress = weeklySessionService.getProgress(inviteCd);
        
        return success(progress, "주차별 수업 진행 상황 조회 성공");
    }

    // 상태별 주차별 수업 목록 조회 (status만으로 검색)
    @GetMapping("/status/{status}")
    @Operation(summary = "상태별 주차별 수업 목록", description = "특정 상태의 주차별 수업 목록을 조회합니다. status만으로 검색하며, response에는 inviteId가 포함됩니다.")
    public ResponseEntity<ApiResponse<List<WeeklySessionResponse>>> getWeeklySessionsByStatus(@Parameter(description = "상태 (PENDING, IN_PROGRESS, COMPLETED)") @PathVariable WeeklySessionStatus status) {

        List<WeeklySession> weeklySessions = weeklySessionService.getSessionsByStatusOnly(status);
        List<WeeklySessionResponse> responses = weeklySessions.stream()
                .map(weeklySessionMapper::toResponse)
                .toList();

        return success(responses, "상태별 주차별 수업 목록 조회 성공");
    }

    // 수업 진행률 조회
    @GetMapping("/invite/{inviteId}/progress-rate")
    @Operation(summary = "수업 진행률 조회", description = "특정 수업의 진행률을 백분율로 조회합니다.")
    public ResponseEntity<ApiResponse<Double>> getProgressRate(@Parameter(description = "초대 코드 ID") @PathVariable Long inviteId) {
        
        double progressRate = weeklySessionService.getProgressRate(inviteId);
        
        return success(progressRate, "수업 진행률 조회 성공");
    }

    // 만료된 주차별 수업 자동 종료 (관리자용)
    @PostMapping("/auto-end-expired")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "만료된 주차별 수업 자동 종료", description = "24시간이 지난 진행 중인 주차별 수업을 자동으로 종료합니다.")
    public ResponseEntity<ApiResponse<String>> autoEndExpiredWeeklySessions() {
        
        weeklySessionService.autoEndExpiredSessions();
        
        return success("자동 종료 완료", "만료된 주차별 수업 자동 종료 성공");
    }

    // 기존 초대 코드에 대한 주차별 수업 생성 (관리자/교수자용)
    @PostMapping("/invite/{inviteId}/create-sessions")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "주차별 수업 생성", description = "기존 초대 코드에 대한 주차별 수업을 생성합니다. 커리큘럼의 주차 정보를 기반으로 자동 생성됩니다.")
    public ResponseEntity<ApiResponse<String>> createWeeklySessionsForInvite(@Parameter(description = "초대 코드 ID") @PathVariable Long inviteId) {
        
        weeklySessionService.createWeeklySessionsForInvite(inviteId);
        
        return success("생성 완료", "주차별 수업이 성공적으로 생성되었습니다.");
    }
}
