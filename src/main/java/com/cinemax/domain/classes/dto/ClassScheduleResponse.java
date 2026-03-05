package com.cinemax.domain.classes.dto;

import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.global.enums.WeeklySessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 수업 일정 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassScheduleResponse {

    private Long classId;
    private String className;
    private Long weeklySessionId;
    private Long inviteId;
    private Integer weekNo;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private WeeklySessionStatus status;

    /**
     * Entity -> DTO 변환 (WeeklySession)
     */
    public static ClassScheduleResponse from(ClassEntity classEntity, WeeklySession weeklySession) {
        return ClassScheduleResponse.builder()
                .classId(classEntity.getClassId())
                .className(classEntity.getClassNm())
                .weeklySessionId(weeklySession.getWeeklySessionId())
                .inviteId(weeklySession.getInviteId())
                .weekNo(weeklySession.getWeekNo())
                .startDt(weeklySession.getStartDt())
                .endDt(weeklySession.getCreateDt()) // endDt를 위한 필드가 없으므로 createDt 사용
                .status(weeklySession.getStatus())
                .build();
    }
}
