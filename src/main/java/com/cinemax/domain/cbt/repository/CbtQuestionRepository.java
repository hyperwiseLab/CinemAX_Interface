package com.cinemax.domain.cbt.repository;

import com.cinemax.domain.cbt.entity.CbtQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CbtQuestionRepository extends JpaRepository<CbtQuestion, Long> {

    // 관리자 목록 (검색/과목 필터, 보기 포함)
    @EntityGraph(attributePaths = "options")
    @Query("SELECT q FROM CbtQuestion q " +
            "WHERE q.classId = :classId " +
            "AND (:subjectId IS NULL OR q.subjectId = :subjectId) " +
            "AND (:keyword IS NULL OR q.content LIKE %:keyword%) " +
            "ORDER BY q.questionId DESC")
    Page<CbtQuestion> search(@Param("classId") Long classId,
                             @Param("subjectId") Long subjectId,
                             @Param("keyword") String keyword,
                             Pageable pageable);

    // 랜덤 출제 대상 (활성 문항, 보기 포함)
    @EntityGraph(attributePaths = "options")
    @Query("SELECT q FROM CbtQuestion q WHERE q.classId = :classId AND q.subjectId = :subjectId AND q.useYn = true")
    List<CbtQuestion> findPlayable(@Param("classId") Long classId, @Param("subjectId") Long subjectId);

    @EntityGraph(attributePaths = "options")
    @Query("SELECT q FROM CbtQuestion q WHERE q.questionId = :questionId")
    Optional<CbtQuestion> findByIdWithOptions(@Param("questionId") Long questionId);

    @EntityGraph(attributePaths = "options")
    @Query("SELECT q FROM CbtQuestion q WHERE q.questionId IN :ids")
    List<CbtQuestion> findAllWithOptionsByIds(@Param("ids") List<Long> ids);

    long countByClassIdAndSubjectIdAndUseYnTrue(Long classId, Long subjectId);

    long countBySubjectId(Long subjectId);

    List<CbtQuestion> findByClassId(Long classId);
}
