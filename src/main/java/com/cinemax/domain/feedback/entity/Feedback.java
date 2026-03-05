package com.cinemax.domain.feedback.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.task.entity.Task;
import com.cinemax.global.enums.FeedbackType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TBL_FEEDBACK")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FEEDBACK_ID")
    private Long feedbackId;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "CYCLE_ID")
    private Long cycleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "FEEDBACK_TYPE", nullable = false)
    private FeedbackType feedbackType;

    @Column(name = "CHARACTER_IMG", nullable = false, length = 500)
    private String characterImg;

    @Column(name = "CHARACTER_PATH", nullable = false, length = 1000)
    private String characterPath;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Column(name = "SUB_TITLE", nullable = false, length = 255)
    private String subTitle;

    @Column(name = "FEEDBACK_CONTENT", nullable = false, length = 1000)
    private String feedbackContent;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", insertable = false, updatable = false)
    private Task task;

    // Feedback 생성 팩토리 메서드
    @Builder
    public static Feedback create(Long cycleId, Long taskId, FeedbackType feedbackType, String characterImg, String characterPath,
                                  String title, String subTitle, String feedbackContent) {
        Feedback feedback = new Feedback();
        feedback.cycleId = cycleId;
        feedback.taskId = taskId;
        feedback.feedbackType = feedbackType;
        feedback.characterImg = characterImg;
        feedback.characterPath = characterPath;
        feedback.title = title;
        feedback.subTitle = subTitle;
        feedback.feedbackContent = feedbackContent;
        return feedback;
    }

    // Feedback 내용 수정
    public void update(FeedbackType feedbackType, String characterImg, String characterPath,
                      String title, String subTitle, String feedbackContent) {
        if (feedbackType != null) {
            this.feedbackType = feedbackType;
        }
        if (characterImg != null) {
            this.characterImg = characterImg;
        }
        if (characterPath != null) {
            this.characterPath = characterPath;
        }
        if (title != null) {
            this.title = title;
        }
        if (subTitle != null) {
            this.subTitle = subTitle;
        }
        if (feedbackContent != null) {
            this.feedbackContent = feedbackContent;
        }
    }
}
