package com.cinemax.domain.classes.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.curriculum.entity.Curriculum;
import com.cinemax.domain.user.entity.User;
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
@Table(name = "TBL_CLASS")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClassEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLASS_ID")
    private Long classId;

    @Column(name = "CLASS_NM", nullable = false, length = 200)
    private String classNm;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "YEAR", nullable = false)
    private Integer year;

    @Column(name = "TERM", nullable = false, length = 20)
    private String term;

    @Column(name = "CURRENT_INVITE_ID", length = 100)
    private String currentInviteId;

    @Column(name = "USE_YN", nullable = false)
    @Builder.Default
    private Boolean useYn = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUR_ID", nullable = false)
    private Curriculum curriculum;

    @JsonIgnore
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClassEnroll> enrollments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClassSubmit> submits = new ArrayList<>();

    // 수업 정보 수정
    public void updateInfo(String classNm, String description, Integer year, String term, Boolean useYn, Curriculum curriculum) {
        this.classNm = classNm;
        this.description = description;
        this.year = year;
        this.term = term;
        this.useYn = useYn;
        if (curriculum != null) {
            this.curriculum = curriculum;
        }
    }

    // 현재 초대 코드 업데이트
    public void updateCurrentInviteId(String inviteId) {
        this.currentInviteId = inviteId;
    }

    // 수업 비활성화
    public void deactivate() {
        this.useYn = false;
    }

    // 수업 활성화
    public void activate() {
        this.useYn = true;
    }
}
