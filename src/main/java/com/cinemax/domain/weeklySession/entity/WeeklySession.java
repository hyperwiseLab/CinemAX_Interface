package com.cinemax.domain.weeklySession.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.classes.entity.ClassInvite;
import com.cinemax.domain.activity.entity.ActivityMonitor;
import com.cinemax.global.enums.WeeklySessionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBL_WEEKLY_SESSION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WeeklySession extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WEEKLY_SESSION_ID")
    private Long weeklySessionId;

    @Column(name = "INVITE_ID")
    private Long inviteId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVITE_ID", insertable = false, updatable = false)
    private ClassInvite classInvite;

    @Column(name = "WEEK_NO", nullable = false)
    private Integer weekNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    @Builder.Default
    private WeeklySessionStatus status = WeeklySessionStatus.PENDING;

    @Column(name = "START_DT")
    private LocalDateTime startDt;

    @Column(name = "AUTO_CLOSED", nullable = false)
    @Builder.Default
    private Boolean autoClosed = true;


    @JsonIgnore
    @OneToMany(mappedBy = "weeklySession", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ActivityMonitor> activityMonitors = new ArrayList<>();

    // 주차별 수업 시작
    public void startSession() {
        this.status = WeeklySessionStatus.IN_PROGRESS;
        this.startDt = LocalDateTime.now();
    }

    // 주차별 수업 종료
    public void endSession(boolean isAutoClosed) {
        this.status = WeeklySessionStatus.COMPLETED;
        this.autoClosed = isAutoClosed;
    }
}
