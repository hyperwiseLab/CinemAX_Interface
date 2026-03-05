package com.cinemax.domain.user.dto;

import com.cinemax.domain.user.entity.User;
import com.cinemax.global.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String name;
    private String email;
    private String studentNum;
    private String department;
    private String tel;
    private String profileImageUrl;
    private RoleType role;
    private Byte status;
    private Boolean termsAgreed;
    private Boolean privacyAgreed;
    private Boolean marketingAgreed;
    private Boolean mustChangePassword;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    // Entity -> DTO 변환
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .studentNum(user.getStudentNum())
                .department(user.getDepartment())
                .tel(user.getTel())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .status(user.getStatus())
                .termsAgreed(user.getTermsAgreed())
                .privacyAgreed(user.getPrivacyAgreed())
                .marketingAgreed(user.getMarketingAgreed())
                .mustChangePassword(user.isMustChangePassword())
                .createDt(user.getCreateDt())
                .updateDt(user.getUpdateDt())
                .build();
    }
}
