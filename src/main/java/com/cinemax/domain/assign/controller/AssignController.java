package com.cinemax.domain.assign.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.assign.dto.AssignRequest;
import com.cinemax.domain.assign.dto.AssignResponse;
import com.cinemax.domain.assign.service.AssignService;
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
 * Assign 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/assigns")
@RequiredArgsConstructor
@Tag(name = "Assign", description = "과제 관리 API")
public class AssignController extends BaseController {

    private final AssignService assignService;

    // Assign 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Assign 생성", description = "새로운 Assign을 생성합니다.")
    public ResponseEntity<ApiResponse<AssignResponse>> createAssign(@Valid @RequestBody AssignRequest request) {

        AssignResponse response = assignService.createAssign(request);

        return created(response, "Assign이 성공적으로 생성되었습니다.");
    }

    // Assign 조회 (단일)
    @GetMapping("/{assignId}/{taskId}")
    @Operation(summary = "Assign 조회", description = "Assign 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<AssignResponse>> getAssign(@Parameter(description = "Assign ID") @PathVariable Long assignId,
                                                                 @Parameter(description = "Task ID") @PathVariable Long taskId) {

        AssignResponse response = assignService.getAssign(assignId, taskId);

        return success(response, "Assign 조회 성공");
    }

    // 전체 Assign 조회
    @GetMapping
    @Operation(summary = "전체 Assign 목록 조회", description = "모든 Assign을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AssignResponse>>> getAllAssigns() {

        List<AssignResponse> responses = assignService.getAllAssigns();

        return success(responses, "전체 Assign 목록 조회 성공");
    }

    // Task별 Assign 조회
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Task별 Assign 조회", description = "특정 Task의 모든 Assign을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AssignResponse>>> getAssignsByTaskId(@Parameter(description = "Task ID") @PathVariable Long taskId) {

        List<AssignResponse> responses = assignService.getAssignsByTaskId(taskId);

        return success(responses, "Task별 Assign 조회 성공");
    }

    // 제목으로 Assign 검색
    @GetMapping("/search")
    @Operation(summary = "Assign 검색", description = "제목으로 Assign을 검색합니다.")
    public ResponseEntity<ApiResponse<List<AssignResponse>>> searchAssignsByTitle(@Parameter(description = "검색 키워드") @RequestParam String keyword) {

        List<AssignResponse> responses = assignService.searchAssignsByTitle(keyword);

        return success(responses, "Assign 검색 성공");
    }

    // Assign 수정
    @PutMapping("/{assignId}/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Assign 수정", description = "Assign 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<AssignResponse>> updateAssign(@Parameter(description = "Assign ID") @PathVariable Long assignId,
                                                                    @Parameter(description = "Task ID") @PathVariable Long taskId,
                                                                    @Valid @RequestBody AssignRequest request) {

        AssignResponse response = assignService.updateAssign(assignId, taskId, request);

        return success(response, "Assign이 성공적으로 수정되었습니다.");
    }

    // Assign 삭제
    @DeleteMapping("/{assignId}/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Assign 삭제", description = "Assign을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAssign(@Parameter(description = "Assign ID") @PathVariable Long assignId,
                                                          @Parameter(description = "Task ID") @PathVariable Long taskId) {

        assignService.deleteAssign(assignId, taskId);

        return success(null, "Assign이 성공적으로 삭제되었습니다.");
    }
}
