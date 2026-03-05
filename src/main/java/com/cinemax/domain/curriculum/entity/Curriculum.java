package com.cinemax.domain.curriculum.entity;

import com.cinemax.core.entity.BaseTimeEntity;
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
@Table(name = "TBL_CURRICULUM")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Curriculum extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUR_ID")
    private Long curId;

    @Column(name = "LANG", nullable = false, length = 100)
    private String lang;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "DURATION_WEEKS", nullable = false)
    private Integer durationWeeks;

    @Column(name = "USE_YN", nullable = false)
    @Builder.Default
    private Boolean useYn = true;

    @JsonIgnore
    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CurriculumWeek> curriculumWeeks = new ArrayList<>();

    // 커리큘럼 정보 수정
    public void updateInfo(String lang, String name, String description, Integer durationWeeks, Boolean useYn) {
        this.lang = lang;
        this.name = name;
        this.description = description;
        this.durationWeeks = durationWeeks;
        this.useYn = useYn;
    }

    // 커리큘럼 비활성화
    public void deactivate() {
        this.useYn = false;
    }

    // 커리큘럼 활성화
    public void activate() {
        this.useYn = true;
    }
}
