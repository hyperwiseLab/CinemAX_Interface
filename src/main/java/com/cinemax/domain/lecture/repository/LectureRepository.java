package com.cinemax.domain.lecture.repository;

import com.cinemax.domain.lecture.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // Task ID로 Lecture 조회
    @Query("SELECT A FROM Lecture A WHERE A.taskId = :taskId")
    List<Lecture> findByTaskId(@Param("taskId") Long taskId);

    // 제목으로 검색
    @Query("SELECT A FROM Lecture A WHERE A.title LIKE CONCAT('%', :keyword, '%')")
    List<Lecture> searchByTitle(@Param("keyword") String keyword);
}
