package com.cinemax.domain.hint.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.task.entity.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_HINT")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Hint extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HINT_ID")
    private Long hintId;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Column(name = "CONTENT", nullable = false, length = 500)
    private String content;

    @Column(name = "VIDEO_URL", length = 255)
    private String videoUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", insertable = false, updatable = false)
    private Task task;

    /**
     * Hint 생성
     */
    public static Hint create(Long taskId, String title, String content, String videoUrl) {
        return Hint.builder()
                .taskId(taskId)
                .title(title)
                .content(content)
                .videoUrl(videoUrl)
                .build();
    }

    /**
     * Hint 정보 수정
     */
    public void updateInfo(String title, String content, String videoUrl) {
        this.title = title;
        this.content = content;
        this.videoUrl = videoUrl;
    }
}
