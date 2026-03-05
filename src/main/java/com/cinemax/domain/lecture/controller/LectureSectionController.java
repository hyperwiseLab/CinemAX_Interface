package com.cinemax.domain.lecture.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.lecture.dto.LectureSectionRequest;
import com.cinemax.domain.lecture.dto.LectureSectionResponse;
import com.cinemax.domain.lecture.service.LectureSectionService;
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
 * LectureSection 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/lecture-sections")
@RequiredArgsConstructor
@Tag(name = "LectureSection", description = "강의 섹션 관리 API")
public class LectureSectionController extends BaseController {

    private final LectureSectionService lectureSectionService;

    // LectureSection 생성
    @PostMapping("/{lectureId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "LectureSection 생성", description = "새로운 LectureSection을 생성합니다.")
    public ResponseEntity<ApiResponse<LectureSectionResponse>> createLectureSection(
            @PathVariable Long lectureId,
            @Valid @RequestBody LectureSectionRequest request) {

        LectureSectionResponse response = lectureSectionService.createLectureSection(lectureId, request);

        return created(response, "LectureSection이 성공적으로 생성되었습니다.");
    }

    // LectureSection 조회 (단일)
    @GetMapping("/{lectureId}/{sectionId}")
    @Operation(summary = "LectureSection 조회", description = "LectureSection 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<LectureSectionResponse>> getLectureSection(@Parameter(description = "Lecture ID") @PathVariable Long lectureId,
                                                                                 @Parameter(description = "Section ID") @PathVariable Long sectionId) {

        LectureSectionResponse response = lectureSectionService.getLectureSection(lectureId, sectionId);

        return success(response, "LectureSection 조회 성공");
    }

    // Lecture별 Section 목록 조회
    @GetMapping("/lecture/{lectureId}")
    @Operation(summary = "Lecture별 Section 목록 조회", description = "특정 Lecture의 모든 Section을 조회합니다.")
    public ResponseEntity<ApiResponse<List<LectureSectionResponse>>> getSectionsByLectureId(@Parameter(description = "Lecture ID") @PathVariable Long lectureId) {

        List<LectureSectionResponse> responses = lectureSectionService.getSectionsByLectureId(lectureId);

        return success(responses, "Lecture별 Section 목록 조회 성공");
    }

    // 제목으로 Section 검색
    @GetMapping("/search")
    @Operation(summary = "LectureSection 검색", description = "제목으로 LectureSection을 검색합니다.")
    public ResponseEntity<ApiResponse<List<LectureSectionResponse>>> searchSectionsByHeading(@Parameter(description = "검색 키워드") @RequestParam String keyword) {

        List<LectureSectionResponse> responses = lectureSectionService.searchSectionsByHeading(keyword);

        return success(responses, "LectureSection 검색 성공");
    }

    // LectureSection 수정
    @PutMapping("/{lectureId}/{sectionId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "LectureSection 수정", description = "LectureSection 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<LectureSectionResponse>> updateLectureSection(@Parameter(description = "Lecture ID") @PathVariable Long lectureId,
                                                                                    @Parameter(description = "Section ID") @PathVariable Long sectionId,
                                                                                    @Valid @RequestBody LectureSectionRequest request) {

        LectureSectionResponse response = lectureSectionService.updateLectureSection(lectureId, sectionId, request);

        return success(response, "LectureSection이 성공적으로 수정되었습니다.");
    }

    // LectureSection 삭제
    @DeleteMapping("/{lectureId}/{sectionId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "LectureSection 삭제", description = "LectureSection을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteLectureSection(@Parameter(description = "Lecture ID") @PathVariable Long lectureId,
                                                                  @Parameter(description = "Section ID") @PathVariable Long sectionId) {

        lectureSectionService.deleteLectureSection(lectureId, sectionId);

        return success(null, "LectureSection이 성공적으로 삭제되었습니다.");
    }
}
