package com.cinemax.domain.lecture.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_LECTURE_SECTION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LectureSection {

    @CreatedDate
    @Column(name = "CREATE_DT", updatable = false, nullable = false)
    private LocalDateTime createDt;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LECTURE_SECTION_ID")
    private Long lectureSectionId;

    @Column(name = "LECTURE_ID")
    private Long lectureId;

    @Column(name = "HEADING", nullable = false, length = 200)
    private String heading;

    @Column(name = "LECTURE_SECTION_TXT", nullable = false, length = 500)
    private String lectureSectionTxt;

    @Column(name = "LECTURE_SECTION_CODE", columnDefinition = "MEDIUMTEXT")
    private String lectureSectionCode;

    // Lecture는 복합키(LECTURE_ID, TASK_ID)를 가지지만
    // LectureSection은 LECTURE_ID만 가지고 있어 객체 매핑 불가
    // ID를 통한 조회만 가능

    /**
     * LectureSection 생성
     */
    public static LectureSection create(Long lectureSectionId, Long lectureId,
                                       String heading, String lectureSectionTxt, String lectureSectionCode) {
        return LectureSection.builder()
                .lectureSectionId(lectureSectionId)
                .lectureId(lectureId)
                .heading(heading)
                .lectureSectionTxt(lectureSectionTxt)
                .lectureSectionCode(lectureSectionCode)
                .build();
    }

    /**
     * LectureSection 정보 수정
     */
    public void updateInfo(String heading, String lectureSectionTxt, String lectureSectionCode) {
        this.heading = heading;
        this.lectureSectionTxt = lectureSectionTxt;
        this.lectureSectionCode = lectureSectionCode;
    }
}
