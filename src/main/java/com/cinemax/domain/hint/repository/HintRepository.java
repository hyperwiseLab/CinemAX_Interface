package com.cinemax.domain.hint.repository;

import com.cinemax.domain.hint.entity.Hint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HintRepository extends JpaRepository<Hint, Long> {

    // Task ID로 Hint 조회
    @Query("SELECT A FROM Hint A WHERE A.taskId = :taskId")
    List<Hint> findByTaskId(@Param("taskId") Long taskId);

  
    // 제목으로 검색
    @Query("SELECT A FROM Hint A WHERE A.title LIKE CONCAT('%', :keyword, '%')")
    List<Hint> searchByTitle(@Param("keyword") String keyword);

  
    // Task ID와 Hint ID로 조회\
    @Query("SELECT A FROM Hint A WHERE A.taskId = :taskId AND A.hintId = :hintId")
    Optional<Hint> findByTaskIdAndHintId(@Param("taskId") Long taskId, @Param("hintId") Long hintId);

  
    // Task ID와 Hint ID로 존재 여부 확인
    @Query("SELECT CASE WHEN COUNT(A) > 0 THEN true ELSE false END FROM Hint A WHERE A.taskId = :taskId AND A.hintId = :hintId")
    boolean existsByTaskIdAndHintId(@Param("taskId") Long taskId, @Param("hintId") Long hintId);
}
