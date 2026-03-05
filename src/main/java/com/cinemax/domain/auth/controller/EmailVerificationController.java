package com.cinemax.domain.auth.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.auth.dto.EmailVerificationRequest;
import com.cinemax.domain.auth.dto.EmailVerificationResponse;
import com.cinemax.domain.auth.dto.EmailVerificationStatusResponse;
import com.cinemax.domain.auth.dto.SendEmailVerificationRequest;
import com.cinemax.domain.auth.service.EmailVerificationService;
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

@Slf4j
@RestController
@RequestMapping("/email-verification")
@RequiredArgsConstructor
@Tag(name = "EmailVerification (Admin)", description = "이메일 인증 관리 ADMIN 전용 API")
public class EmailVerificationController extends BaseController {

    private final EmailVerificationService emailVerificationService;


    // 만료된 인증 코드 정리 (관리자용)
    @PostMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "만료된 인증 코드 정리", description = "만료된 인증 코드를 정리합니다.")
    public ResponseEntity<ApiResponse<String>> cleanupExpiredVerifications() {

        emailVerificationService.cleanupExpiredVerifications();
        
        return success("정리 완료", "만료된 인증 코드 정리 성공");
    }

    // 사용자 인증 코드 전체 삭제 (관리자용)
    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 인증 코드 전체 삭제", description = "특정 사용자의 모든 인증 코드를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteAllVerificationsByUserId(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        emailVerificationService.deleteAllVerificationsByUserId(userId);
        
        return success("삭제 완료", "사용자 인증 코드 전체 삭제 성공");
    }

    // 특정 인증 코드 삭제 (관리자용)
    @DeleteMapping("/{verificationId}/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "특정 인증 코드 삭제", description = "특정 인증 코드를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteVerification(@Parameter(description = "인증 ID") @PathVariable Long verificationId,
                                                                  @Parameter(description = "사용자 ID") @PathVariable Long userId) {

        emailVerificationService.deleteVerification(verificationId, userId);
        
        return success("삭제 완료", "특정 인증 코드 삭제 성공");
    }
}
