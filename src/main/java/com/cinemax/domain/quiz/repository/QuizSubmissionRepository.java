package com.cinemax.domain.quiz.repository;

import com.cinemax.domain.quiz.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    // 특정 퀴즈에 대한 본인 최초 제출 (최초 1회만 기록 정책)
    @Query("SELECT S FROM QuizSubmission S WHERE S.quizId = :quizId AND S.userId = :userId")
    Optional<QuizSubmission> findByQuizIdAndUserId(@Param("quizId") Long quizId, @Param("userId") Long userId);

    // 존재 여부 (재응시 판단)
    boolean existsByQuizIdAndUserId(Long quizId, Long userId);

    // 퀴즈 전체 제출 목록 (반 결과 집계)
    @Query("SELECT S FROM QuizSubmission S WHERE S.quizId = :quizId ORDER BY S.totalScore DESC, S.submittedAt ASC")
    List<QuizSubmission> findAllByQuizId(@Param("quizId") Long quizId);

    // 반 삭제 시 제출 기록 일괄 삭제
    void deleteByClassId(Long classId);
}
