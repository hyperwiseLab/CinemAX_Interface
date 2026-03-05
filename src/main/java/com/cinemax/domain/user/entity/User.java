package com.cinemax.domain.user.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.classes.entity.ClassEnroll;
import com.cinemax.domain.worklog.entity.WorkLog;
import com.cinemax.domain.auth.entity.EmailVerification;
import com.cinemax.domain.auth.entity.AuthLog;
import com.cinemax.domain.notification.entity.Notification;
import com.cinemax.global.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBL_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private RoleType role = RoleType.STUDENT;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "EMAIL", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "TEL", nullable = false, length = 100, unique = true)
    private String tel;

    @Column(name = "DEPARTMENT", nullable = false, length = 100)
    private String department;

    @Column(name = "SELF_INTRODUCTION", length =  1000)
    private String selfIntroduction;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 150)
    private String passwordHash;

    @Column(name = "STUDENT_NUM", nullable = false)
    private String studentNum;

    @Column(name = "PROFILE_IMAGE_URL", length = 500)
    private String profileImageUrl;

    @Column(name = "STATUS")
    private Byte status;

    @Column(name = "TERMS_AGREED")
    private Boolean termsAgreed;

    @Column(name = "PRIVACY_AGREED")
    private Boolean privacyAgreed;

    @Column(name = "MARKETING_AGREED")
    private Boolean marketingAgreed;

    @Column(name = "MUST_CHANGE_PASSWORD")
    private Boolean mustChangePassword;

    @Column(name = "LAST_PASSWORD_CHANGED_AT")
    private java.time.LocalDateTime lastPasswordChangedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassEnroll> enrollments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkLog> workLogs = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailVerification> emailVerifications = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthLog> authLogs = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    private User(String name, String email, String tel, String department, String selfIntroduction, String passwordHash, String studentNum, RoleType role, Byte status,
                Boolean termsAgreed, Boolean privacyAgreed, Boolean marketingAgreed) {
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.department = department;
        this.selfIntroduction = selfIntroduction;
        this.passwordHash = passwordHash;
        this.studentNum = studentNum;
        this.role = role != null ? role : RoleType.STUDENT;
        this.status = status;
        this.termsAgreed = termsAgreed;
        this.privacyAgreed = privacyAgreed;
        this.marketingAgreed = marketingAgreed;
        this.mustChangePassword = Boolean.FALSE;
        this.lastPasswordChangedAt = java.time.LocalDateTime.now();
    }

    /**
     * 정적 팩토리 메서드 - 학생 회원가입
     */
    public static User createStudent(String name, String email, String tel, String department, String selfIntroduction, String encodedPassword, String studentNum,
                                   Boolean termsAgreed, Boolean privacyAgreed, Boolean marketingAgreed) {
        return User.builder()
                .name(name)
                .email(email)
                .tel(tel)
                .department(department)
                .selfIntroduction(selfIntroduction)
                .passwordHash(encodedPassword)
                .studentNum(studentNum)
                .role(RoleType.STUDENT)
                .status((byte) 0) // 0: 활성
                .termsAgreed(termsAgreed)
                .privacyAgreed(privacyAgreed)
                .marketingAgreed(marketingAgreed)
                .build();
    }

    /**
     * 정적 팩토리 메서드 - 교수 회원가입
     */
    public static User createProfessor(String name, String email, String tel, String selfIntroduction, String encodedPassword, String studentNum,
                                     Boolean termsAgreed, Boolean privacyAgreed, Boolean marketingAgreed) {
        return User.builder()
                .name(name)
                .email(email)
                .tel(tel)
                .department(selfIntroduction)
                .selfIntroduction(selfIntroduction)
                .passwordHash(encodedPassword)
                .studentNum(studentNum)
                .role(RoleType.PROFESSOR)
                .status((byte) 0) // 0: 활성
                .termsAgreed(termsAgreed)
                .privacyAgreed(privacyAgreed)
                .marketingAgreed(marketingAgreed)
                .build();
    }

    /**
     * 정적 팩토리 메서드 - 관리자 생성
     */
    public static User createAdmin(String name, String email, String tel, String selfIntroduction, String encodedPassword, String studentNum,
                                 Boolean termsAgreed, Boolean privacyAgreed, Boolean marketingAgreed) {
        return User.builder()
                .name(name)
                .email(email)
                .tel(tel)
                .department(selfIntroduction)
                .selfIntroduction(selfIntroduction)
                .passwordHash(encodedPassword)
                .studentNum(studentNum)
                .role(RoleType.ADMIN)
                .status((byte) 0) // 0: 활성
                .termsAgreed(termsAgreed)
                .privacyAgreed(privacyAgreed)
                .marketingAgreed(marketingAgreed)
                .build();
    }

    /**
     * 정적 팩토리 메서드 - 역할 지정 회원가입
     */
    public static User createWithRole(String name, String email, String encodedPassword,
                                      String studentNum, RoleType role,
                                      Boolean termsAgreed, Boolean privacyAgreed, Boolean marketingAgreed) {
        return User.builder()
                .name(name)
                .email(email)
                .passwordHash(encodedPassword)
                .studentNum(studentNum)
                .role(role)
                .status((byte) 0) // 0: 활성
                .termsAgreed(termsAgreed)
                .privacyAgreed(privacyAgreed)
                .marketingAgreed(marketingAgreed)
                .build();
    }

    // 비즈니스 메서드 - 사용자 비활성화
    public void deactivate() {
        this.status = (byte) 1;
    }

    /**
     * 비즈니스 메서드 - 사용자 활성화
     */
    public void activate() {
        this.status = (byte) 0;
    }

    /**
     * 비즈니스 메서드 - 사용자 활성 상태 확인
     */
    public boolean isActive() {
        return this.status == null || this.status == 0;
    }

    /**
     * 비즈니스 메서드 - 비밀번호 업데이트
     */
    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.lastPasswordChangedAt = java.time.LocalDateTime.now();
    }

    /**
     * 비즈니스 메서드 - 임시 비밀번호 사용 강제 여부 설정
     */
    public void setMustChangePassword(boolean mustChange) {
        this.mustChangePassword = mustChange;
    }

    /**
     * 비즈니스 메서드 - 임시 비밀번호 사용 강제 여부 확인
     */
    public boolean isMustChangePassword() {
        return Boolean.TRUE.equals(this.mustChangePassword);
    }

    /**
     * 비즈니스 메서드 - 사용자 정보 업데이트
     */
    public void updateInfo(String name, String email, String studentNum, String department, String tel, String selfIntroduction) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.tel = tel;
        this.studentNum = studentNum;
        this.selfIntroduction = selfIntroduction;
    }

    // 비즈니스 메서드 - 프로필 이미지 업데이트
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // 비즈니스 메서드 - 약관 동의 정보 업데이트
    public void updateAgreementInfo(Boolean termsAgreed, Boolean privacyAgreed, Boolean marketingAgreed) {
        this.termsAgreed = termsAgreed;
        this.privacyAgreed = privacyAgreed;
        this.marketingAgreed = marketingAgreed;
    }

    // 비즈니스 메서드 - 필수 약관 동의 확인
    public boolean hasRequiredAgreements() {
        return Boolean.TRUE.equals(termsAgreed) && Boolean.TRUE.equals(privacyAgreed);
    }

    // 비즈니스 메서드 - 마케팅 동의 확인
    public boolean hasMarketingAgreement() {
        return Boolean.TRUE.equals(marketingAgreed);
    }

    /**
     * 비즈니스 메서드 - 비밀번호 변경 필요 여부 확인 (6개월 경과)
     * @return 비밀번호 변경이 필요하면 true
     */
    public boolean isPasswordExpired() {
        if (this.lastPasswordChangedAt == null) {
            return true; // 비밀번호 변경 이력이 없으면 변경 필요
        }
        java.time.LocalDateTime sixMonthsAgo = java.time.LocalDateTime.now().minusMonths(6);
        return this.lastPasswordChangedAt.isBefore(sixMonthsAgo);
    }

    /**
     * 비즈니스 메서드 - 비밀번호 만료까지 남은 일수
     * @return 남은 일수 (음수면 이미 만료됨)
     */
    public long getDaysUntilPasswordExpiry() {
        if (this.lastPasswordChangedAt == null) {
            return -1; // 비밀번호 변경 이력이 없음
        }
        java.time.LocalDateTime expiryDate = this.lastPasswordChangedAt.plusMonths(6);
        return java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDateTime.now(), expiryDate);
    }
}
