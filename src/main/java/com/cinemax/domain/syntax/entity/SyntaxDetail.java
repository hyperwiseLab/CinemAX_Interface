package com.cinemax.domain.syntax.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Syntax의 상세 정보 엔티티
 * JSON의 syntax_detail 배열 항목에 해당
 */
@Entity
@Table(name = "TBL_SYNTAX_DETAIL")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SyntaxDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SYNTAX_DETAIL_ID")
    private Long syntaxDetailId;

    @Column(name = "SYNTAX_ID", nullable = false)
    private Long syntaxId;

    @Column(name = "SYNTAX_TITLE", nullable = false, length = 200)
    private String syntaxTitle;

    @Column(name = "SYNTAX_COMMENT", length = 500)
    private String syntaxComment;

    @Column(name = "SYNTAX_CODE", columnDefinition = "TEXT")
    private String syntaxCode;

    @Column(name = "DETAIL_ORDER")
    private Integer detailOrder;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SYNTAX_ID", insertable = false, updatable = false)
    private Syntax syntax;

    public static SyntaxDetail create(Long syntaxId, String syntaxTitle,
                                      String syntaxComment, String syntaxCode,
                                      Integer detailOrder) {
        return SyntaxDetail.builder()
                .syntaxId(syntaxId)
                .syntaxTitle(syntaxTitle)
                .syntaxComment(syntaxComment)
                .syntaxCode(syntaxCode)
                .detailOrder(detailOrder)
                .build();
    }

    public void updateInfo(String syntaxTitle, String syntaxComment, String syntaxCode) {
        this.syntaxTitle = syntaxTitle;
        this.syntaxComment = syntaxComment;
        this.syntaxCode = syntaxCode;
    }

    public void updateOrder(Integer detailOrder) {
        this.detailOrder = detailOrder;
    }
}
