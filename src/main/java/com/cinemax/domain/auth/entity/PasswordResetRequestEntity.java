package com.cinemax.domain.auth.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 비밀번호 재설정 요청 엔티티
 */
@Entity
@Table(name = "TBL_PASSWORD_RESET_REQUEST")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PasswordResetRequestEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "EMAIL", nullable = false, length = 200)
    private String email;

    @Column(name = "VERIFICATION_CODE", nullable = false, length = 6)
    private String verificationCode;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "USED_AT")
    private LocalDateTime usedAt;

    @Column(name = "ATTEMPT_COUNT", nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    @Column(name = "RESET_METHOD", nullable = false, length = 20)
    private String resetMethod;

    @Column(name = "STATUS", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    // 인증 코드 검증
    public boolean verifyCode(String inputCode) {
        if (isExpired()) {
            return false;
        }

        if (attemptCount >= 5) { // 최대 5회 시도
            return false;
        }

        return verificationCode.equals(inputCode);
    }

    // 만료 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // 사용 완료 처리
    public void markAsUsed() {
        this.usedAt = LocalDateTime.now();
        this.status = "USED";
    }

    // 시도 횟수 증가
    public void incrementAttemptCount() {
        this.attemptCount++;
    }

    // 상태 업데이트
    public void updateStatus(String status) {
        this.status = status;
    }

    // 인증 코드 재발급
    public void regenerateCode(String newCode, LocalDateTime newExpiresAt) {
        this.verificationCode = newCode;
        this.expiresAt = newExpiresAt;
        this.attemptCount = 0;
        this.usedAt = null;
        this.status = "PENDING";
    }

    // 사용 완료 여부 확인
    public boolean isUsed() {
        return "USED".equals(status);
    }

    // 취소 여부 확인
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    // 차단 여부 확인
    public boolean isBlocked() {
        return "BLOCKED".equals(status);
    }

    // 대기 중 여부 확인
    public boolean isPending() {
        return "PENDING".equals(status);
    }
}
