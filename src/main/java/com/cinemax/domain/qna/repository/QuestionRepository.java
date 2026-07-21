package com.cinemax.domain.qna.repository;

import com.cinemax.domain.qna.entity.Question;
import com.cinemax.global.enums.QuestionStatus;
import com.cinemax.global.enums.QuestionUrgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 클래스별 질문 목록 조회
    @Query("SELECT A FROM Question A WHERE A.classId = :classId ORDER BY A.createDt DESC")
    List<Question> findByClassId(@Param("classId") Long classId);

    // 주차별 세션의 질문 목록 조회
    @Query("SELECT A FROM Question A WHERE A.weeklySessionId = :weeklySessionId ORDER BY A.createDt DESC")
    List<Question> findByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);

    // 주차별 세션의 질문 목록 + 답변 fetch join (목록에서 답변 N+1 제거)
    @Query("SELECT DISTINCT A FROM Question A LEFT JOIN FETCH A.answers WHERE A.weeklySessionId = :weeklySessionId ORDER BY A.createDt DESC")
    List<Question> findByWeeklySessionWithAnswers(@Param("weeklySessionId") Long weeklySessionId);

    // 사용자별 질문 목록 + 답변 fetch join (학생 QnA 화면에서 교수 답변 표시용)
    @Query("SELECT DISTINCT A FROM Question A LEFT JOIN FETCH A.answers WHERE A.user.userId = :userId ORDER BY A.createDt DESC")
    List<Question> findByUserIdWithAnswers(@Param("userId") Long userId);

    // 사용자별 질문 목록 조회
    @Query("SELECT A FROM Question A WHERE A.user.userId = :userId ORDER BY A.createDt DESC")
    List<Question> findByUserId(@Param("userId") Long userId);

    // 상태별 질문 목록 조회
    @Query("SELECT A FROM Question A WHERE A.weeklySessionId = :weeklySessionId AND A.status = :status ORDER BY A.createDt DESC")
    List<Question> findByWeeklySessionAndStatus(@Param("weeklySessionId") Long weeklySessionId, @Param("status") QuestionStatus status);

    // 긴급도별 질문 목록 조회
    @Query("SELECT A FROM Question A WHERE A.weeklySessionId = :weeklySessionId AND A.urgency = :urgency ORDER BY A.createDt DESC")
    List<Question> findByWeeklySessionAndUrgency(@Param("weeklySessionId") Long weeklySessionId, @Param("urgency") QuestionUrgency urgency);

    // 답변되지 않은 질문 목록 조회 (OPEN 상태)
    @Query("SELECT A FROM Question A WHERE A.weeklySessionId = :weeklySessionId AND A.status = 'OPEN' ORDER BY A.urgency DESC, A.createDt ASC")
    List<Question> findUnansweredQuestions(@Param("weeklySessionId") Long weeklySessionId);

    // 높은 긴급도 질문 조회 (HIGH)
    @Query("SELECT A FROM Question A WHERE A.weeklySessionId = :weeklySessionId AND A.urgency = 'HIGH' AND A.status = 'OPEN' ORDER BY A.createDt ASC")
    List<Question> findHighUrgencyQuestions(@Param("weeklySessionId") Long weeklySessionId);

    // 제목으로 질문 검색
    @Query("SELECT A FROM Question A WHERE A.classId = :classId AND A.title LIKE CONCAT('%', :keyword, '%') ORDER BY A.createDt DESC")
    List<Question> searchByTitle(@Param("classId") Long classId, @Param("keyword") String keyword);

    // 내용으로 질문 검색
    @Query("SELECT A FROM Question A WHERE A.classId = :classId AND A.content LIKE CONCAT('%', :keyword, '%') ORDER BY A.createDt DESC")
    List<Question> searchByContent(@Param("classId") Long classId, @Param("keyword") String keyword);

    // 태그로 질문 검색
    @Query("SELECT A FROM Question A WHERE A.classId = :classId AND A.tags LIKE CONCAT('%', :tag, '%') ORDER BY A.createDt DESC")
    List<Question> searchByTag(@Param("classId") Long classId, @Param("tag") String tag);

    // 기간으로 질문 검색
    @Query("SELECT A FROM Question A WHERE A.classId = :classId AND A.createDt BETWEEN :startDate AND :endDate ORDER BY A.createDt DESC")
    List<Question> searchByDateRange(@Param("classId") Long classId,
                                     @Param("startDate") java.time.LocalDateTime startDate,
                                     @Param("endDate") java.time.LocalDateTime endDate);

    // 통합 검색 (키워드, 태그, 기간)
    @Query("SELECT DISTINCT A FROM Question A WHERE A.classId = :classId " +
           "AND (:keyword IS NULL OR A.title LIKE CONCAT('%', :keyword, '%') OR A.content LIKE CONCAT('%', :keyword, '%')) " +
           "AND (:tag IS NULL OR A.tags LIKE CONCAT('%', :tag, '%')) " +
           "AND (:startDate IS NULL OR A.createDt >= :startDate) " +
           "AND (:endDate IS NULL OR A.createDt <= :endDate) " +
           "ORDER BY A.createDt DESC")
    List<Question> searchQuestions(@Param("classId") Long classId,
                                   @Param("keyword") String keyword,
                                   @Param("tag") String tag,
                                   @Param("startDate") java.time.LocalDateTime startDate,
                                   @Param("endDate") java.time.LocalDateTime endDate);

    // 주차별 세션의 질문 개수
    @Query("SELECT COUNT(A) FROM Question A WHERE A.weeklySessionId = :weeklySessionId")
    Long countByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);

    // 답변되지 않은 질문 개수
    @Query("SELECT COUNT(A) FROM Question A WHERE A.weeklySessionId = :weeklySessionId AND A.status = 'OPEN'")
    Long countUnansweredQuestions(@Param("weeklySessionId") Long weeklySessionId);

    // 학생별 전체 질문 개수 조회
    @Query("SELECT COUNT(A) FROM Question A WHERE A.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    // 학생별 답변 받은 질문 개수 조회
    @Query("SELECT COUNT(A) FROM Question A WHERE A.user.userId = :userId AND A.status = 'ANSWERED'")
    Long countAnsweredByUserId(@Param("userId") Long userId);

    // 학생별 미답변 질문 개수 조회
    @Query("SELECT COUNT(A) FROM Question A WHERE A.user.userId = :userId AND A.status = 'OPEN'")
    Long countUnansweredByUserId(@Param("userId") Long userId);

    // 전체 미답변 질문 개수 조회 (관리자 대시보드용)
    @Query("SELECT COUNT(A) FROM Question A WHERE A.status = 'OPEN'")
    Long countUnansweredQuestions();
}
