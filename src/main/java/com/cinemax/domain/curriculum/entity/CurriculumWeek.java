package com.cinemax.domain.curriculum.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_CURRICULUM_WEEK")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CurriculumWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUR_WEEK_ID")
    private Long curWeekId;

    @Column(name = "CUR_ID")
    private Long curId;

    @Column(name = "WEEK_NO")
    private Integer weekNo;

    @CreatedDate
    @Column(name = "CREATE_DT", updatable = false, nullable = false)
    private LocalDateTime createDt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUR_ID", insertable = false, updatable = false)
    private Curriculum curriculum;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "SUBTITLE", length = 200)
    private String subtitle;

    @Column(name = "CONTENT", length = 500)
    private String content;

    @Column(name = "CUR_LEV", nullable = false)
    private Integer curLev;

    @Column(name = "CHARACTER_NM", length = 50)
    private String characterNm;
}
