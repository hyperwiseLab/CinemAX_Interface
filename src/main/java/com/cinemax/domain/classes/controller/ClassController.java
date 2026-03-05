package com.cinemax.domain.classes.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.core.error.enums.ErrorCode;
import com.cinemax.core.exception.AuthenticationException;
import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.classes.dto.*;
import com.cinemax.domain.classes.service.ClassService;
import com.cinemax.domain.classes.dto.ClassScheduleResponse;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.global.enums.EnrollStatus;
import com.cinemax.global.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 수업 관리 API
 */
@Slf4j
@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
@Tag(name = "Class", description = "수업 관리 API")
public class ClassController extends BaseController {

    private final ClassService classService;
    private final UserRepository userRepository;

    // 전체 수업 목록 조회 (관리자 전용)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "전체 수업 목록 조회", description = "관리자만 전체 수업 목록을 조회할 수 있습니다. 활성화 상태 및 교수자 ID로 필터링 가능합니다.")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getAllClasses(
            @Parameter(description = "활성화 상태 필터") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "교수자 ID 필터") @RequestParam(required = false) Long professorId) {

        List<ClassResponse> responses = classService.getAllClasses(isActive, professorId);

        return success(responses, "전체 수업 목록 조회 성공");
    }

    // 수업 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업 생성", description = "새로운 수업을 생성합니다.")
    public ResponseEntity<ApiResponse<ClassResponse>> createClass(@Valid @RequestBody ClassRequest request, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        String email = authentication.getName();
        ClassResponse response = classService.createClass(request, email);

        return created(response, "수업이 성공적으로 생성되었습니다.");
    }

    // 수업 조회
    @GetMapping("/{classId}")
    @Operation(summary = "수업 조회", description = "수업 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ClassResponse>> getClass(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        ClassResponse response = classService.getClass(classId);
        
        return success(response, "수업 조회 성공");
    }

    // 교수자의 수업 목록 조회
    @GetMapping("/professor")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "교수자 수업 목록 조회", description = "교수자의 모든 수업을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getProfessorClasses(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        String email = authentication.getName();
        User professor = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("교수자 정보를 찾을 수 없습니다: " + email));

        List<ClassResponse> responses = classService.getProfessorClasses(professor.getUserId());

        return success(responses, "교수자 수업 목록 조회 성공");
    }

    // 교수자의 수업 목록 + 학생 목록 조회
    @GetMapping("/professor/with-students")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "교수자 수업 목록 + 학생 목록 조회", description = "교수자의 모든 수업과 각 수업의 학생 목록을 한 번에 조회합니다.")
    public ResponseEntity<ApiResponse<List<ClassWithStudentsResponse>>> getProfessorClassesWithStudents(
            @AuthenticationPrincipal CustomUserDetailsService userDetails) {

        List<ClassWithStudentsResponse> responses = classService.getProfessorClassesWithStudents(userDetails.getUserId());

        return success(responses, "교수자 수업 및 학생 목록 조회 성공");
    }

    // 학생의 수강 수업 목록 조회
    @GetMapping("/student")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "학생 수강 수업 목록 조회", description = "학생이 수강 중인 수업을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getStudentClasses(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException(ErrorCode.UNAUTHORIZED, "인증된 사용자 정보를 찾을 수 없습니다.");
        }

        String email = authentication.getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "학생 정보를 찾을 수 없습니다: " + email
                ));

        List<ClassResponse> responses = classService.getStudentClasses(student.getUserId());

        return success(responses, "학생 수강 수업 목록 조회 성공");
    }

    // 학생의 삭제된 수업 목록 조회 (읽기 전용)
    @GetMapping("/student/deleted")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "학생 삭제된 수업 목록 조회", description = "학생이 과거에 수강했던 교수가 삭제한 수업을 조회합니다. 읽기 전용입니다.")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getDeletedClasses(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException(ErrorCode.UNAUTHORIZED, "인증된 사용자 정보를 찾을 수 없습니다.");
        }

        String email = authentication.getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "학생 정보를 찾을 수 없습니다: " + email
                ));

        List<ClassResponse> responses = classService.getDeletedClasses(student.getUserId());

        return success(responses, "삭제된 수업 목록 조회 성공");
    }

    // 수업 수정
    @PutMapping("/{classId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업 수정", description = "수업 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<ClassResponse>> updateClass(@Parameter(description = "수업 ID") @PathVariable Long classId,
                                                                  @Valid @RequestBody ClassRequest request) {

        ClassResponse response = classService.updateClass(classId, request);
        
        return success(response, "수업이 성공적으로 수정되었습니다.");
    }

    // 수업 비활성화
    @PatchMapping("/{classId}/deactivate")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업 비활성화", description = "수업을 비활성화합니다.")
    public ResponseEntity<ApiResponse<Void>> deactivateClass(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        classService.deactivateClass(classId);
        
        return success(null, "수업이 성공적으로 비활성화되었습니다.");
    }

    // 수업 활성화
    @PatchMapping("/{classId}/activate")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업 활성화", description = "수업을 활성화합니다.")
    public ResponseEntity<ApiResponse<Void>> activateClass(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        classService.activateClass(classId);
        
        return success(null, "수업이 성공적으로 활성화되었습니다.");
    }

    // 수업 삭제
    @DeleteMapping("/{classId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업 삭제", description = "수업을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        classService.deleteClass(classId);
        
        return success(null, "수업이 성공적으로 삭제되었습니다.");
    }

    // 수업 초대 코드 생성
    @PostMapping("/{classId}/invite")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업 초대 코드 생성", description = "수업의 초대 코드를 생성합니다.")
    public ResponseEntity<ApiResponse<ClassInviteResponse>> createInviteCode(@Parameter(description = "수업 ID") @PathVariable Long classId) {
        ClassInviteResponse response = classService.createInviteCode(classId);
        
        return created(response, "초대 코드가 성공적으로 생성되었습니다.");
    }

    // 초대 코드 재발급(기존 무효화 후 신규 발급)
    @PostMapping("/{classId}/invite/rotate")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업 초대 코드 재발급", description = "기존 초대 코드를 모두 무효화하고 새 코드를 발급합니다.")
    public ResponseEntity<ApiResponse<ClassInviteResponse>> rotateInviteCode(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        ClassInviteResponse response = classService.rotateInviteCode(classId);

        return created(response, "초대 코드가 성공적으로 재발급되었습니다.");
    }

    // 초대 코드로 수업 조회
    @GetMapping("/invite/{inviteCode}")
    @Operation(summary = "초대 코드로 수업 조회", description = "초대 코드로 수업 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ClassResponse>> getClassByInviteCode(@Parameter(description = "초대 코드") @PathVariable String inviteCode) {

        ClassResponse response = classService.getClassByInviteCode(inviteCode);
        
        return success(response, "초대 코드로 수업 조회 성공");
    }

    // 초대 ID로 수업 조회
    @GetMapping("/invite-id/{inviteId}")
    @Operation(summary = "초대 ID로 수업 조회", description = "초대 ID로 수업 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ClassResponse>> getClassByInviteId(@Parameter(description = "초대 ID") @PathVariable Long inviteId) {

        ClassResponse response = classService.getClassByInviteId(inviteId);
        
        return success(response, "초대 ID로 수업 조회 성공");
    }

    // 활성 초대 코드의 QR 정보 조회
    @GetMapping("/{classId}/invite/qr")
    @Operation(summary = "수업 초대 QR 조회", description = "현재 활성화된 초대의 QR 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<ClassInviteResponse>> getActiveInviteQr(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        ClassInviteResponse response = classService.getActiveInvite(classId);

        return success(response, "활성 초대 QR 조회 성공");
    }

    // 초대 링크 기반 QR PNG 생성
    @GetMapping(value = "/{classId}/invite/qr-image", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "초대 링크 QR 이미지 생성", description = "활성 초대코드를 포함한 링크를 QR PNG로 반환합니다. baseUrl, size(128~1024) 지정 가능")
    public @ResponseBody byte[] getInviteQrImage(@Parameter(description = "수업 ID") @PathVariable Long classId,
                                                 @Parameter(description = "절대 base URL (예: https://cinemax.com)") @RequestParam(required = false) String baseUrl,
                                                 @Parameter(description = "QR 사이즈(px), 128~1024") @RequestParam(required = false) Integer size) {

        return classService.getInviteQrImage(classId, baseUrl, size);
    }

    // 수업 수강 신청
    @PostMapping("/{classId}/enroll")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "수업 수강 신청", description = "수업에 수강 신청합니다.")
    public ResponseEntity<ApiResponse<ClassEnrollResponse>> enrollClass(@Parameter(description = "수업 ID") @PathVariable Long classId,
                                                                        Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        String email = authentication.getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다: " + email));

        ClassEnrollResponse response = classService.enrollClass(classId, student);
        
        return created(response, "수강 신청이 성공적으로 완료되었습니다.");
    }

    // 수강 신청 취소
    @DeleteMapping("/{classId}/enroll")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "수강 신청 취소", description = "수강 신청을 취소합니다. 탈퇴 사유는 선택사항입니다.")
    public ResponseEntity<ApiResponse<Void>> cancelEnrollment(@Parameter(description = "수업 ID") @PathVariable Long classId,
                                                              @Parameter(description = "탈퇴 사유 (선택)") @RequestParam(required = false) String withdrawReason,
                                                              Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        String email = authentication.getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다: " + email));

        classService.cancelEnrollment(classId, student.getUserId(), withdrawReason);

        return success(null, "수강 신청이 성공적으로 취소되었습니다.");
    }

    // 교수자의 학생 수동 제거
    @DeleteMapping("/{classId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "학생 제거", description = "교수자가 수업에서 학생을 제거합니다.")
    public ResponseEntity<ApiResponse<Void>> removeStudent(@Parameter(description = "수업 ID") @PathVariable Long classId,
                                                            @Parameter(description = "학생 ID") @PathVariable Long studentId,
                                                            @Parameter(description = "제거 사유 (선택)") @RequestParam(required = false) String reason) {

        classService.cancelEnrollment(classId, studentId, reason);

        return success(null, "학생이 성공적으로 제거되었습니다.");
    }

    // 초대 코드로 참여(재참여 포함)
    @PostMapping("/join-by-code/{inviteCode}")
    @Operation(summary = "초대 코드로 참여", description = "초대 코드로 수업에 참여합니다. 기존 탈퇴 이력이 있으면 재참여 처리합니다.")
    public ResponseEntity<ApiResponse<ClassEnrollResponse>> joinByInviteCode(@Parameter(description = "초대 코드") @PathVariable String inviteCode,
                                                                             Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다. 회원가입 후 다시 시도해주세요.");
        }

        ClassEnrollResponse response = classService.joinByInviteCode(inviteCode, authentication.getName());

        return created(response, "수업 참여가 완료되었습니다.");
    }

    // 수업 수강생 목록 조회
    @GetMapping("/{classId}/enrollments")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "수업 수강생 목록 조회", description = "수업의 수강생 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ClassEnrollResponse>>> getClassEnrollments(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        List<ClassEnrollResponse> responses = classService.getClassEnrollments(classId);
        
        return success(responses, "수업 수강생 목록 조회 성공");
    }

    // 상태별 수강생 조회
    @GetMapping("/{classId}/enrollments/status/{status}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "상태별 수강생 조회", description = "특정 상태의 수강생을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ClassEnrollResponse>>> getEnrollmentsByStatus(@Parameter(description = "수업 ID") @PathVariable Long classId,
                                                                                         @Parameter(description = "수강 상태") @PathVariable EnrollStatus status) {

        List<ClassEnrollResponse> responses = classService.getEnrollmentsByStatus(classId, status);

        return success(responses, "상태별 수강생 조회 성공");
    }

    // 교수자의 수업 일정 조회
    @GetMapping("/professor/{userId}/schedule")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "교수자 수업 일정 조회", description = "교수자의 모든 수업과 주차별 세션을 달력 형태로 조회합니다.")
    public ResponseEntity<ApiResponse<List<ClassScheduleResponse>>> getClassSchedule(@Parameter(description = "교수자 ID") @PathVariable Long professorId) {

        List<ClassScheduleResponse> responses = classService.getClassSchedule(professorId);

        return success(responses, "교수자 수업 일정 조회 성공");
    }

    // 교수자의 당일 수업 목록 조회
    @GetMapping("/professor/{userId}/today")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "교수자 당일 수업 목록 조회", description = "교수자의 오늘 진행 중인 수업 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ClassScheduleResponse>>> getTodayClasses(@Parameter(description = "교수자 ID") @PathVariable Long professorId) {

        List<ClassScheduleResponse> responses = classService.getTodayClasses(professorId);

        return success(responses, "당일 수업 목록 조회 성공");
    }
}
