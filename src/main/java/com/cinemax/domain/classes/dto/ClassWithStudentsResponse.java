package com.cinemax.domain.classes.dto;

import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 수업 + 학생 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassWithStudentsResponse {

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

    // 수강 학생 목록
    private List<ClassEnrollResponse> students;
    private Integer studentCount;

    /**
     * ClassResponse와 학생 목록으로 생성
     */
    public static ClassWithStudentsResponse of(ClassResponse classResponse, List<ClassEnrollResponse> students) {
        return ClassWithStudentsResponse.builder()
                .classId(classResponse.getClassId())
                .classNm(classResponse.getClassNm())
                .description(classResponse.getDescription())
                .year(classResponse.getYear())
                .term(classResponse.getTerm())
                .lang(classResponse.getLang())
                .totalWeeks(classResponse.getTotalWeeks())
                .currentInviteId(classResponse.getCurrentInviteId())
                .inviteId(classResponse.getInviteId())
                .useYn(classResponse.getUseYn())
                .curId(classResponse.getCurId())
                .curriculumName(classResponse.getCurriculumName())
                .professor(classResponse.getProfessor())
                .professorDepartment(classResponse.getProfessorDepartment())
                .professorTel(classResponse.getProfessorTel())
                .createDt(classResponse.getCreateDt())
                .updateDt(classResponse.getUpdateDt())
                .students(students)
                .studentCount(students != null ? students.size() : 0)
                .build();
    }

    /**
     * Entity와 학생 목록으로 생성
     */
    public static ClassWithStudentsResponse of(ClassEntity classEntity, List<ClassEnrollResponse> students) {
        ClassResponse classResponse = ClassResponse.from(classEntity);
        return of(classResponse, students);
    }
}
