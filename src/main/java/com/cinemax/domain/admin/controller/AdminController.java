package com.cinemax.domain.admin.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.classes.dto.ClassResponse;
import com.cinemax.domain.classes.service.ClassService;
import com.cinemax.domain.curriculum.dto.CurriculumResponse;
import com.cinemax.domain.curriculum.service.CurriculumService;
import com.cinemax.domain.user.dto.UserResponse;
import com.cinemax.domain.user.service.UserService;
import com.cinemax.global.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자 전용 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "관리자 전용 API")
public class AdminController extends BaseController {

    private final UserService userService;
    private final CurriculumService curriculumService;
    private final ClassService classService;

    // 교수 목록 조회
    @GetMapping("/professors")
    @Operation(summary = "교수 목록 조회", description = "모든 교수 사용자를 조회합니다.")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getProfessors() {

        List<UserResponse> professors = userService.getUsersByRole(RoleType.PROFESSOR);

        return success(professors, "교수 목록 조회 성공");
    }

    // 수업 목록 조회
    @GetMapping("/classes")
    @Operation(summary = "수업 목록 조회", description = "모든 수업을 조회합니다 (활성화 여부 무관).")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getClasses(Boolean isActive, Long professorId) {

        List<ClassResponse> classResponseList = classService.getAllClasses(isActive, professorId);

        return success(classResponseList, "수업 목록 조회 성공");
    }

    // 커리큘럼 목록 조회
    @GetMapping("/curriculums")
    @Operation(summary = "커리큘럼 목록 조회", description = "모든 커리큘럼을 조회합니다 (활성화 여부 무관).")
    public ResponseEntity<ApiResponse<List<CurriculumResponse>>> getCurriculums() {

        List<CurriculumResponse> curriculums = curriculumService.getAllCurriculums();

        return success(curriculums, "커리큘럼 목록 조회 성공");
    }

    // 학생 목록 조회
    @GetMapping("/students")
    @Operation(summary = "학생 목록 조회", description = "모든 학생 사용자를 조회합니다.")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getStudents() {

        List<UserResponse> students = userService.getUsersByRole(RoleType.STUDENT);

        return success(students, "학생 목록 조회 성공");
    }

    // 전체 사용자 목록 조회
    @GetMapping("/users")
    @Operation(summary = "전체 사용자 목록 조회", description = "모든 사용자를 조회합니다.")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return success(users, "전체 사용자 목록 조회 성공");
    }
}
