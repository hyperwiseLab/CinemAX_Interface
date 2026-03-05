package com.cinemax.domain.auth.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.auth.dto.PasswordResetRequest;
import com.cinemax.domain.auth.dto.PasswordResetResponse;
import com.cinemax.domain.auth.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 비밀번호 재설정 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/auth/temporary-password")
@RequiredArgsConstructor
@Tag(name = "Password Reset", description = "임시 비밀번호 발급 API")
public class PasswordResetController extends BaseController {

    private final PasswordResetService passwordResetService;

    // 임시 비밀번호 발급 요청
    @PostMapping("/request")
    @Operation(summary = "임시비밀번호 발급", description = "이메일로 임시 비밀번호를 발급합니다.")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {

        PasswordResetResponse response = passwordResetService.requestPasswordReset(request);

        return success(response, "임시 비밀번호가 이메일로 발급되었습니다.");
    }

    // 임시 비밀번호 발급 요청 취소
    @DeleteMapping("/cancel")
    @Operation(summary = "임시 비밀번호 발급 요청 취소", description = "진행 중인 임시 비밀번호 발급 요청을 취소합니다.")
    public ResponseEntity<ApiResponse<Void>> cancelPasswordResetRequest(@Parameter(description = "사용자 이메일") @RequestParam String email) {

        passwordResetService.cancelPasswordResetRequest(email);

        return success(null, "임시 비밀번호 발급 요청이 취소되었습니다.");
    }
}
