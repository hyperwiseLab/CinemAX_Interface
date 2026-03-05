package com.cinemax.domain.codesnapshot.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.codesnapshot.dto.CodeSaveRequest;
import com.cinemax.domain.codesnapshot.dto.CodeSnapshotResponse;
import com.cinemax.domain.codesnapshot.service.CodeSnapshotService;
import com.cinemax.domain.user.entity.User;
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
 * 코드 스냅샷 관리 API
 * 코드 자동저장 및 불러오기 기능
 */
@Slf4j
@RestController
@RequestMapping("/code-snapshots")
@RequiredArgsConstructor
@Tag(name = "CodeSnapshot", description = "코드 자동저장 API")
public class CodeSnapshotController extends BaseController {

    private final CodeSnapshotService codeSnapshotService;

    // 코드 자동저장
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "코드 자동저장", description = "작성 중인 코드를 자동으로 저장합니다. 기존 코드가 있으면 업데이트됩니다.")
    public ResponseEntity<ApiResponse<CodeSnapshotResponse>> saveCode(@Valid @RequestBody CodeSaveRequest request,
                                                                      @AuthenticationPrincipal CustomUserDetailsService userDetails) {

        CodeSnapshotResponse response = codeSnapshotService.saveCode(request, userDetails.getUserId());

        return success(response, "코드가 성공적으로 저장되었습니다.");
    }

    // 최신 코드 불러오기
    @GetMapping("/latest")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "최신 코드 불러오기", description = "저장된 최신 코드를 불러옵니다.")
    public ResponseEntity<ApiResponse<CodeSnapshotResponse>> getLatestCode(@Parameter(description = "주차 수업 ID") @RequestParam Long weeklySessionId,
                                                                           @Parameter(description = "사용자 ID") @RequestParam Long userId) {

        CodeSnapshotResponse response = codeSnapshotService.getLatestCode(weeklySessionId, userId);

        return success(response, "최신 코드를 불러왔습니다.");
    }

    // 코드 저장 이력 조회
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "코드 저장 이력 조회", description = "과제에 대한 모든 코드 저장 이력을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CodeSnapshotResponse>>> getCodeHistory(@Parameter(description = "과제 ID") @RequestParam Long taskId,
                                                                                  @Parameter(description = "주차 수업 ID") @RequestParam Long weeklySessionId,
                                                                                  @Parameter(description = "사이클 ID") @RequestParam Long cycleId,
                                                                                  @AuthenticationPrincipal CustomUserDetailsService userDetails) {

        List<CodeSnapshotResponse> responses = codeSnapshotService.getCodeHistory(taskId, userDetails.getUserId(), weeklySessionId, cycleId);

        return success(responses, "코드 이력을 조회했습니다.");
    }

    // 저장된 코드 존재 여부 확인
    @GetMapping("/exists")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "저장된 코드 존재 여부", description = "저장된 코드가 있는지 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> hasCode(@Parameter(description = "과제 ID") @RequestParam Long taskId,
                                                        @Parameter(description = "주차 수업 ID") @RequestParam Long weeklySessionId,
                                                        @Parameter(description = "사이클 ID") @RequestParam Long cycleId,
                                                        @AuthenticationPrincipal CustomUserDetailsService userDetails) {

        boolean exists = codeSnapshotService.hasCode(taskId, userDetails.getUserId(), weeklySessionId, cycleId);

        return success(exists, "코드 존재 여부를 확인했습니다.");
    }

    // 코드 삭제
    @DeleteMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "코드 삭제", description = "저장된 코드를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteCode(@Parameter(description = "과제 ID") @RequestParam Long taskId,
                                                        @Parameter(description = "주차 수업 ID") @RequestParam Long weeklySessionId,
                                                        @Parameter(description = "사이클 ID") @RequestParam Long cycleId,
                                                        @AuthenticationPrincipal CustomUserDetailsService userDetails) {

        codeSnapshotService.deleteCode(taskId, userDetails.getUserId(), weeklySessionId, cycleId);

        return success(null, "코드가 성공적으로 삭제되었습니다.");
    }

    // 실시간 모니터링: 주차별 수업의 모든 학생 코드 조회 (교수용)
    @GetMapping("/monitor")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "학생 코드 실시간 모니터링", description = "주차별 수업의 모든 학생 코드를 실시간으로 조회합니다. userId 파라미터를 추가하면 특정 학생만 필터링합니다. (교수 전용)")
    public ResponseEntity<ApiResponse<List<CodeSnapshotResponse>>> monitorStudentCodes(@Parameter(description = "주차 수업 ID") @RequestParam Long weeklySessionId,
                                                                                       @Parameter(description = "과제 ID") @RequestParam Long taskId,
                                                                                       @Parameter(description = "학생 ID (선택사항)") @RequestParam(required = false) Long userId) {

        List<CodeSnapshotResponse> responses;

        if (userId != null) {
            // userId가 제공된 경우 특정 학생 코드만 조회
            responses = codeSnapshotService.getStudentCodesByUserId(weeklySessionId, taskId, userId);
        } else {
            // userId가 없으면 모든 학생 코드 조회
            responses = codeSnapshotService.getAllStudentCodes(weeklySessionId, taskId);
        }

        return success(responses, "학생 코드를 조회했습니다.");
    }
}
