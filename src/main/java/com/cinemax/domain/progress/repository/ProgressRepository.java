package com.cinemax.domain.progress.repository;

import com.cinemax.domain.progress.entity.Progress;
import com.cinemax.global.enums.StudentActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Progress Repository
 */
@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

    // 주차별 수업의 모든 학생 진도 조회
    @Query("SELECT A FROM Progress A JOIN FETCH A.user LEFT JOIN FETCH A.weeklySession " +
           "WHERE A.weeklySessionId = :weeklySessionId")
    List<Progress> findAllByWeeklySessionId(@Param("weeklySessionId") Long weeklySessionId);

    // 특정 상태의 학생 진도 조회
    @Query("SELECT A FROM Progress A JOIN FETCH A.user WHERE A.weeklySessionId = :weeklySessionId " +
            "AND A.activityStatus = :status")
    List<Progress> findAllByWeeklySessionIdAndStatus(@Param("weeklySessionId") Long weeklySessionId, @Param("status") StudentActivityStatus status);

    // 사용자의 특정 주차 진도 조회
    @Query("SELECT A FROM Progress A JOIN FETCH A.user WHERE A.weeklySessionId = :weeklySessionId " +
            "AND A.user.userId = :userId")
    Optional<Progress> findByWeeklySessionIdAndUserId(@Param("weeklySessionId") Long weeklySessionId, @Param("userId") Long userId);

    // 수업의 모든 학생 진도 조회
    @Query("SELECT A FROM Progress A JOIN FETCH A.user WHERE A.classId = :classId")
    List<Progress> findAllByClassId(@Param("classId") Long classId);

    // 도움이 필요한 학생 목록 조회 (5분 이상 진도 없음 또는 테스트 3회 이상 실패)
    @Query("SELECT A FROM Progress A JOIN FETCH A.user WHERE A.weeklySessionId = :weeklySessionId AND A.activityStatus = 'NEED_HELP'")
    List<Progress> findStudentsNeedingHelp(@Param("weeklySessionId") Long weeklySessionId);

    // 활동중인 학생 목록 조회 (최근 5분 이내 활동)
    @Query("SELECT A FROM Progress A JOIN FETCH A.user WHERE A.weeklySessionId = :weeklySessionId AND A.activityStatus = 'ACTIVE'")
    List<Progress> findActiveStudents(@Param("weeklySessionId") Long weeklySessionId);

    // 완료한 학생 목록 조회
    @Query("SELECT A FROM Progress A JOIN FETCH A.user WHERE A.weeklySessionId = :weeklySessionId AND A.activityStatus = 'COMPLETED'")
    List<Progress> findCompletedStudents(@Param("weeklySessionId") Long weeklySessionId);

    // 학생의 전체 진도 목록 조회
    @Query("SELECT A FROM Progress A JOIN FETCH A.user LEFT JOIN FETCH A.weeklySession WHERE A.user.userId = :userId")
    List<Progress> findAllByUserId(@Param("userId") Long userId);

    // 진도 삭제
    @Query("DELETE FROM Progress A WHERE A.weeklySessionId = :weeklySessionId AND A.user.userId = :userId")
    void deleteByWeeklySessionIdAndUserId(@Param("weeklySessionId") Long weeklySessionId, @Param("userId") Long userId);

    // 학생의 평균 진도율 조회
    @Query("SELECT COALESCE(AVG(A.progressPct), 0) FROM Progress A WHERE A.user.userId = :userId")
    java.math.BigDecimal getAverageProgressByUserId(@Param("userId") Long userId);

    // 특정 활동 상태를 가진 학생 수 조회 (관리자 대시보드용)
    @Query("SELECT COUNT(DISTINCT A.user.userId) FROM Progress A WHERE A.activityStatus = :status")
    Long countStudentsWithActivityStatus(@Param("status") StudentActivityStatus status);

    // "진행 중" 수업들의 모든 진도 조회 (출석률 계산용)
    @Query("SELECT p FROM Progress p " +
           "JOIN p.weeklySession ws " +
           "WHERE ws.status = 'IN_PROGRESS'")
    List<Progress> findAllByInProgressSessions();

    // 특정 수업의 진행 중인 세션들의 모든 진도 조회
    @Query("SELECT p FROM Progress p " +
           "JOIN p.weeklySession ws " +
           "WHERE p.classId = :classId AND ws.status = 'IN_PROGRESS'")
    List<Progress> findAllByClassIdAndInProgressSessions(@Param("classId") Long classId);

    // "진행 중" 수업들의 수강생별 출석률 계산 (완료한 차시 수 / 전체 차시 수)
    @Query("SELECT p.user.userId, " +
           "COUNT(CASE WHEN p.completedDt IS NOT NULL THEN 1 END) * 100.0 / COUNT(p.progressId) " +
           "FROM Progress p " +
           "JOIN p.weeklySession ws " +
           "WHERE ws.status = 'IN_PROGRESS' " +
           "GROUP BY p.user.userId")
    List<Object[]> calculateAttendanceRateByStudent();

    // "진행 중" 수업들의 총 수강생 수 조회
    @Query("SELECT COUNT(DISTINCT p.user.userId) FROM Progress p " +
           "JOIN p.weeklySession ws " +
           "WHERE ws.status = 'IN_PROGRESS'")
    Long countDistinctStudentsInProgressSessions();
}
