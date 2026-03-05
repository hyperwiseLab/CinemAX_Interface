package com.cinemax.domain.mentor.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.domain.task.entity.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TBL_MENTOR_DIALOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorDialog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DIALOG_ID")
    private Long dialogId;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "MENTOR_TYPE", nullable = false)
    private Integer mentorType;

    @Column(name = "DIALOG_SORT", nullable = false)
    private Integer dialogSort;

    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    @Column(name = "DIALOG_TYPE", nullable = false)
    private Integer dialogType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID", insertable = false, updatable = false)
    private Task task;
}
