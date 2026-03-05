package com.cinemax.domain.qna.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.global.enums.QuestionStatus;
import com.cinemax.global.enums.QuestionUrgency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBL_QUESTION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_ID")
    private Long questionId;

    @Column(name = "CLASS_ID")
    private Long classId;

    @Column(name = "WEEKLY_SESSION_ID")
    private Long weeklySessionId;

    @Column(name = "WEEK_NO")
    private Integer weekNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "URGENCY", nullable = false)
    private QuestionUrgency urgency;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "CONTENT", nullable = false, length = 500)
    private String content;

    @Column(name = "TAGS", length = 500)
    private String tags;  // Comma-separated tags (e.g., "Java,Spring,JPA")

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private QuestionStatus status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASS_ID", insertable = false, updatable = false)
    private ClassEntity classEntity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WEEKLY_SESSION_ID", insertable = false, updatable = false)
    private WeeklySession weeklySession;

    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();

    // Question 생성 팩토리 메서드
    public static Question create(Long classId, Long weeklySessionId, Integer weekNo,
                                   User user, String title, String content, QuestionUrgency urgency, String tags) {
        return Question.builder()
                .classId(classId)
                .weeklySessionId(weeklySessionId)
                .weekNo(weekNo)
                .user(user)
                .title(title)
                .content(content)
                .tags(tags)
                .urgency(urgency != null ? urgency : QuestionUrgency.MEDIUM)
                .status(QuestionStatus.OPEN)
                .build();
    }

    // Question 정보 수정
    public void updateInfo(String title, String content, QuestionUrgency urgency, String tags) {
        this.title = title;
        this.content = content;
        this.urgency = urgency;
        this.tags = tags;
    }

    // Question 상태 변경
    public void updateStatus(QuestionStatus status) {
        this.status = status;
    }

    // Answer 추가
    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    // Question 닫기
    public void close() {
        this.status = QuestionStatus.CLOSED;
    }

    // Question 재오픈
    public void reopen() {
        this.status = QuestionStatus.OPEN;
    }

    // 답변 완료 처리
    public void markAsAnswered() {
        this.status = QuestionStatus.ANSWERED;
    }
}
