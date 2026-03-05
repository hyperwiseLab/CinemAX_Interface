package com.cinemax.domain.qna.repository;

import com.cinemax.domain.qna.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 답변 ID와 질문 ID로 조회
    Optional<Answer> findByAnswerIdAndQuestionId(Long answerId, Long questionId);

    // 질문별 답변 목록 조회
    @Query("SELECT A FROM Answer A WHERE A.questionId = :questionId ORDER BY A.createDt ASC")
    List<Answer> findByQuestionId(@Param("questionId") Long questionId);

    // 사용자별 답변 목록 조회
    @Query("SELECT A FROM Answer A WHERE A.user.userId = :userId ORDER BY A.createDt DESC")
    List<Answer> findByUserId(@Param("userId") Long userId);

    // 질문별 답변 개수
    @Query("SELECT COUNT(A) FROM Answer A WHERE A.questionId = :questionId")
    Long countByQuestionId(@Param("questionId") Long questionId);

    // 질문에 대한 최신 답변 조회
    @Query("SELECT A FROM Answer A WHERE A.questionId = :questionId ORDER BY A.createDt DESC")
    List<Answer> findLatestAnswerByQuestionId(@Param("questionId") Long questionId);
}
