package com.cinemax.domain.classes.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBL_CLASS_INVITE")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClassInvite extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVITE_ID")
    private Long inviteId;

    @Column(name = "INVITE_CD", nullable = false, length = 20)
    private String inviteCd;

    @Column(name = "QR_CD", length = 500)
    private String qrCd;

    @Column(name = "ACTIVE_YN", nullable = false)
    @Builder.Default
    private Boolean activeYn = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASS_ID", nullable = false)
    private ClassEntity classEntity;

    @JsonIgnore
    @OneToMany(mappedBy = "classInvite", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WeeklySession> weeklySessions = new ArrayList<>();
}
