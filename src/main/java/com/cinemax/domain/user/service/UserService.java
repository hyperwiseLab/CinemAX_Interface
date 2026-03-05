package com.cinemax.domain.user.service;

import com.cinemax.domain.user.dto.*;
import org.springframework.security.core.Authentication;

/**
 * User 서비스 인터페이스
 */
public interface UserService {

    // 회원가입
    UserResponse signUp(SignUpRequest request);

    // 로그인 및 JWT 토큰 발급
    TokenResponse login(LoginRequest request);

    // Refresh Token을 이용한 Access Token 재발급
    TokenResponse refreshToken(RefreshTokenRequest request);

    // 로그아웃 (Refresh Token 무효화)
    void logout(String refreshToken);

    // 사용자 ID로 사용자 정보 조회
    UserResponse getUserById(Long userId);

    // 이메일로 사용자 정보 조회
    UserResponse getUserByEmail(String email);

    // 현재 인증된 사용자 정보 조회
    UserResponse getCurrentUser(Authentication authentication);

    // 사용자 비활성화
    void deactivateUser(Long userId);

    // 사용자 활성화
    void activateUser(Long userId);

    // 사용자 정보 수정 (이름, 학번)
    UserResponse updateCurrentUser(Authentication authentication, UpdateUserRequest request);

    // 비밀번호 변경 (현재 비밀번호 확인)
    void changePassword(Authentication authentication, ChangePasswordRequest request);

    // 회원가입 전 이메일 인증 코드 발송
    void sendSignUpVerificationCode(String email);

    // 이메일 인증 코드 검증
    boolean verifySignUpEmailCode(String email, String code);

    // 비밀번호 확인 (현재 로그인한 사용자)
    VerifyPasswordResponse verifyPassword(Authentication authentication, VerifyPasswordRequest request);

    // 비밀번호 만료 상태 확인
    PasswordExpiryStatusResponse getPasswordExpiryStatus(Authentication authentication);

    // 관리자용 - 전체 사용자 목록 조회 (필터링 가능)
    java.util.List<UserResponse> getAllUsers(String role, Boolean isActive);

    // 관리자용 - 사용자 생성
    UserResponse createUserByAdmin(CreateUserRequest request);

    // 관리자용 - 사용자 정보 수정
    UserResponse updateUserByAdmin(Long userId, UpdateUserRequest request);

    // 관리자용 - 사용자 삭제
    void deleteUser(Long userId);

    // 관리자용 - 역할별 사용자 목록 조회
    java.util.List<UserResponse> getUsersByRole(com.cinemax.global.enums.RoleType role);

    // 관리자용 - 전체 사용자 목록 조회
    java.util.List<UserResponse> getAllUsers();
}
