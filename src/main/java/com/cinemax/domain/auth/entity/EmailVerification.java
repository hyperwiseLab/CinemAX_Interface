package com.cinemax.domain.auth.entity;

import com.cinemax.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_EMAIL_VERIFICATION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailVerification {

    @Id
    @Column(name = "VARIFICATION_ID")
    private Long verificationId;

    @Column(name = "USER_ID", nullable = true)
    private Long userId;

    @Column(name = "EMAIL", nullable = false, length = 200)
    private String email;

    @Column(name = "CODE", nullable = false, length = 6)
    private String code;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "ATTEMPT_COUNT", nullable = false)
    private Byte attemptCount;

    @Column(name = "VERIFIED_AT")
    private LocalDateTime verifiedAt;

    @Column(name = "CREATE_DT", nullable = false)
    private LocalDateTime createDt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private User user;

    // 인증 코드 검증
    public boolean verifyCode(String inputCode) {
        if (isExpired()) {
            return false;
        }
        
        if (attemptCount >= 5) { // 최대 5회 시도
            return false;
        }
        
        return code.equals(inputCode);
    }

    // 인증 완료 처리
    public void markAsVerified() {
        this.verifiedAt = LocalDateTime.now();
    }

    // 만료 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // 인증 완료 여부 확인
    public boolean isVerified() {
        return verifiedAt != null;
    }

    // 시도 횟수 증가
    public void incrementAttemptCount() {
        this.attemptCount++;
    }

    // 인증 코드 재발급
    public void regenerateCode(String newCode, LocalDateTime newExpiresAt) {
        this.code = newCode;
        this.expiresAt = newExpiresAt;
        this.attemptCount = 0;
        this.verifiedAt = null;
    }
}
