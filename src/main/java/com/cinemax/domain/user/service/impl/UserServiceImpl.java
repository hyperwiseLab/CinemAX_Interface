package com.cinemax.domain.user.service.impl;

import com.cinemax.domain.auth.dto.EmailVerificationRequest;
import com.cinemax.domain.auth.dto.SendEmailVerificationRequest;
import com.cinemax.domain.user.dto.*;
import com.cinemax.domain.user.entity.RefreshToken;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.RefreshTokenRepository;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.domain.user.service.UserService;
import com.cinemax.domain.auth.service.EmailVerificationService;
import com.cinemax.core.exception.BusinessException;
import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.core.error.enums.ErrorCode;
import com.cinemax.global.enums.RoleType;
import com.cinemax.global.jwt.JwtTokenProvider;
import com.cinemax.global.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * User 비즈니스 로직 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final EmailVerificationService emailVerificationService;

    // 회원가입
    @Override
    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 사용 중인 이메일입니다.");
        }

        // 학번 중복 체크
        if (userRepository.existsByStudentNum(request.getStudentNum())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "이미 사용 중인 학번입니다.");
        }

        // 비밀번호 형식 검증
        if (request.getPassword().length() < 8) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호는 8자 이상이어야 합니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 팩토리 메서드를 사용한 User 엔티티 생성
        User user;
        if (request.getRole() == null) {
            user = User.createStudent(request.getName(), request.getEmail(),
                                     null, null, null, // tel, selfIntroduction은 선택사항
                                     encodedPassword, request.getStudentNum(),
                                     request.getTermsAgreed(), request.getPrivacyAgreed(), request.getMarketingAgreed());
        } else {
            user = User.createWithRole(request.getName(), request.getEmail(),
                                      encodedPassword, request.getStudentNum(), request.getRole(),
                                      request.getTermsAgreed(), request.getPrivacyAgreed(), request.getMarketingAgreed());
        }

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    // 로그인 및 JWT 토큰 발급
    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {

        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        // 실제 검증 (사용자 비밀번호 체크)
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        // 사용자 정보 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // CustomUserDetailsService 생성 (userId 포함)
        com.cinemax.global.security.CustomUserDetailsService userDetails =
                new com.cinemax.global.security.CustomUserDetailsService(
                        user.getUserId(),
                        user.getEmail(),
                        user.getPasswordHash(),
                        user.getRole().name(),
                        user.isActive()
                );

        // CustomUserDetailsService로 새로운 Authentication 생성
        Authentication customAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // JWT 토큰 생성 (customAuthentication 사용)
        String accessToken = jwtTokenProvider.createAccessToken(customAuthentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(customAuthentication);

        // Refresh Token 만료 시간 계산
        Date refreshTokenExpiration = jwtTokenProvider.getExpirationFromToken(refreshToken);

        // Refresh Token 저장 (기존 토큰이 있으면 삭제 후 새로 저장)
        refreshTokenRepository.deleteByUserId(userDetails.getUserId());
        RefreshToken savedRefreshToken = RefreshToken.create(refreshToken, userDetails.getUserId(), refreshTokenExpiration);
        refreshTokenRepository.save(savedRefreshToken);

        // Access Token 만료 시간 계산
        Date expirationDate = jwtTokenProvider.getExpirationFromToken(accessToken);
        long accessTokenExpiresIn = expirationDate.getTime();

        return TokenResponse.builder()
                .grantType(jwtTokenProvider.getBearerType())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .build();
    }

    // Refresh Token을 이용한 Access Token 재발급
    @Override
    @Transactional(readOnly = true)
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 Refresh Token입니다.");
        }

        // 블랙리스트 체크 (무효화된 토큰인지 확인)
        RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN, "무효화된 Refresh Token입니다."));

        // Refresh Token에서 사용자 이메일 추출
        String email = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 사용자 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 사용자 활성 상태 확인
        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "비활성화된 사용자입니다.");
        }

        // Authentication 객체 생성 (Refresh Token에는 권한 정보가 없으므로 사용자 정보로부터 생성)
        CustomUserDetailsService userDetails = new CustomUserDetailsService(
                user.getUserId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().name(),
                user.isActive()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

        // Access Token 만료 시간 계산
        Date expirationDate = jwtTokenProvider.getExpirationFromToken(newAccessToken);
        long accessTokenExpiresIn = expirationDate.getTime();

        return TokenResponse.builder()
                .grantType(jwtTokenProvider.getBearerType())
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .build();
    }

    // 로그아웃 (Refresh Token 무효화)
    @Override
    @Transactional
    public void logout(String refreshToken) {

        // Refresh Token이 유효한지 확인
        if (jwtTokenProvider.validateToken(refreshToken)) {
            // 블랙리스트에 추가 (DB에서 삭제)
            refreshTokenRepository.deleteByToken(refreshToken);
            log.info("Refresh Token이 무효화되었습니다.");
        } else {
            log.warn("유효하지 않은 Refresh Token입니다. 무효화 작업을 건너뜁니다.");
        }
    }

    // 사용자 ID로 사용자 정보 조회
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserResponse.from(user);
    }

    // 이메일로 사용자 정보 조회
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserResponse.from(user);
    }

    // 현재 인증된 사용자 정보 조회
    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        return getUserByEmail(email);
    }

    // 사용자 비활성화
    @Override
    @Transactional
    public void deactivateUser(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.deactivate();
    }

    // 사용자 활성화
    @Override
    @Transactional
    public void activateUser(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.activate();
    }

    // 사용자 정보 수정 (본인)
    @Override
    @Transactional
    public UserResponse updateCurrentUser(Authentication authentication, UpdateUserRequest request) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateInfo(request.getName(), user.getEmail(), request.getStudentNum(), request.getDepartment(), request.getTel(), request.getSelfIntroduction());

        // 프로필 이미지 URL이 제공된 경우 업데이트
        if (request.getProfileImageUrl() != null) {
            user.updateProfileImage(request.getProfileImageUrl());
        }

        User saved = userRepository.save(user);

        return UserResponse.from(saved);
    }

    // 비밀번호 변경 (현재 비밀번호 확인)
    @Override
    @Transactional
    public void changePassword(Authentication authentication, ChangePasswordRequest request) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호가 현재 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 비밀번호 정책 검증은 @Valid 어노테이션으로 DTO에서 처리됨 -> 프론트에서 확인 필요 251112
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);

        userRepository.save(user);
    }

    // 회원가입 전 이메일 인증 코드 발송
    @Override
    @Transactional
    public void sendSignUpVerificationCode(String email) {

        // 이미 가입된 이메일인지 확인
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 가입된 이메일입니다.");
        }

        // 이메일 인증 코드 발송
        emailVerificationService.sendVerificationCode(
            SendEmailVerificationRequest.builder()
                .email(email)
                .purpose("회원가입")
                .build()
        );
    }

    // 이메일 인증 코드 검증
    @Override
    @Transactional(readOnly = true)
    public boolean verifySignUpEmailCode(String email, String code) {

        EmailVerificationRequest request = EmailVerificationRequest.builder()
                .email(email)
                .code(code)
                .build();

        emailVerificationService.verifyCode(request);

        return true;
    }

    // 비밀번호 확인 (현재 로그인한 사용자)
    @Override
    @Transactional(readOnly = true)
    public VerifyPasswordResponse verifyPassword(Authentication authentication, VerifyPasswordRequest request) {

        // 현재 로그인한 사용자 정보 조회
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 입력된 비밀번호와 저장된 비밀번호 해시 비교
        boolean isValid = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        return VerifyPasswordResponse.of(isValid);
    }

    // 비밀번호 만료 상태 확인
    @Override
    @Transactional(readOnly = true)
    public PasswordExpiryStatusResponse getPasswordExpiryStatus(Authentication authentication) {

        // 현재 로그인한 사용자 정보 조회
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        return PasswordExpiryStatusResponse.from(user);
    }

    // 관리자용 - 전체 사용자 목록 조회 (필터링 가능)
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(String role, Boolean isActive) {
        List<User> users;
        Byte statusByte = isActive != null ? (isActive ? (byte) 0 : (byte) 1) : null;

        if (role != null && statusByte != null) {
            // 역할과 활성화 상태 모두 필터링
            users = userRepository.findByRoleAndStatus(
                    com.cinemax.global.enums.RoleType.valueOf(role.toUpperCase()), statusByte);
        } else if (role != null) {
            // 역할만 필터링
            users = userRepository.findByRole(com.cinemax.global.enums.RoleType.valueOf(role.toUpperCase()));
        } else if (statusByte != null) {
            // 활성화 상태만 필터링
            users = userRepository.findByStatus(statusByte);
        } else {
            // 필터 없음 - 전체 조회
            users = userRepository.findAll();
        }

        return users.stream()
                .map(UserResponse::from)
                .toList();
    }

    // 관리자용 - 사용자 생성
    @Override
    @Transactional
    public UserResponse createUserByAdmin(CreateUserRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 사용 중인 이메일입니다.");
        }

        // 학번 중복 체크 (학번이 제공된 경우)
        if (request.getStudentId() != null && userRepository.existsByStudentNum(request.getStudentId())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "이미 사용 중인 학번입니다.");
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .name(request.getName())
                .studentNum(request.getStudentId())
                .role(request.getRole())
                .status(request.getIsActive() != null && request.getIsActive() ? (byte) 0 : (byte) 1)
                .termsAgreed(true)
                .privacyAgreed(true)
                .marketingAgreed(false)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    // 관리자용 - 사용자 정보 수정
    @Override
    @Transactional
    public UserResponse updateUserByAdmin(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        user.updateInfo(request.getName(), user.getEmail(), request.getStudentNum(), request.getDepartment(), request.getTel(), request.getSelfIntroduction());

        return UserResponse.from(user);
    }

    // 관리자용 - 사용자 삭제
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        // Refresh Token 삭제
        refreshTokenRepository.deleteByUserId(userId);

        // 사용자 삭제
        userRepository.delete(user);
    }

    // 관리자용 - 역할별 사용자 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(RoleType role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(UserResponse::from)
                .collect(java.util.stream.Collectors.toList());
    }

    // 관리자용 - 전체 사용자 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponse::from)
                .collect(java.util.stream.Collectors.toList());
    }
}
