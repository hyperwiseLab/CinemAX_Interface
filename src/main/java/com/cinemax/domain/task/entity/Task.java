package com.cinemax.domain.task.entity;

import com.cinemax.core.entity.BaseTimeEntity;
import com.cinemax.global.enums.TaskMode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_TASK")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Task extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "CYCLE_ID")
    private Long cycleId;

    @Column(name = "CUR_ID")
    private Long curId;

    @Column(name = "WEEK_NO")
    private Integer weekNo;

    @Column(name = "TASK_TITLE")
    private String taskTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "TASK_MODE")
    private TaskMode taskMode;

    // 실제 DB 컬럼은 mediumtext(문자열). 코드 원본을 그대로 저장한다.
    @Column(name = "START_CODE", columnDefinition = "mediumtext")
    private String startCode;

    @Column(name = "CONFIG_JSON", columnDefinition = "JSON")
    private String configJson;

    @Column(name = "ORDER_NO", nullable = false)
    private Integer orderNo;

    // Task 생성
    public static Task create(Long cycleId, Long curId, Integer weekNo, String taskTitle,
                              TaskMode taskMode, String startCode, String configJson, Integer orderNo) {
        return Task.builder()
                .cycleId(cycleId)
                .curId(curId)
                .weekNo(weekNo)
                .taskTitle(taskTitle)
                .taskMode(taskMode)
                .startCode(startCode)
                .configJson(configJson)
                .orderNo(orderNo)
                .build();
    }

    // Task 정보 수정
    public void updateInfo(String taskTitle, TaskMode taskMode, String startCode, String configJson, Integer orderNo) {
        this.taskTitle = taskTitle;
        this.taskMode = taskMode;
        this.startCode = startCode;
        this.configJson = configJson;
        this.orderNo = orderNo;
    }
}
