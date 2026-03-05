package com.cinemax.domain.feedback.repository;

import com.cinemax.domain.feedback.entity.Feedback;
import com.cinemax.global.enums.FeedbackType;
import com.cinemax.global.enums.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
 
    // Task ID로 피드백 목록 조회
    @Query("SELECT A FROM Feedback A WHERE A.taskId = :taskId ORDER BY A.createDt DESC")
    List<Feedback> findByTaskId(@Param("taskId") Long taskId);
 
    // Feedback Type으로 조회
    @Query("SELECT A FROM Feedback A WHERE A.feedbackType = :type ORDER BY A.createDt DESC")
    List<Feedback> findByFeedbackType(@Param("type") FeedbackType type);

 
    // Task ID와 Feedback Type으로 조회
    @Query("SELECT A FROM Feedback A WHERE A.taskId = :taskId AND A.feedbackType = :type ORDER BY A.createDt DESC")
    List<Feedback> findByTaskIdAndType(@Param("taskId") Long taskId, @Param("type") FeedbackType type);

    // 기간 기반 조회
    List<Feedback> findByTaskIdAndCreateDtBetween(Long taskId, LocalDateTime start, LocalDateTime end);
    List<Feedback> findByCreateDtBetween(LocalDateTime start, LocalDateTime end);

    // Task ID로 피드백 개수 조회
    @Query("SELECT COUNT(A) FROM Feedback A WHERE A.taskId = :taskId")
    Long countByTaskId(@Param("taskId") Long taskId);
}
