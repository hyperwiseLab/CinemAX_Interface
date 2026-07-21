package com.cinemax.domain.worklog.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 업무일지 엔티티
 */
@Entity
@Table(name = "TBL_WORK_LOG")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WorkLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORK_LOG_ID")
    private Long workLogId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "WEEKLY_SESSION_ID")
    private Long weeklySessionId;

    @Column(name = "LOG_DATE")
    private LocalDate logDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WEEKLY_SESSION_ID", insertable = false, updatable = false)
    private WeeklySession weeklySession;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "WORK_HOURS", precision = 5, scale = 2)
    private BigDecimal workHours;

    @Column(name = "ACHIEVEMENTS", columnDefinition = "TEXT")
    private String achievements;

    @Column(name = "DIFFICULTY_LEVEL")
    private Integer difficultyLevel;

    @Column(name = "PROFICIENCY_LEVEL")
    private Integer proficiencyLevel;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "MEANINGFUL_CONTENT", length = 1000)
    private String meaningfulContent;

    @Column(name = "DIFFICULT_CONTENT", length = 1000)
    private String difficultContent;

    @Column(name = "QUESTION_CONTENT", length = 1000)
    private String questionContent;

    // 교수 피드백
    @Column(name = "PROFESSOR_FEEDBACK", columnDefinition = "TEXT")
    private String professorFeedback;

    @Column(name = "FEEDBACK_DATE")
    private LocalDateTime feedbackDate;

    @Column(name = "FEEDBACK_SCORE")
    private Double feedbackScore; // 1-5점

    // 사이클별 평가 점수. 위 difficultyLevel/proficiencyLevel 은 이 값들의 평균이다.
    //
    // SUBSELECT 인 이유: 이 리포지토리는 JOIN FETCH 쿼리가 8개이고 그중
    // findLatestByUserIdAndWeeklySessionId 는 LIMIT 1 과 함께 쓰인다.
    // 컬렉션을 조인으로 가져오면 MultipleBagFetchException 이 나거나
    // LIMIT 이 조인 결과 행 기준으로 걸려 결과가 조용히 틀어진다.
    @JsonIgnore
    @OneToMany(mappedBy = "workLog", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNo ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<WorkLogCycleScore> cycleScores = new ArrayList<>();

    // ===== 비즈니스 메서드 =====

    // 업무일지 생성
    public static WorkLog create(Long userId, Long weeklySessionId, LocalDate logDate, User user, String content,
                                 BigDecimal workHours, String achievements) {
        return WorkLog.builder()
                .userId(userId)
                .weeklySessionId(weeklySessionId)
                .logDate(logDate)
                .user(user)
                .content(content)
                .workHours(workHours)
                .achievements(achievements)
                .difficultyLevel(3)
                .build();
    }

    // 업무일지 수정
    public void update(String content, BigDecimal workHours, String achievements,
                      Integer difficultyLevel, Integer proficiencyLevel, String notes,
                      String meaningfulContent, String difficultContent, String questionContent) {
        if (content != null) {
            this.content = content;
        }
        if (workHours != null) {
            this.workHours = workHours;
        }
        if (achievements != null) {
            this.achievements = achievements;
        }
        if (difficultyLevel != null) {
            this.difficultyLevel = difficultyLevel;
        }
        if (proficiencyLevel != null) {
            this.proficiencyLevel = proficiencyLevel;
        }
        if (notes != null) {
            this.notes = notes;
        }
        if (meaningfulContent != null) {
            this.meaningfulContent = meaningfulContent;
        }
        if (difficultContent != null) {
            this.difficultContent = difficultContent;
        }
        if (questionContent != null) {
            this.questionContent = questionContent;
        }
    }

    // 사이클 점수 추가 (양방향 연관 유지)
    public void addCycleScore(WorkLogCycleScore score) {
        this.cycleScores.add(score);
        score.assignWorkLog(this);
    }

    // 사이클 점수 전체 교체 (orphanRemoval 로 기존 점수 삭제됨)
    //
    // update() 의 null-skip 패치 방식과 달리 여기는 전량 교체다.
    // null-skip 으로 두면 빈 리스트가 "무시"로 해석돼 점수를 지울 수 없다.
    public void replaceCycleScores(List<WorkLogCycleScore> newScores) {
        this.cycleScores.clear();
        if (newScores != null) {
            for (WorkLogCycleScore s : newScores) {
                addCycleScore(s);
            }
        }
    }

    // 사이클 점수 평균으로 주차 단위 점수를 다시 계산한다.
    // 화면·통계가 아직 주차 스칼라를 읽으므로 원본과 어긋나지 않게 서버가 직접 파생시킨다.
    public void recalculateLevelsFromCycleScores() {
        if (cycleScores.isEmpty()) {
            return;
        }
        this.difficultyLevel = average(cycleScores.stream()
                .map(WorkLogCycleScore::getConceptScore));
        this.proficiencyLevel = average(cycleScores.stream()
                .map(WorkLogCycleScore::getApplicationScore));
    }

    private Integer average(java.util.stream.Stream<Integer> scores) {
        return (int) Math.round(scores.mapToInt(Integer::intValue).average().orElse(0));
    }

    // 업무 내용 수정
    public void updateContent(String content) {
        this.content = content;
    }

    // 작업 시간 수정
    public void updateWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    // 성과 수정
    public void updateAchievements(String achievements) {
        this.achievements = achievements;
    }

    // 난이도 수정 (개념 이해 점수)
    public void updateDifficultyLevel(Integer difficultyLevel) {
        if (difficultyLevel < 1 || difficultyLevel > 5) {
            throw new IllegalArgumentException("난이도는 1~5 사이의 값이어야 합니다.");
        }
        this.difficultyLevel = difficultyLevel;
    }

    // 코드 활용 점수 수정
    public void updateProficiencyLevel(Integer proficiencyLevel) {
        if (proficiencyLevel < 1 || proficiencyLevel > 5) {
            throw new IllegalArgumentException("코드 활용 점수는 1~5 사이의 값이어야 합니다.");
        }
        this.proficiencyLevel = proficiencyLevel;
    }

    // Week별 피드백 수정
    public void updateWeeklyFeedback(String meaningfulContent, String difficultContent, String questionContent) {
        if (meaningfulContent != null) {
            this.meaningfulContent = meaningfulContent;
        }
        if (difficultContent != null) {
            this.difficultContent = difficultContent;
        }
        if (questionContent != null) {
            this.questionContent = questionContent;
        }
    }

    // 메모 수정
    public void updateNotes(String notes) {
        this.notes = notes;
    }

    // 교수 피드백 추가
    public void addProfessorFeedback(String feedback, Double score) {
        this.professorFeedback = feedback;
        this.feedbackScore = score;
        this.feedbackDate = LocalDateTime.now();

        if (score != null && (score < 1 || score > 5)) {
            throw new IllegalArgumentException("피드백 점수는 1~5 사이의 값이어야 합니다.");
        }
    }

    // 교수 피드백 수정
    public void updateProfessorFeedback(String feedback, Double score) {
        if (feedback != null) {
            this.professorFeedback = feedback;
        }
        if (score != null) {
            if (score < 1 || score > 5) {
                throw new IllegalArgumentException("피드백 점수는 1~5 사이의 값이어야 합니다.");
            }
            this.feedbackScore = score;
        }
        this.feedbackDate = LocalDateTime.now();
    }
}
