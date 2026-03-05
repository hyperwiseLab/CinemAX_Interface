package com.cinemax.domain.cycle.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.cycle.dto.CycleRequest;
import com.cinemax.domain.cycle.dto.CycleResponse;
import com.cinemax.domain.cycle.service.CycleService;
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
 * 학습 사이클(주차별 학습 단위) 관리 API
 */
@Slf4j
@RestController
@RequestMapping("/cycles")
@RequiredArgsConstructor
@Tag(name = "Cycle", description = "학습 사이클 API - 주차별 학습 단위, 문법 인덱스 및 Task 컨테이너")
public class CycleController extends BaseController {

    private final CycleService cycleService;

    //Cycle 생성 (교수자/관리자)
    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Cycle 생성", description = "새로운 학습 사이클을 생성합니다.")
    public ResponseEntity<ApiResponse<CycleResponse>> createCycle(@Valid @RequestBody CycleRequest request) {

        CycleResponse response = cycleService.createCycle(request);

        return created(response, "Cycle 생성 성공");
    }

 
    //Cycle 수정 (교수자/관리자)
    @PutMapping("/{cycleId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Cycle 수정", description = "기존 학습 사이클의 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<CycleResponse>> updateCycle(@Parameter(description = "Cycle ID") @PathVariable Long cycleId,
                                                                  @Valid @RequestBody CycleRequest request) {

        CycleResponse response = cycleService.updateCycle(cycleId, request);

        return success(response, "Cycle 수정 성공");
    }

 
    // Cycle 삭제 (교수자/관리자)
    @DeleteMapping("/{cycleId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Cycle 삭제", description = "학습 사이클을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteCycle(@Parameter(description = "Cycle ID") @PathVariable Long cycleId) {

        cycleService.deleteCycle(cycleId);

        return success(null, "Cycle 삭제 성공");
    }


    /**
     * Cycle 단건 조회 (모든 사용자)
     * Task 목록 및 문법 인덱스 정보 포함
     */
    @GetMapping("/{cycleId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "Cycle 상세 조회", description = "학습 사이클의 상세 정보를 조회합니다. Task 목록(모드별)과 문법 인덱스 정보가 포함됩니다.")
    public ResponseEntity<ApiResponse<CycleResponse>> getCycleById(@Parameter(description = "Cycle ID") @PathVariable Long cycleId) {

        CycleResponse response = cycleService.getCycleById(cycleId);

        return success(response, "Cycle 조회 성공");
    }

    /**
     * 커리큘럼 주차별 Cycle 목록 조회 (모든 사용자)
     * 각 Cycle의 Task 목록 포함
     */
    @GetMapping("/week/{curWeekId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "주차별 Cycle 목록 조회", description = "특정 커리큘럼 주차의 모든 학습 사이클 목록을 조회합니다. 각 Cycle의 Task 목록이 포함됩니다.")
    public ResponseEntity<ApiResponse<List<CycleResponse>>> getCyclesByWeek(@Parameter(description = "커리큘럼 주차 ID") @PathVariable Long curWeekId) {

        List<CycleResponse> responses = cycleService.getCyclesByWeek(curWeekId);

        return success(responses, "주차별 Cycle 목록 조회 성공");
    }
}
