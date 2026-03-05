package com.cinemax.domain.lecture.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.task.entity.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_LECTURE")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Lecture extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LECTURE_ID")
    private Long lectureId;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "CYCLE_ID")
    private Long cycleId;

    @Column(name = "CHARACTER_IMG", nullable = false, length = 500)
    private String characterImg;

    @Column(name = "CHARACTER_PATH", nullable = false, length = 500)
    private String characterPath;

    @Column(name = "TITLE", nullable = false, length = 100)
    private String title;

    @Column(name = "KEY_TAKEAWAY", nullable = false, length = 500)
    private String keyTakeaway;

    @Column(name = "SANDBOX_CODE", nullable = false, length = 500)
    private String sandboxCode;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", insertable = false, updatable = false)
    private Task task;

    // LectureSection과의 양방향 관계는 SQL 스키마 제약으로 매핑 불가
    // LectureSection은 LECTURE_ID만 가지고 있어 Lecture의 복합키 전체를 참조할 수 없음

    /**
     * Lecture 생성
     */
    public static Lecture create(Long cycleId, Long taskId, String characterImg, String characterPath,
                                 String title, String keyTakeaway, String sandboxCode) {
        return Lecture.builder()
                .cycleId(cycleId)
                .taskId(taskId)
                .characterImg(characterImg)
                .characterPath(characterPath)
                .title(title)
                .keyTakeaway(keyTakeaway)
                .sandboxCode(sandboxCode)
                .build();
    }

    /**
     * Lecture 정보 수정
     */
    public void updateInfo(String characterImg, String characterPath, String title,
                           String keyTakeaway, String sandboxCode) {
        this.characterImg = characterImg;
        this.characterPath = characterPath;
        this.title = title;
        this.keyTakeaway = keyTakeaway;
        this.sandboxCode = sandboxCode;
    }
}
