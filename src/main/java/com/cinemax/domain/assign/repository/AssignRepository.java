package com.cinemax.domain.assign.repository;

import com.cinemax.domain.assign.entity.Assign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignRepository extends JpaRepository<Assign, Long> {

    // Task ID로 Assign 조회
    @Query("SELECT A FROM Assign A WHERE A.taskId = :taskId")
    List<Assign> findByTaskId(@Param("taskId") Long taskId);

    // 제목으로 검색
    @Query("SELECT A FROM Assign A WHERE A.title LIKE CONCAT('%', :keyword, '%')")
    List<Assign> searchByTitle(@Param("keyword") String keyword);

    // Task ID와 Assign ID로 조회
    @Query("SELECT A FROM Assign A WHERE A.taskId = :taskId AND A.assignId = :assignId")
    Optional<Assign> findByTaskIdAndAssignId(@Param("taskId") Long taskId, @Param("assignId") Long assignId);

    // Task ID와 Assign ID로 존재 여부 확인
    @Query("SELECT CASE WHEN COUNT(A) > 0 THEN true ELSE false END FROM Assign A WHERE A.taskId = :taskId AND A.assignId = :assignId")
    boolean existsByTaskIdAndAssignId(@Param("taskId") Long taskId, @Param("assignId") Long assignId);
}
