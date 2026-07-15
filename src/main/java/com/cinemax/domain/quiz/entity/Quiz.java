package com.cinemax.domain.quiz.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.global.enums.QuizStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TBL_QUIZ")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Quiz extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUIZ_ID")
    private Long quizId;

    // 퀴즈는 반(Class)의 특정 주차에 소속된다.
    @Column(name = "CLASS_ID", nullable = false)
    private Long classId;

    @Column(name = "WEEK_NO", nullable = false)
    private Integer weekNo;

    // 응시/결과를 실제 주차 세션에 연결하기 위해 함께 저장 (ClassSubmit 패턴)
    @Column(name = "WEEKLY_SESSION_ID")
    private Long weeklySessionId;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private QuizStatus status;

    // 생성한 관리자(교수) userId
    @Column(name = "CREATED_BY")
    private Long createdBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASS_ID", insertable = false, updatable = false)
    private ClassEntity classEntity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WEEKLY_SESSION_ID", insertable = false, updatable = false)
    private WeeklySession weeklySession;

    @JsonIgnore
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuizQuestion> questions = new ArrayList<>();

    // Quiz 생성 팩토리 메서드
    public static Quiz create(Long classId, Integer weekNo, Long weeklySessionId,
                              String title, Long createdBy) {
        return Quiz.builder()
                .classId(classId)
                .weekNo(weekNo)
                .weeklySessionId(weeklySessionId)
                .title(title)
                .createdBy(createdBy)
                .status(QuizStatus.DRAFT)
                .build();
    }

    // 제목 수정
    public void updateTitle(String title) {
        this.title = title;
    }

    // 문항 추가 (양방향 연관 유지)
    public void addQuestion(QuizQuestion question) {
        this.questions.add(question);
        question.assignQuiz(this);
    }

    // 문항 전체 교체 (전체 수정 시)
    public void replaceQuestions(List<QuizQuestion> newQuestions) {
        this.questions.clear();
        if (newQuestions != null) {
            for (QuizQuestion q : newQuestions) {
                addQuestion(q);
            }
        }
    }

    // 검토중으로 전환
    public void markReview() {
        this.status = QuizStatus.REVIEW;
    }

    // 확정(공개)
    public void publish() {
        this.status = QuizStatus.PUBLISHED;
    }

    // 공개 여부
    public boolean isPublished() {
        return this.status == QuizStatus.PUBLISHED;
    }
}
