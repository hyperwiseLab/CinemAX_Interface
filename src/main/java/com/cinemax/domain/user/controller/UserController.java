package com.cinemax.domain.user.controller;

import com.cinemax.domain.user.dto.*;
import com.cinemax.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 정보 관련 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "사용자 정보 관리 API")
public class UserController {

    private final UserService userService;

    //현재 로그인한 사용자 정보 조회
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {

        UserResponse response = userService.getCurrentUser(authentication);

        return ResponseEntity.ok(response);
    }

    // 사용자 정보 수정 (본인)
    @PatchMapping("/me")
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 이름과 학번을 수정합니다.")
    public ResponseEntity<UserResponse> updateCurrentUser(Authentication authentication, @Valid @RequestBody UpdateUserRequest request) {

        UserResponse response = userService.updateCurrentUser(authentication, request);

        return ResponseEntity.ok(response);
    }

    // 비밀번호 변경 (현재 비밀번호 확인)
    @PatchMapping("/me/password")
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 새 비밀번호로 변경합니다. 임시비밀번호 사용자는 이 API로 비밀번호를 변경해야 합니다.")
    public ResponseEntity<Void> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(authentication, request);

        return ResponseEntity.noContent().build();
    }

    // 비밀번호 확인
    @PostMapping("/me/verify-password")
    @Operation(summary = "비밀번호 확인", description = "현재 로그인한 사용자의 비밀번호가 올바른지 확인합니다. 중요한 작업(회원탈퇴, 정보수정 등) 전에 사용할 수 있습니다.")
    public ResponseEntity<VerifyPasswordResponse> verifyPassword(Authentication authentication, @Valid @RequestBody VerifyPasswordRequest request) {

        VerifyPasswordResponse response = userService.verifyPassword(authentication, request);

        return ResponseEntity.ok(response);
    }

    // 비밀번호 만료 상태 확인
    @GetMapping("/me/password-expiry-status")
    @Operation(summary = "비밀번호 만료 상태 확인",
              description = "현재 로그인한 사용자의 비밀번호 만료 상태를 확인합니다. 6개월마다 비밀번호 변경을 권장합니다.")
    public ResponseEntity<PasswordExpiryStatusResponse> getPasswordExpiryStatus(Authentication authentication) {

        PasswordExpiryStatusResponse response = userService.getPasswordExpiryStatus(authentication);

        return ResponseEntity.ok(response);
    }

    // 사용자 ID로 사용자 정보 조회
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "사용자 정보 조회", description = "교수/관리자만 사용자 ID로 사용자 정보를 조회할 수 있습니다.")
    public ResponseEntity<UserResponse> getUserById(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        UserResponse response = userService.getUserById(userId);

        return ResponseEntity.ok(response);
    }

    // 이메일로 사용자 정보 조회
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(summary = "이메일로 사용자 조회", description = "교수/관리자만 이메일로 사용자 정보를 조회할 수 있습니다.")
    public ResponseEntity<UserResponse> getUserByEmail(@Parameter(description = "사용자 이메일") @PathVariable String email) {

        UserResponse response = userService.getUserByEmail(email);

        return ResponseEntity.ok(response);
    }

    // 사용자 비활성화 (관리자 전용)
    @PatchMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 비활성화", description = "관리자만 사용자를 비활성화할 수 있습니다.")
    public ResponseEntity<Void> deactivateUser(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        userService.deactivateUser(userId);

        return ResponseEntity.noContent().build();
    }

    // 사용자 활성화 (관리자 전용)
    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 활성화", description = "관리자만 사용자를 활성화할 수 있습니다.")
    public ResponseEntity<Void> activateUser(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        userService.activateUser(userId);

        return ResponseEntity.noContent().build();
    }

    // 전체 사용자 목록 조회 (관리자 전용)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "전체 사용자 목록 조회", description = "관리자만 전체 사용자 목록을 조회할 수 있습니다. 역할 및 활성화 상태로 필터링 가능합니다.")
    public ResponseEntity<java.util.List<UserResponse>> getAllUsers(
            @Parameter(description = "역할 필터 (STUDENT, PROFESSOR, ADMIN)") @RequestParam(required = false) String role,
            @Parameter(description = "활성화 상태 필터") @RequestParam(required = false) Boolean isActive) {

        java.util.List<UserResponse> responses = userService.getAllUsers(role, isActive);

        return ResponseEntity.ok(responses);
    }

    // 사용자 생성 (관리자 전용)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 생성", description = "관리자만 새로운 사용자를 생성할 수 있습니다.")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUserByAdmin(request);

        return ResponseEntity.status(201).body(response);
    }

    // 사용자 정보 수정 (관리자 전용)
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 정보 수정", description = "관리자만 사용자 정보를 수정할 수 있습니다.")
    public ResponseEntity<UserResponse> updateUserByAdmin(@Parameter(description = "사용자 ID") @PathVariable Long userId,
                                                          @Valid @RequestBody UpdateUserRequest request) {

        UserResponse response = userService.updateUserByAdmin(userId, request);

        return ResponseEntity.ok(response);
    }

    // 사용자 삭제 (관리자 전용)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 삭제", description = "관리자만 사용자를 삭제할 수 있습니다. 주의: 이 작업은 되돌릴 수 없습니다.")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
