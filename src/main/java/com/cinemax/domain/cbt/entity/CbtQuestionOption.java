package com.cinemax.domain.cbt.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CBT 문항 보기. 정답은 문항당 정확히 1개(CORRECT_YN=true).
 */
@Entity
@Table(name = "TBL_CBT_QUESTION_OPTION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CbtQuestionOption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPTION_ID")
    private Long optionId;

    @Column(name = "ORDER_NO", nullable = false)
    private Integer orderNo;

    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    @Column(name = "CORRECT_YN", nullable = false)
    private Boolean correctYn;

    // FK 는 이 연관관계가 관리한다 (부모 저장 전 id null 문제 방지)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private CbtQuestion question;

    public static CbtQuestionOption create(Integer orderNo, String content, Boolean correctYn) {
        return CbtQuestionOption.builder()
                .orderNo(orderNo)
                .content(content)
                .correctYn(correctYn)
                .build();
    }

    public void assignQuestion(CbtQuestion question) {
        this.question = question;
    }
}
