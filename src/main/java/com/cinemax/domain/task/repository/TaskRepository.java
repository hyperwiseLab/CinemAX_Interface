package com.cinemax.domain.task.repository;

import com.cinemax.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  TaskRepository extends JpaRepository<Task, Long> {

    // Cycle ID로 Task 조회
    @Query("SELECT A FROM Task A WHERE  A.cycleId = :cycleId ORDER BY  A.orderNo")
    List<Task> findByCycleId(@Param("cycleId") Long cycleId);

    // Curriculum ID로 Task 조회
    @Query("SELECT A FROM Task A WHERE  A.curId = :curId ORDER BY  A.orderNo")
    List<Task> findByCurId(@Param("curId") Long curId);

    // 주차별 Task 조회
    @Query("SELECT A FROM Task A WHERE  A.curId = :curId AND  A.weekNo = :weekNo ORDER BY  A.orderNo")
    List<Task> findByWeek(@Param("curId") Long curId, @Param("weekNo") Integer weekNo);

    // Curriculum과 Cycle로 Task 조회
    @Query("SELECT A FROM Task A WHERE  A.curId = :curId AND  A.cycleId = :cycleId ORDER BY  A.orderNo")
    List<Task> findByCurIdAndCycleId(@Param("curId") Long curId, @Param("cycleId") Long cycleId);

    // Task Mode로 조회
    @Query("SELECT A FROM Task A WHERE  A.taskMode = :taskMode ORDER BY  A.orderNo")
    List<Task> findByTaskMode(@Param("taskMode") com.cinemax.global.enums.TaskMode taskMode);

    // 주차별 세션의 전체 Task 수 조회
    @Query("SELECT COUNT(t) FROM Task t, WeeklySession ws " +
           "JOIN ws.classInvite ci JOIN ci.classEntity ce " +
           "WHERE t.curId = ce.curriculum.curId AND t.weekNo = ws.weekNo AND ws.weeklySessionId = :weeklySessionId")
    Long countByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);
}
