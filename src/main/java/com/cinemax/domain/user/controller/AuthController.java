package com.cinemax.domain.user.controller;

import com.cinemax.domain.user.dto.*;
import com.cinemax.domain.user.service.UserService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController extends BaseController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    //회원가입
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일 인증이 완료된 사용자의 회원가입을 처리합니다.")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        
        UserResponse response = userService.signUp(request);
        
        return created(response, "회원가입 성공");
    }

    // 이메일 인증 코드 발송
    @PostMapping("/email-verification/send")
    @Operation(summary = "이메일 인증 코드 발송", description = "이메일 주소로 인증 코드를 발송합니다.")
    public ResponseEntity<ApiResponse<EmailVerificationResponse>> sendVerificationCode(@Valid @RequestBody SendEmailVerificationRequest request) {

        EmailVerificationResponse response = emailVerificationService.sendVerificationCode(request);

        return success(response, "이메일 인증 코드 발송 성공");
    }

    // 이메일 인증 코드 검증
    @PostMapping("/email-verification/verify")
    @Operation(summary = "이메일 인증 코드 검증", description = "발송된 인증 코드를 검증합니다.")
    public ResponseEntity<ApiResponse<EmailVerificationResponse>> verifyEmailCode(@Valid @RequestBody EmailVerificationRequest request) {

        EmailVerificationResponse response = emailVerificationService.verifyCode(request);

        return success(response, "이메일 인증 코드 검증 성공");
    }

    // 이메일 인증 코드 재발송
    @PostMapping("/email-verification/resend")
    @Operation(summary = "이메일 인증 코드 재발송", description = "인증 코드를 재발송합니다.")
    public ResponseEntity<ApiResponse<EmailVerificationResponse>> resendEmailCode(@Parameter(description = "이메일 주소") @RequestParam String email) {

        EmailVerificationResponse response = emailVerificationService.resendVerificationCode(email);

        return success(response, "이메일 인증 코드 재발송 성공");
    }

    // 이메일 인증 상태 조회
    @GetMapping("/email-verification/status/{userId}")
    @Operation(summary = "이메일 인증 상태 조회", description = "사용자의 이메일 인증 상태를 조회합니다.")
    public ResponseEntity<ApiResponse<EmailVerificationStatusResponse>> getVerificationStatus(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        EmailVerificationStatusResponse response = emailVerificationService.getVerificationStatus(userId);

        return success(response, "이메일 인증 상태 조회 성공");
    }

    // 이메일로 인증 상태 조회
    @GetMapping("/email-verification/status")
    @Operation(summary = "이메일로 인증 상태 조회", description = "이메일 주소로 인증 상태를 조회합니다.")
    public ResponseEntity<ApiResponse<EmailVerificationStatusResponse>> getVerificationStatusByEmail(@Parameter(description = "이메일 주소") @RequestParam String email) {

        EmailVerificationStatusResponse response = emailVerificationService.getVerificationStatusByEmail(email);

        return success(response, "이메일 인증 상태 조회 성공");
    }

    // 이메일 인증 완료 여부 확인
    @GetMapping("/email-verification/check/{userId}")
    @Operation(summary = "이메일 인증 완료 여부 확인", description = "사용자의 이메일 인증 완료 여부를 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> isEmailVerified(@Parameter(description = "사용자 ID") @PathVariable Long userId) {

        boolean isVerified = emailVerificationService.isEmailVerified(userId);

        return success(isVerified, "이메일 인증 완료 여부 확인 성공");
    }

    // 이메일로 인증 완료 여부 확인
    @GetMapping("/email-verification/check")
    @Operation(summary = "이메일로 인증 완료 여부 확인", description = "이메일 주소로 인증 완료 여부를 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> isEmailVerifiedByEmail(@Parameter(description = "이메일 주소") @RequestParam String email) {

        boolean isVerified = emailVerificationService.isEmailVerifiedByEmail(email);

        return success(isVerified, "이메일 인증 완료 여부 확인 성공");
    }

    // 회원가입 이메일 인증 코드 발송
    @PostMapping("/send-verification-code")
    @Operation(summary = "회원가입 이메일 인증 코드 발송", description = "회원가입을 위한 이메일 인증 코드를 발송합니다.")
    public ResponseEntity<ApiResponse<String>> sendSignUpVerificationCode(@Valid @RequestBody SendSignUpVerificationRequest request) {
        
        userService.sendSignUpVerificationCode(request.getEmail());
        
        return success("인증 코드가 발송되었습니다", "이메일 인증 코드 발송 성공");
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인 및 JWT 토큰을 발급합니다.")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {

        TokenResponse response = userService.login(request);
        
        return success(response, "로그인 성공");
    }

    // Access Token 재발급
    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 Access Token을 재발급합니다.")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        
        TokenResponse response = userService.refreshToken(request);
        
        return success(response, "토큰 재발급 성공");
    }

    // 로그아웃
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃 및 Refresh Token을 무효화합니다.")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        
        userService.logout(request.getRefreshToken());
        
        return success(null, "로그아웃 성공");
    }
}
