package com.cinemax.domain.classes.dto;

import com.cinemax.domain.classes.entity.ClassEnroll;
import com.cinemax.global.enums.EnrollStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 수강 신청 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassEnrollResponse {

    private Long enrollId;
    private EnrollStatus status;
    private String statusDescription;
    private LocalDateTime withdrawnDt;
    private Long classId;
    private String className;
    private Long userId;
    private String userName;
    private String userEmail;
    private String tel;
    private String department;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    // Entity -> DTO 변환
    public static ClassEnrollResponse from(ClassEnroll classEnroll) {
        return ClassEnrollResponse.builder()
                .enrollId(classEnroll.getEnrollId())
                .status(classEnroll.getStatus())
                .statusDescription(classEnroll.getStatus().name())
                .withdrawnDt(classEnroll.getWithdrawnDt())
                .classId(classEnroll.getClassEntity().getClassId())
                .className(classEnroll.getClassEntity().getClassNm())
                .userId(classEnroll.getUser().getUserId())
                .userName(classEnroll.getUser().getName())
                .userEmail(classEnroll.getUser().getEmail())
                .tel(classEnroll.getUser().getTel())
                .department(classEnroll.getUser().getDepartment())
                .createDt(classEnroll.getCreateDt())
                .updateDt(classEnroll.getUpdateDt())
                .build();
    }
}
