package com.cinemax.domain.user.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Refresh Token 무효화 관리를 위한 엔티티
 */
@Entity
@Table(name = "TBL_REFRESH_TOKEN", indexes = {
    @Index(name = "idx_refresh_token_token", columnList = "TOKEN"),
    @Index(name = "idx_refresh_token_user_id", columnList = "USER_ID")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFRESH_TOKEN_ID")
    private Long refreshTokenId;

    @Column(name = "TOKEN", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "EXPIRES_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    @Builder
    private RefreshToken(String token, Long userId, Date expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    /**
     * 정적 팩토리 메서드 - Refresh Token 생성
     */
    public static RefreshToken create(String token, Long userId, Date expiresAt) {
        return RefreshToken.builder()
                .token(token)
                .userId(userId)
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * 토큰이 만료되었는지 확인
     */
    public boolean isExpired() {
        return expiresAt.before(new Date());
    }
}

