package com.cinemax.domain.codesnapshot.repository;

import com.cinemax.domain.codesnapshot.entity.CodeSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeSnapshotRepository extends JpaRepository<CodeSnapshot, Long> {

    // 특정 사용자의 특정 과제에 대한 최신 코드 스냅샷 조회
    @Query("SELECT A FROM CodeSnapshot A WHERE A.taskId = :taskId " +
            "AND A.userId = :userId AND A.weeklySessionId = :weeklySessionId " +
            "ORDER BY A.saveAt DESC")
    Optional<CodeSnapshot> findLatestByTaskAndUser(@Param("taskId") Long taskId, @Param("userId") Long userId,
                                                   @Param("weeklySessionId") Long weeklySessionId);

    // 특정 주차 수업과 사용자에 대한 최신 코드 스냅샷 조회 (taskId 없이)
    @Query("SELECT A FROM CodeSnapshot A WHERE A.weeklySessionId = :weeklySessionId " +
            "AND A.userId = :userId ORDER BY A.saveAt DESC LIMIT 1")
    Optional<CodeSnapshot> findLatestByWeeklySessionAndUser(@Param("weeklySessionId") Long weeklySessionId,
                                                             @Param("userId") Long userId);

    // 특정 사용자의 특정 과제에 대한 모든 스냅샷 조회 (시간순)
    @Query("SELECT A FROM CodeSnapshot A WHERE A.taskId = :taskId " +
            "AND A.userId = :userId AND A.weeklySessionId = :weeklySessionId " +
            "ORDER BY A.saveAt DESC")
    List<CodeSnapshot> findAllByTaskAndUser(@Param("taskId") Long taskId, @Param("userId") Long userId,
                                            @Param("weeklySessionId") Long weeklySessionId);

    // 특정 주차 수업의 모든 학생 코드 스냅샷 조회 (실시간 모니터링용)
    @Query("SELECT A FROM CodeSnapshot A WHERE A.weeklySessionId = :weeklySessionId AND A.taskId = :taskId ORDER BY A.saveAt DESC")
    List<CodeSnapshot> findAllByWeeklySessionAndTask(@Param("weeklySessionId") Long weeklySessionId, @Param("taskId") Long taskId);

    // 특정 주차 수업의 특정 학생 코드 스냅샷 조회 (userId 필터링)
    @Query("SELECT A FROM CodeSnapshot A WHERE A.weeklySessionId = :weeklySessionId AND A.taskId = :taskId AND A.userId = :userId ORDER BY A.saveAt DESC")
    List<CodeSnapshot> findAllByWeeklySessionAndTaskAndUser(@Param("weeklySessionId") Long weeklySessionId, @Param("taskId") Long taskId, @Param("userId") Long userId);

    // 특정 사용자의 코드 스냅샷 삭제
    void deleteByTaskIdAndUserIdAndWeeklySessionId(Long taskId, Long userId, Long weeklySessionId);
}
