package com.cinemax.domain.classes.dto;

import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 수업 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {

    private Long classId;
    private String classNm;
    private String description;
    private Integer year;
    private String term;
    private String lang;
    private Integer totalWeeks;
    private String currentInviteId;
    private Long inviteId;
    private Boolean useYn;
    private Long curId;
    private String curriculumName;
    private UserResponse professor;
    // 교수 정보 편의 필드
    private String professorDepartment;
    private String professorTel;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    /**
     * Entity -> DTO 변환
     */
    public static ClassResponse from(ClassEntity classEntity) {
        UserResponse professorResponse = UserResponse.from(classEntity.getUser());
        return ClassResponse.builder()
                .classId(classEntity.getClassId())
                .classNm(classEntity.getClassNm())
                .description(classEntity.getDescription())
                .year(classEntity.getYear())
                .term(classEntity.getTerm())
                .lang(classEntity.getCurriculum().getLang())
                .totalWeeks(classEntity.getCurriculum().getDurationWeeks())
                .currentInviteId(classEntity.getCurrentInviteId())
                .inviteId(null)
                .useYn(classEntity.getUseYn())
                .curId(classEntity.getCurriculum().getCurId())
                .curriculumName(classEntity.getCurriculum().getName())
                .professor(professorResponse)
                .professorDepartment(professorResponse.getDepartment())
                .professorTel(professorResponse.getTel())
                .createDt(classEntity.getCreateDt())
                .updateDt(classEntity.getUpdateDt())
                .build();
    }

    /**
     * Entity -> DTO 변환 (inviteId 포함)
     */
    public static ClassResponse from(ClassEntity classEntity, Long inviteId) {
        UserResponse professorResponse = UserResponse.from(classEntity.getUser());
        return ClassResponse.builder()
                .classId(classEntity.getClassId())
                .classNm(classEntity.getClassNm())
                .description(classEntity.getDescription())
                .year(classEntity.getYear())
                .term(classEntity.getTerm())
                .lang(classEntity.getCurriculum().getLang())
                .totalWeeks(classEntity.getCurriculum().getDurationWeeks())
                .currentInviteId(classEntity.getCurrentInviteId())
                .inviteId(inviteId)
                .useYn(classEntity.getUseYn())
                .curId(classEntity.getCurriculum().getCurId())
                .curriculumName(classEntity.getCurriculum().getName())
                .professor(professorResponse)
                .professorDepartment(professorResponse.getDepartment())
                .professorTel(professorResponse.getTel())
                .createDt(classEntity.getCreateDt())
                .updateDt(classEntity.getUpdateDt())
                .build();
    }
}
