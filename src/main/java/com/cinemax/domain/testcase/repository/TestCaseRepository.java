package com.cinemax.domain.testcase.repository;

import com.cinemax.domain.testcase.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    /**
     * 특정 과제의 모든 테스트 케이스 조회
     */
    @Query("SELECT tc FROM TestCase tc WHERE tc.taskId = :taskId AND tc.cycleId = :cycleId ORDER BY tc.testCaseId")
    List<TestCase> findAllByTaskId(@Param("taskId") Long taskId,
                                   @Param("cycleId") Long cycleId);

    /**
     * 특정 과제의 테스트 케이스 개수 조회
     */
    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.taskId = :taskId " +
           "AND tc.cycleId = :cycleId")
    Long countByTaskId(
        @Param("taskId") Long taskId,
        @Param("cycleId") Long cycleId
    );

    /**
     * 특정 과제의 총 가중치 합계
     */
    @Query("SELECT SUM(tc.weight) FROM TestCase tc " +
           "WHERE tc.taskId = :taskId " +
           "AND tc.cycleId = :cycleId")
    Integer sumWeightByTaskId(
        @Param("taskId") Long taskId,
        @Param("cycleId") Long cycleId
    );
}
