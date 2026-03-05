package com.cinemax.domain.classes.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.user.entity.User;
import com.cinemax.global.enums.EnrollStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_CLASS_ENROLL")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClassEnroll extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ENROLL_ID")
    private Long enrollId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    @Builder.Default
    private EnrollStatus status = EnrollStatus.ACTIVE;

    @Column(name = "WITHDRAWN_DT")
    private LocalDateTime withdrawnDt;

    @Column(name = "WITHDRAW_REASON", length = 500)
    private String withdrawReason;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASS_ID", nullable = false)
    private ClassEntity classEntity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    // 수강 신청 취소
    public void withdraw() {
        this.status = EnrollStatus.WITHDRAWN;
        this.withdrawnDt = LocalDateTime.now();
    }

    // 수강 신청 취소 (사유 포함)
    public void withdraw(String reason) {
        this.status = EnrollStatus.WITHDRAWN;
        this.withdrawnDt = LocalDateTime.now();
        this.withdrawReason = reason;
    }

    // 수강 재참여(복원)
    public void reactivate() {
        this.status = EnrollStatus.ACTIVE;
        this.withdrawnDt = null;
        this.withdrawReason = null;
    }
}
