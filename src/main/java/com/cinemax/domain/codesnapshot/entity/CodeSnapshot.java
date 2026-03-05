package com.cinemax.domain.codesnapshot.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.task.entity.Task;
import com.cinemax.domain.user.entity.User;
import com.cinemax.global.enums.LanguageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_CODE_SNAPSHOT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CodeSnapshot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODE_ID")
    private Long codeId;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "WEEKLY_SESSION_ID")
    private Long weeklySessionId;

    @Column(name = "CYCLE_ID")
    private Long cycleId;

    @Column(name = "USER_ID")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "LANG", nullable = false)
    private LanguageType lang;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "SAVE_AT", nullable = false)
    private LocalDateTime saveAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", insertable = false, updatable = false)
    private Task task;

    // WeeklySession has a composite key; avoid direct association here to prevent mismatched join errors.

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private User user;

    @Builder
    public CodeSnapshot(Long taskId, Long weeklySessionId, Long cycleId, Long userId, LanguageType lang, String content, LocalDateTime saveAt) {
        this.taskId = taskId;
        this.weeklySessionId = weeklySessionId;
        this.cycleId = cycleId;
        this.userId = userId;
        this.lang = lang;
        this.content = content;
        this.saveAt = saveAt;
    }

    // 코드 내용 업데이트
    public void updateContent(String content, LocalDateTime saveAt) {
        this.content = content;
        this.saveAt = saveAt;
    }

    // 정적 팩토리 메서드
    public static CodeSnapshot create(Long taskId, Long weeklySessionId, Long cycleId, Long userId, LanguageType lang, String content) {
        return CodeSnapshot.builder()
                .taskId(taskId)
                .weeklySessionId(weeklySessionId)
                .cycleId(cycleId)
                .userId(userId)
                .lang(lang)
                .content(content)
                .saveAt(LocalDateTime.now())
                .build();
    }
}
