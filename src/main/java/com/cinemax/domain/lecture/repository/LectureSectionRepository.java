package com.cinemax.domain.lecture.repository;

import com.cinemax.domain.lecture.entity.LectureSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureSectionRepository extends JpaRepository<LectureSection, Long> {

    // Lecture ID로 Section 목록 조회
    @Query("SELECT A FROM LectureSection A WHERE A.lectureId = :lectureId ORDER BY A.lectureSectionId")
    List<LectureSection> findByLectureId(@Param("lectureId") Long lectureId);

    // Heading으로 검색
    @Query("SELECT A FROM LectureSection A WHERE A.heading LIKE CONCAT('%', :keyword, '%')")
    List<LectureSection> searchByHeading(@Param("keyword") String keyword);

    // Lecture ID와 Section ID로 조회
    @Query("SELECT A FROM LectureSection A WHERE A.lectureId = :lectureId AND A.lectureSectionId = :sectionId")
    Optional<LectureSection> findByLectureIdAndSectionId(@Param("lectureId") Long lectureId, @Param("sectionId") Long sectionId);

    // Lecture ID와 Section ID로 Section 존재 여부 확인
    @Query("SELECT CASE WHEN COUNT(A) > 0 THEN true ELSE false END FROM LectureSection A WHERE A.lectureId = :lectureId AND A.lectureSectionId = :sectionId")
    boolean existsByLectureIdAndSectionId(@Param("lectureId") Long lectureId, @Param("sectionId") Long sectionId);
}
