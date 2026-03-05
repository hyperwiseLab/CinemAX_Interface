package com.cinemax.domain.lecture.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.lecture.dto.LectureRequest;
import com.cinemax.domain.lecture.dto.LectureResponse;
import com.cinemax.domain.lecture.service.LectureService;
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
 * Lecture 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/lectures")
@RequiredArgsConstructor
@Tag(name = "Lecture", description = "Lecture 관리 API (강의 노트)")
public class LectureController extends BaseController {

    private final LectureService lectureService;

    // Lecture 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Lecture 생성", description = "새로운 Lecture를 생성합니다.")
    public ResponseEntity<ApiResponse<LectureResponse>> createLecture(@Valid @RequestBody LectureRequest request) {

        LectureResponse response = lectureService.createLecture(request);

        return created(response, "Lecture가 성공적으로 생성되었습니다.");
    }

    // Lecture 조회 (단일)
    @GetMapping("/{lectureId}/{taskId}")
    @Operation(summary = "Lecture 조회", description = "Lecture 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<LectureResponse>> getLecture(@Parameter(description = "Lecture ID") @PathVariable Long lectureId,
                                                                   @Parameter(description = "Task ID") @PathVariable Long taskId) {

        LectureResponse response = lectureService.getLecture(lectureId, taskId);

        return success(response, "Lecture 조회 성공");
    }

    // 전체 Lecture 조회
    @GetMapping
    @Operation(summary = "전체 Lecture 목록 조회", description = "모든 Lecture를 조회합니다.")
    public ResponseEntity<ApiResponse<List<LectureResponse>>> getAllLectures() {

        List<LectureResponse> responses = lectureService.getAllLectures();

        return success(responses, "전체 Lecture 목록 조회 성공");
    }

    // Task별 Lecture 조회
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Task별 Lecture 조회", description = "특정 Task의 모든 Lecture를 조회합니다.")
    public ResponseEntity<ApiResponse<List<LectureResponse>>> getLecturesByTaskId(@Parameter(description = "Task ID") @PathVariable Long taskId) {

        List<LectureResponse> responses = lectureService.getLecturesByTaskId(taskId);

        return success(responses, "Task별 Lecture 조회 성공");
    }

    // 제목으로 Lecture 검색
    @GetMapping("/search")
    @Operation(summary = "Lecture 검색", description = "제목으로 Lecture를 검색합니다.")
    public ResponseEntity<ApiResponse<List<LectureResponse>>> searchLecturesByTitle(@Parameter(description = "검색 키워드") @RequestParam String keyword) {

        List<LectureResponse> responses = lectureService.searchLecturesByTitle(keyword);

        return success(responses, "Lecture 검색 성공");
    }

    // Lecture 수정
    @PutMapping("/{lectureId}/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Lecture 수정", description = "Lecture 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<LectureResponse>> updateLecture(@Parameter(description = "Lecture ID") @PathVariable Long lectureId,
                                                                      @Parameter(description = "Task ID") @PathVariable Long taskId,
                                                                      @Valid @RequestBody LectureRequest request) {

        LectureResponse response = lectureService.updateLecture(lectureId, taskId, request);

        return success(response, "Lecture가 성공적으로 수정되었습니다.");
    }

    // Lecture 삭제
    @DeleteMapping("/{lectureId}/{taskId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "Lecture 삭제", description = "Lecture를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteLecture(@Parameter(description = "Lecture ID") @PathVariable Long lectureId,
                                                           @Parameter(description = "Task ID") @PathVariable Long taskId) {

        lectureService.deleteLecture(lectureId, taskId);

        return success(null, "Lecture가 성공적으로 삭제되었습니다.");
    }
}
