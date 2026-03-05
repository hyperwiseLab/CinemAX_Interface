package com.cinemax.domain.brief.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.brief.dto.BriefRequest;
import com.cinemax.domain.brief.dto.BriefResponse;
import com.cinemax.domain.brief.service.BriefService;
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
 * Brief 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/briefs")
@RequiredArgsConstructor
@Tag(name = "Brief", description = "Briefing 관리 API")
public class BriefController extends BaseController {

    private final BriefService briefService;

    // Brief 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Brief 생성", description = "새로운 Briefing을 생성합니다.")
    public ResponseEntity<ApiResponse<BriefResponse>> createBrief(@Valid @RequestBody BriefRequest request) {

        BriefResponse response = briefService.createBrief(request);

        return created(response, "Brief가 성공적으로 생성되었습니다.");
    }

    // Brief 조회 (단일)
    @GetMapping("/{briefId}/{taskId}")
    @Operation(summary = "Brief 조회", description = "Brief 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<BriefResponse>> getBrief(@Parameter(description = "Brief ID") @PathVariable Long briefId,
                                                               @Parameter(description = "Task ID") @PathVariable Long taskId) {

        BriefResponse response = briefService.getBrief(briefId, taskId);

        return success(response, "Brief 조회 성공");
    }

    // 전체 Brief 조회
    @GetMapping
    @Operation(summary = "전체 Brief 목록 조회", description = "모든 Briefing을 조회합니다.")
    public ResponseEntity<ApiResponse<List<BriefResponse>>> getAllBriefs() {

        List<BriefResponse> responses = briefService.getAllBriefs();

        return success(responses, "전체 Brief 목록 조회 성공");
    }

    // Task별 Brief 조회
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Task별 Brief 조회", description = "특정 Task의 모든 Briefing을 조회합니다.")
    public ResponseEntity<ApiResponse<List<BriefResponse>>> getBriefsByTaskId(@Parameter(description = "Task ID") @PathVariable Long taskId) {

        List<BriefResponse> responses = briefService.getBriefsByTaskId(taskId);

        return success(responses, "Task별 Brief 조회 성공");
    }

    // 제목으로 Brief 검색
    @GetMapping("/search")
    @Operation(summary = "Brief 검색", description = "제목으로 Briefing을 검색합니다.")
    public ResponseEntity<ApiResponse<List<BriefResponse>>> searchBriefsByTitle(@Parameter(description = "검색 키워드") @RequestParam String keyword) {

        List<BriefResponse> responses = briefService.searchBriefsByTitle(keyword);

        return success(responses, "Brief 검색 성공");
    }

    // Brief 수정
    @PutMapping("/{briefId}/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Brief 수정", description = "Brief 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<BriefResponse>> updateBrief(@Parameter(description = "Brief ID") @PathVariable Long briefId,
                                                                  @Parameter(description = "Task ID") @PathVariable Long taskId,
                                                                  @Valid @RequestBody BriefRequest request) {

        BriefResponse response = briefService.updateBrief(briefId, taskId, request);

        return success(response, "Brief가 성공적으로 수정되었습니다.");
    }

    // Brief 삭제
    @DeleteMapping("/{briefId}/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Brief 삭제", description = "Brief를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteBrief(@Parameter(description = "Brief ID") @PathVariable Long briefId,
                                                         @Parameter(description = "Task ID") @PathVariable Long taskId) {

        briefService.deleteBrief(briefId, taskId);

        return success(null, "Brief가 성공적으로 삭제되었습니다.");
    }
}
