package com.cinemax.domain.quiz.repository;

import com.cinemax.domain.quiz.entity.Quiz;
import com.cinemax.global.enums.QuizStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // 반의 전체 퀴즈 목록 (관리 화면). 문항+보기까지 함께 로딩.
    @EntityGraph(attributePaths = {"questions", "questions.options"})
    @Query("SELECT DISTINCT Q FROM Quiz Q WHERE Q.classId = :classId ORDER BY Q.weekNo ASC, Q.createDt DESC")
    List<Quiz> findByClassId(@Param("classId") Long classId);

    // 반의 특정 주차 퀴즈 목록
    @Query("SELECT Q FROM Quiz Q WHERE Q.classId = :classId AND Q.weekNo = :weekNo ORDER BY Q.createDt DESC")
    List<Quiz> findByClassIdAndWeekNo(@Param("classId") Long classId, @Param("weekNo") Integer weekNo);

    // 반의 특정 주차 + 상태 (학생 응시: PUBLISHED만). 문항+보기까지 함께 로딩.
    @EntityGraph(attributePaths = {"questions", "questions.options"})
    @Query("SELECT DISTINCT Q FROM Quiz Q WHERE Q.classId = :classId AND Q.weekNo = :weekNo AND Q.status = :status ORDER BY Q.createDt DESC")
    List<Quiz> findByClassIdAndWeekNoAndStatus(@Param("classId") Long classId,
                                               @Param("weekNo") Integer weekNo,
                                               @Param("status") QuizStatus status);

    // 문항+보기까지 함께 로딩 (상세 조회 시 N+1 방지)
    @EntityGraph(attributePaths = {"questions", "questions.options"})
    @Query("SELECT DISTINCT Q FROM Quiz Q WHERE Q.quizId = :quizId")
    Optional<Quiz> findByIdWithQuestions(@Param("quizId") Long quizId);
}
