package com.cinemax.domain.codesnapshot.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 코드 감시 제재 엔티티
 */
@Entity
@Table(name = "TBL_CODE_PEEK_PENALTY")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CodePeekPenalty extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PENALTY_ID")
    private Long penaltyId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "WEEKLY_SESSION_ID")
    private Long weeklySessionId;

    @Column(name = "INVITE_ID")
    private Long inviteId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WEEKLY_SESSION_ID", insertable = false, updatable = false)
    private WeeklySession weeklySession;

    @Column(name = "PEEK_COUNT", nullable = false)
    @Builder.Default
    private Integer peekCount = 0;

    @Column(name = "PENALTY_POINTS", nullable = false)
    @Builder.Default
    private Integer penaltyPoints = 0;

    @Column(name = "LAST_PEEK_DT")
    private LocalDateTime lastPeekDt;

    /**
     * 정적 팩토리 메서드 - CodePeekPenalty 생성
     */
    public static CodePeekPenalty create(Long userId, Long weeklySessionId, Long inviteId, User user, WeeklySession weeklySession) {
        return CodePeekPenalty.builder()
                .userId(userId)
                .weeklySessionId(weeklySessionId)
                .inviteId(inviteId)
                .user(user)
                .weeklySession(weeklySession)
                .peekCount(0)
                .penaltyPoints(0)
                .build();
    }

    /**
     * 엿보기 횟수 증가
     */
    public void incrementPeek() {
        this.peekCount++;
        this.lastPeekDt = LocalDateTime.now();
        calculatePenalty();
    }

    /**
     * 제재 점수 계산
     * - 1-2회: 0점
     * - 3-5회: 10점
     * - 6-10회: 20점
     * - 11회 이상: 30점
     */
    public void calculatePenalty() {
        if (this.peekCount <= 2) {
            this.penaltyPoints = 0;
        } else if (this.peekCount <= 5) {
            this.penaltyPoints = 10;
        } else if (this.peekCount <= 10) {
            this.penaltyPoints = 20;
        } else {
            this.penaltyPoints = 30;
        }
    }

    /**
     * 제재 점수 수동 설정
     */
    public void setPenaltyPoints(Integer points) {
        this.penaltyPoints = points;
    }

    /**
     * 엿보기 횟수 초기화
     */
    public void resetPeekCount() {
        this.peekCount = 0;
        this.penaltyPoints = 0;
        this.lastPeekDt = null;
    }
}
