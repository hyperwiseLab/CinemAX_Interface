package com.cinemax.domain.curriculum.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.curriculum.dto.CurriculumRequest;
import com.cinemax.domain.curriculum.dto.CurriculumResponse;
import com.cinemax.domain.curriculum.service.CurriculumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 커리큘럼 관리 API Controller
 * 관리자 전용 API
 */
@Slf4j
@RestController
@RequestMapping("/curriculums")
@RequiredArgsConstructor
@Tag(name = "Curriculum", description = "커리큘럼 관리 API")
public class CurriculumController extends BaseController {

    private final CurriculumService curriculumService;

    // 커리큘럼 생성
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "커리큘럼 생성", description = "새로운 커리큘럼을 생성합니다.")
    public ResponseEntity<ApiResponse<CurriculumResponse>> createCurriculum(@Valid @RequestBody CurriculumRequest request) {

        CurriculumResponse response = curriculumService.createCurriculum(request);
        
        return created(response, "커리큘럼이 성공적으로 생성되었습니다.");
    }

    // 커리큘럼 조회
    @GetMapping("/{curId}")
    @Operation(summary = "커리큘럼 조회", description = "커리큘럼 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<CurriculumResponse>> getCurriculum(@Parameter(description = "커리큘럼 ID") @PathVariable Long curId) {

        CurriculumResponse response = curriculumService.getCurriculum(curId);
        
        return success(response, "커리큘럼 조회 성공");
    }

    // 커리큘럼 상세 조회 (주차 정보 포함)
    @GetMapping("/{curId}/detail")
    @Operation(summary = "커리큘럼 상세 조회", description = "커리큘럼과 주차별 정보를 함께 조회합니다.")
    public ResponseEntity<ApiResponse<CurriculumResponse>> getCurriculumWithWeeks(@Parameter(description = "커리큘럼 ID") @PathVariable Long curId) {

        CurriculumResponse response = curriculumService.getCurriculumWithWeeks(curId);
        
        return success(response, "커리큘럼 상세 조회 성공");
    }

    // 모든 커리큘럼 조회 (활성화 여부 무관)
    @GetMapping("/all")
    @Operation(summary = "전체 커리큘럼 목록 조회", description = "활성화 여부와 관계없이 모든 커리큘럼을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CurriculumResponse>>> getAllCurriculums() {

        List<CurriculumResponse> responses = curriculumService.getAllCurriculums();

        return success(responses, "전체 커리큘럼 목록 조회 성공");
    }

    // 모든 활성화된 커리큘럼 조회
    @GetMapping
    @Operation(summary = "활성화된 커리큘럼 목록 조회", description = "활성화된 모든 커리큘럼을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CurriculumResponse>>> getAllActiveCurriculums() {

        List<CurriculumResponse> responses = curriculumService.getAllActiveCurriculums();

        return success(responses, "활성화된 커리큘럼 목록 조회 성공");
    }

    // 언어별 활성화된 커리큘럼 조회
    @GetMapping("/language/{lang}")
    @Operation(summary = "언어별 커리큘럼 조회", description = "특정 언어의 활성화된 커리큘럼을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CurriculumResponse>>> getActiveCurriculumsByLang(@Parameter(description = "언어") @PathVariable String lang) {

        List<CurriculumResponse> responses = curriculumService.getActiveCurriculumsByLang(lang);
        
        return success(responses, "언어별 커리큘럼 조회 성공");
    }

    // 커리큘럼 수정
    @PutMapping("/{curId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "커리큘럼 수정", description = "커리큘럼 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<CurriculumResponse>> updateCurriculum(@Parameter(description = "커리큘럼 ID") @PathVariable Long curId,
                                                                            @Valid @RequestBody CurriculumRequest request) {
        CurriculumResponse response = curriculumService.updateCurriculum(curId, request);
        
        return success(response, "커리큘럼이 성공적으로 수정되었습니다.");
    }

    // 커리큘럼 비활성화
    @PatchMapping("/{curId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "커리큘럼 비활성화", description = "커리큘럼을 비활성화합니다.")
    public ResponseEntity<ApiResponse<Void>> deactivateCurriculum(@Parameter(description = "커리큘럼 ID") @PathVariable Long curId) {

        curriculumService.deactivateCurriculum(curId);
        
        return success(null, "커리큘럼이 성공적으로 비활성화되었습니다.");
    }

    // 커리큘럼 활성화
    @PatchMapping("/{curId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "커리큘럼 활성화", description = "커리큘럼을 활성화합니다.")
    public ResponseEntity<ApiResponse<Void>> activateCurriculum(@Parameter(description = "커리큘럼 ID") @PathVariable Long curId) {

        curriculumService.activateCurriculum(curId);
        
        return success(null, "커리큘럼이 성공적으로 활성화되었습니다.");
    }

    // 커리큘럼 삭제
    @DeleteMapping("/{curId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "커리큘럼 삭제", description = "커리큘럼을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteCurriculum(@Parameter(description = "커리큘럼 ID") @PathVariable Long curId) {

        curriculumService.deleteCurriculum(curId);

        return success(null, "커리큘럼이 성공적으로 삭제되었습니다.");
    }

    // 특정 커리큘럼으로 생성된 수업 개수 조회
    @GetMapping("/{curId}/class-count")
    @Operation(summary = "커리큘럼별 수업 개수 조회", description = "특정 커리큘럼으로 생성된 수업의 개수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> getClassCountByCurriculum(@Parameter(description = "커리큘럼 ID") @PathVariable Long curId) {

        Long count = curriculumService.getClassCountByCurriculum(curId);

        return success(count, "커리큘럼별 수업 개수 조회 성공");
    }
}
