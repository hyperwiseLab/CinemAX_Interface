package com.cinemax.domain.cycle.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.curriculum.entity.CurriculumWeek;
import com.cinemax.domain.syntax.entity.Syntax;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBL_CYCLE")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Cycle extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CYCLE_ID")
    private Long cycleId;

    @Column(name = "CUR_WEEK_ID")
    private Long curWeekId;

    @Column(name = "SYNTAX_DETAIL_ID")
    private Long syntaxDetailId;

    @Column(name = "SYNTAX_ID")
    private Long syntaxId;

    @Column(name = "CYCLE_TITLE", length = 500)
    private String cycleTitle;

    @Column(name = "FILE_NM", length = 200)
    private String fileNm;

    // 외래키 제약조건 추가
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUR_WEEK_ID", insertable = false, updatable = false)
    private CurriculumWeek curriculumWeek;

    // Cycle에 속한 Syntax 그룹들
    @JsonIgnore
    @OneToMany(mappedBy = "cycle", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("syntaxOrder ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Syntax> syntaxes = new ArrayList<>();

    // Cycle 생성 (기본)
    public static Cycle create(Long curWeekId, Long syntaxDetailId, Long syntaxId, String cycleTitle, String fileNm) {
        return Cycle.builder()
                .curWeekId(curWeekId)
                .syntaxDetailId(syntaxDetailId)
                .syntaxId(syntaxId)
                .cycleTitle(cycleTitle)
                .fileNm(fileNm)
                .syntaxes(new ArrayList<>())
                .build();
    }

    // Cycle 정보 수정
    public void updateInfo(Long syntaxDetailId, Long syntaxId, String cycleTitle, String fileNm) {
        this.syntaxDetailId = syntaxDetailId;
        this.syntaxId = syntaxId;
        this.cycleTitle = cycleTitle;
        this.fileNm = fileNm;
    }
}
