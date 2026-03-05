package com.cinemax.domain.notice.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * 공지사항 엔티티
 */
@Entity
@Table(name = "TBL_NOTICE", indexes = {
    @Index(name = "idx_notice_author", columnList = "AUTHOR_ID"),
    @Index(name = "idx_notice_class", columnList = "CLASS_ID"),
    @Index(name = "idx_notice_important", columnList = "IS_IMPORTANT")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_ID")
    private Long noticeId;

    @Column(name = "AUTHOR_ID", nullable = false)
    private Long authorId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHOR_ID", insertable = false, updatable = false)
    private User author;

    @Column(name = "CLASS_ID")
    private Long classId;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "IS_IMPORTANT", nullable = false)
    @Builder.Default
    private Boolean isImportant = false;

    @Column(name = "VIEW_COUNT", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "USE_YN", nullable = false)
    @Builder.Default
    private Boolean useYn = true;

    /**
     * Notice 생성
     */
    public static Notice create(Long authorId, Long classId, String title, String content, Boolean isImportant) {
        return Notice.builder()
                .authorId(authorId)
                .classId(classId)
                .title(title)
                .content(content)
                .isImportant(isImportant != null ? isImportant : false)
                .build();
    }

    /**
     * Notice 수정
     */
    public void updateInfo(String title, String content, Boolean isImportant) {
        this.title = title;
        this.content = content;
        this.isImportant = isImportant;
    }

    /**
     * 조회수 증가
     */
    public void increaseViewCount() {
        this.viewCount++;
    }

    /**
     * 공지사항 삭제 (soft delete)
     */
    public void delete() {
        this.useYn = false;
    }
}
