package com.cinemax.domain.assign.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.task.entity.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_ASSIGN")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Assign extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ASSIGN_ID")
    private Long assignId;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "CYCLE_ID")
    private Long cycleId;

    @Column(name = "CHARACTER_IMG", nullable = false, length = 500)
    private String characterImg;

    @Column(name = "CHARACTER_PATH", nullable = false, length = 1000)
    private String characterPath;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Column(name = "SUB_TITLE", nullable = false, length = 255)
    private String subTitle;

    @Column(name = "ASSIGN_CONTENT", nullable = false, length = 1000)
    private String assignContent;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", insertable = false, updatable = false)
    private Task task;

    // Assign 생성
    public static Assign create(Long cycleId, Long taskId, String characterImg, String characterPath,
                               String title, String subTitle, String assignContent) {
        return Assign.builder()
                .cycleId(cycleId)
                .taskId(taskId)
                .characterImg(characterImg)
                .characterPath(characterPath)
                .title(title)
                .subTitle(subTitle)
                .assignContent(assignContent)
                .build();
    }

    // Assign 정보 수정
    public void updateInfo(String characterImg, String characterPath, String title, String subTitle, String assignContent) {
        this.characterImg = characterImg;
        this.characterPath = characterPath;
        this.title = title;
        this.subTitle = subTitle;
        this.assignContent = assignContent;
    }
}
