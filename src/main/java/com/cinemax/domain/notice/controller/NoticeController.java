package com.cinemax.domain.notice.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.notice.dto.NoticeRequest;
import com.cinemax.domain.notice.dto.NoticeResponse;
import com.cinemax.domain.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Notice API Controller
 * 공지사항 관리 API
 */
@Slf4j
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
@Tag(name = "Notice", description = "공지사항 API")
public class NoticeController extends BaseController {

    private final NoticeService noticeService;

    /**
     * 공지사항 생성 (교수자/관리자)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "공지사항 생성", description = "새로운 공지사항을 생성합니다.")
    public ResponseEntity<ApiResponse<NoticeResponse>> createNotice(
            Authentication authentication,
            @Valid @RequestBody NoticeRequest request) {

        Long authorId = Long.parseLong(authentication.getName());
        log.info("공지사항 생성 요청: authorId={}, request={}", authorId, request);

        NoticeResponse response = noticeService.createNotice(authorId, request);
        return created(response, "공지사항 생성 성공");
    }

    /**
     * 공지사항 수정 (작성자)
     */
    @PutMapping("/{noticeId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "공지사항 수정", description = "기존 공지사항을 수정합니다. 작성자만 수정 가능합니다.")
    public ResponseEntity<ApiResponse<NoticeResponse>> updateNotice(
            Authentication authentication,
            @Parameter(description = "공지사항 ID") @PathVariable Long noticeId,
            @Valid @RequestBody NoticeRequest request) {

        Long authorId = Long.parseLong(authentication.getName());
        log.info("공지사항 수정 요청: noticeId={}, authorId={}", noticeId, authorId);

        NoticeResponse response = noticeService.updateNotice(noticeId, authorId, request);
        return success(response, "공지사항 수정 성공");
    }

    /**
     * 공지사항 삭제 (작성자)
     */
    @DeleteMapping("/{noticeId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다. 작성자만 삭제 가능합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            Authentication authentication,
            @Parameter(description = "공지사항 ID") @PathVariable Long noticeId) {

        Long authorId = Long.parseLong(authentication.getName());
        log.info("공지사항 삭제 요청: noticeId={}, authorId={}", noticeId, authorId);

        noticeService.deleteNotice(noticeId, authorId);
        return success(null, "공지사항 삭제 성공");
    }

    /**
     * 공지사항 단건 조회 (모든 사용자)
     * 조회 시 조회수 증가
     */
    @GetMapping("/{noticeId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "공지사항 상세 조회", description = "공지사항의 상세 정보를 조회합니다. 조회 시 조회수가 증가합니다.")
    public ResponseEntity<ApiResponse<NoticeResponse>> getNoticeById(
            @Parameter(description = "공지사항 ID") @PathVariable Long noticeId) {

        log.info("공지사항 조회 요청: noticeId={}", noticeId);
        NoticeResponse response = noticeService.getNoticeById(noticeId);

        return success(response, "공지사항 조회 성공");
    }

    /**
     * 전체 공지사항 목록 조회 (모든 사용자, 페이징)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "전체 공지사항 목록 조회", description = "전체 공지사항 목록을 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<Page<NoticeResponse>>> getAllNotices(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        log.info("전체 공지사항 목록 조회 요청: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeResponse> responses = noticeService.getAllNotices(pageable);

        return success(responses, "전체 공지사항 목록 조회 성공");
    }

    /**
     * 수업별 공지사항 목록 조회 (모든 사용자, 페이징)
     */
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업별 공지사항 목록 조회", description = "특정 수업의 공지사항 목록을 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<Page<NoticeResponse>>> getNoticesByClass(
            @Parameter(description = "수업 ID") @PathVariable Long classId,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        log.info("수업별 공지사항 목록 조회 요청: classId={}, page={}, size={}", classId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeResponse> responses = noticeService.getNoticesByClass(classId, pageable);

        return success(responses, "수업별 공지사항 목록 조회 성공");
    }

    /**
     * 중요 공지사항 목록 조회 (모든 사용자)
     */
    @GetMapping("/important")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "중요 공지사항 목록 조회", description = "중요 표시된 전체 공지사항 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getImportantNotices() {

        log.info("중요 공지사항 목록 조회 요청");
        List<NoticeResponse> responses = noticeService.getImportantNotices();

        return success(responses, "중요 공지사항 목록 조회 성공");
    }

    /**
     * 수업별 중요 공지사항 조회 (모든 사용자)
     */
    @GetMapping("/important/class/{classId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업별 중요 공지사항 조회", description = "특정 수업의 중요 공지사항 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getImportantNoticesByClass(
            @Parameter(description = "수업 ID") @PathVariable Long classId) {

        log.info("수업별 중요 공지사항 조회 요청: classId={}", classId);
        List<NoticeResponse> responses = noticeService.getImportantNoticesByClass(classId);

        return success(responses, "수업별 중요 공지사항 조회 성공");
    }

    /**
     * 공지사항 검색 (모든 사용자, 페이징)
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "공지사항 검색", description = "제목으로 공지사항을 검색합니다.")
    public ResponseEntity<ApiResponse<Page<NoticeResponse>>> searchNotices(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        log.info("공지사항 검색 요청: keyword={}, page={}, size={}", keyword, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeResponse> responses = noticeService.searchNotices(keyword, pageable);

        return success(responses, "공지사항 검색 성공");
    }

    /**
     * 수업별 공지사항 검색 (모든 사용자, 페이징)
     */
    @GetMapping("/search/class/{classId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업별 공지사항 검색", description = "특정 수업 내에서 제목으로 공지사항을 검색합니다.")
    public ResponseEntity<ApiResponse<Page<NoticeResponse>>> searchNoticesByClass(
            @Parameter(description = "수업 ID") @PathVariable Long classId,
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        log.info("수업별 공지사항 검색 요청: classId={}, keyword={}", classId, keyword);
        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeResponse> responses = noticeService.searchNoticesByClass(classId, keyword, pageable);

        return success(responses, "수업별 공지사항 검색 성공");
    }

    /**
     * 작성자별 공지사항 조회 (본인 또는 관리자)
     */
    @GetMapping("/author/{authorId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "작성자별 공지사항 조회", description = "특정 작성자가 작성한 공지사항 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getNoticesByAuthor(
            @Parameter(description = "작성자 ID") @PathVariable Long authorId) {

        log.info("작성자별 공지사항 조회 요청: authorId={}", authorId);
        List<NoticeResponse> responses = noticeService.getNoticesByAuthor(authorId);

        return success(responses, "작성자별 공지사항 조회 성공");
    }
}
