package com.cinemax.domain.hint.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.hint.dto.HintRequest;
import com.cinemax.domain.hint.dto.HintResponse;
import com.cinemax.domain.hint.service.HintService;
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
 * Hint 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/hints")
@RequiredArgsConstructor
@Tag(name = "Hint", description = "힌트 관리 API")
public class HintController extends BaseController {

    private final HintService hintService;

    // Hint 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Hint 생성", description = "새로운 Hint를 생성합니다.")
    public ResponseEntity<ApiResponse<HintResponse>> createHint(@Valid @RequestBody HintRequest request) {
        log.info("Hint 생성 요청 - hintId: {}, taskId: {}", request.getHintId(), request.getTaskId());

        HintResponse response = hintService.createHint(request);

        return created(response, "Hint가 성공적으로 생성되었습니다.");
    }

    // Hint 조회 (단일)
    @GetMapping("/{hintId}/{taskId}")
    @Operation(summary = "Hint 조회", description = "Hint 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<HintResponse>> getHint(
            @Parameter(description = "Hint ID") @PathVariable Long hintId,
            @Parameter(description = "Task ID") @PathVariable Long taskId) {

        HintResponse response = hintService.getHint(hintId, taskId);

        return success(response, "Hint 조회 성공");
    }

    /**
     * 전체 Hint 조회
     */
    @GetMapping
    @Operation(summary = "전체 Hint 목록 조회", description = "모든 Hint를 조회합니다.")
    public ResponseEntity<ApiResponse<List<HintResponse>>> getAllHints() {

        List<HintResponse> responses = hintService.getAllHints();

        return success(responses, "전체 Hint 목록 조회 성공");
    }

    /**
     * Task별 Hint 조회
     */
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Task별 Hint 조회", description = "특정 Task의 모든 Hint를 조회합니다.")
    public ResponseEntity<ApiResponse<List<HintResponse>>> getHintsByTaskId(
            @Parameter(description = "Task ID") @PathVariable Long taskId) {

        List<HintResponse> responses = hintService.getHintsByTaskId(taskId);

        return success(responses, "Task별 Hint 조회 성공");
    }

    /**
     * 제목으로 Hint 검색
     */
    @GetMapping("/search")
    @Operation(summary = "Hint 검색", description = "제목으로 Hint를 검색합니다.")
    public ResponseEntity<ApiResponse<List<HintResponse>>> searchHintsByTitle(
            @Parameter(description = "검색 키워드") @RequestParam String keyword) {

        List<HintResponse> responses = hintService.searchHintsByTitle(keyword);

        return success(responses, "Hint 검색 성공");
    }

    /**
     * Hint 수정
     */
    @PutMapping("/{hintId}/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Hint 수정", description = "Hint 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<HintResponse>> updateHint(
            @Parameter(description = "Hint ID") @PathVariable Long hintId,
            @Parameter(description = "Task ID") @PathVariable Long taskId,
            @Valid @RequestBody HintRequest request) {

        HintResponse response = hintService.updateHint(hintId, taskId, request);

        return success(response, "Hint가 성공적으로 수정되었습니다.");
    }

    /**
     * Hint 삭제
     */
    @DeleteMapping("/{hintId}/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Hint 삭제", description = "Hint를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteHint(
            @Parameter(description = "Hint ID") @PathVariable Long hintId,
            @Parameter(description = "Task ID") @PathVariable Long taskId) {

        hintService.deleteHint(hintId, taskId);

        return success(null, "Hint가 성공적으로 삭제되었습니다.");
    }
}
