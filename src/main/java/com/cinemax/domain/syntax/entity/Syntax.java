package com.cinemax.domain.syntax.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.cycle.entity.Cycle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Cycle과 SyntaxDetail의 중간 테이블
 * Cycle에 속한 문법 그룹을 관리
 */
@Entity
@Table(name = "TBL_SYNTAX")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Syntax extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SYNTAX_ID")
    private Long syntaxId;

    @Column(name = "CYCLE_ID", nullable = false)
    private Long cycleId;

    @Column(name = "SYNTAX_ORDER")
    private Integer syntaxOrder;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CYCLE_ID", insertable = false, updatable = false)
    private Cycle cycle;

    // Syntax에 속한 상세 정보들
    @JsonIgnore
    @OneToMany(mappedBy = "syntax", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("detailOrder ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<SyntaxDetail> syntaxDetails = new ArrayList<>();

    public static Syntax create(Long cycleId, Integer syntaxOrder) {
        return Syntax.builder()
                .cycleId(cycleId)
                .syntaxOrder(syntaxOrder)
                .syntaxDetails(new ArrayList<>())
                .build();
    }
}
