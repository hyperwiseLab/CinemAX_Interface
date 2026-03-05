package com.cinemax.domain.worklog.repository;

import com.cinemax.domain.worklog.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * WorkLog Repository
 */
@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

    // 특정 사용자의 특정 주차 업무일지 조회
    @Query("SELECT A FROM WorkLog A JOIN FETCH A.user LEFT JOIN FETCH A.weeklySession WHERE A.userId = :userId " +
            "AND A.weeklySessionId = :weeklySessionId ORDER BY A.logDate DESC")
    List<WorkLog> findByUserIdAndWeeklySessionId(@Param("userId") Long userId,
                                                   @Param("weeklySessionId") Long weeklySessionId);

    // 특정 사용자의 모든 업무일지 조회
    @Query("SELECT A FROM WorkLog A JOIN FETCH A.user LEFT JOIN FETCH A.weeklySession " +
            "WHERE A.userId = :userId ORDER BY A.logDate DESC")
    List<WorkLog> findByUserId(@Param("userId") Long userId);

    // 특정 주차의 모든 학생 업무일지 조회
    @Query("SELECT A FROM WorkLog A JOIN FETCH A.user LEFT JOIN FETCH A.weeklySession " +
            "WHERE A.weeklySessionId = :weeklySessionId ORDER BY A.logDate DESC, A.userId")
    List<WorkLog> findByWeeklySessionId(@Param("weeklySessionId") Long weeklySessionId);

    // 특정 사용자의 특정 날짜 업무일지 조회
    @Query("SELECT A FROM WorkLog A JOIN FETCH A.user LEFT JOIN FETCH A.weeklySession " +
            "WHERE A.userId = :userId AND A.weeklySessionId = :weeklySessionId AND A.logDate = :logDate")
    Optional<WorkLog> findByUserIdAndWeeklySessionIdAndLogDate(@Param("userId") Long userId,
                                                                @Param("weeklySessionId") Long weeklySessionId,
                                                                @Param("logDate") LocalDate logDate);

    // 특정 사용자의 특정 기간 업무일지 조회
    @Query("SELECT A FROM WorkLog A JOIN FETCH A.user LEFT JOIN FETCH A.weeklySession " +
            "WHERE A.userId = :userId AND A.weeklySessionId = :weeklySessionId AND A.logDate BETWEEN :startDate AND :endDate ORDER BY A.logDate DESC")
    List<WorkLog> findByUserIdAndWeeklySessionIdAndDateBetween(@Param("userId") Long userId,
                                                                @Param("weeklySessionId") Long weeklySessionId,
                                                                @Param("startDate") LocalDate startDate,
                                                                @Param("endDate") LocalDate endDate);

    // 특정 주차의 특정 기간 업무일지 조회
    @Query("SELECT A FROM WorkLog A JOIN FETCH A.user LEFT JOIN FETCH A.weeklySession " +
            "WHERE A.weeklySessionId = :weeklySessionId AND A.logDate BETWEEN :startDate AND :endDate ORDER BY A.logDate DESC, A.userId")
    List<WorkLog> findByWeeklySessionIdAndDateBetween(@Param("weeklySessionId") Long weeklySessionId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    // 업무일지 존재 여부 확인
    @Query("SELECT CASE WHEN COUNT(A) > 0 THEN true ELSE false END FROM WorkLog A WHERE A.userId = :userId AND A.weeklySessionId = :weeklySessionId " +
            "AND A.logDate = :logDate")
    boolean existsByUserIdAndWeeklySessionIdAndLogDate(@Param("userId") Long userId,
                                                        @Param("weeklySessionId") Long weeklySessionId,
                                                        @Param("logDate") LocalDate logDate);

    // 특정 사용자의 업무일지 개수 조회
    @Query("SELECT COUNT(A) FROM WorkLog A WHERE A.userId = :userId AND A.weeklySessionId = :weeklySessionId")
    long countByUserIdAndWeeklySessionId(@Param("userId") Long userId,
                                          @Param("weeklySessionId") Long weeklySessionId);

    // 특정 사용자의 최근 업무일지 조회
    @Query("SELECT A FROM WorkLog A JOIN FETCH A.user LEFT JOIN FETCH A.weeklySession WHERE A.userId = :userId AND A.weeklySessionId = :weeklySessionId ORDER BY A.logDate DESC LIMIT 1")
    Optional<WorkLog> findLatestByUserIdAndWeeklySessionId(@Param("userId") Long userId,
                                                            @Param("weeklySessionId") Long weeklySessionId);

    // 특정 사용자의 특정 주차 총 학습 시간 조회
    @Query("SELECT COALESCE(SUM(A.workHours), 0) FROM WorkLog A WHERE A.userId = :userId AND A.weeklySessionId = :weeklySessionId")
    java.math.BigDecimal sumWorkHoursByUserIdAndWeeklySessionId(@Param("userId") Long userId,
                                                                  @Param("weeklySessionId") Long weeklySessionId);

    // 특정 주차의 전체 학생 총 학습 시간 조회
    @Query("SELECT COALESCE(SUM(A.workHours), 0) FROM WorkLog A WHERE A.weeklySessionId = :weeklySessionId")
    java.math.BigDecimal sumWorkHoursByWeeklySessionId(@Param("weeklySessionId") Long weeklySessionId);

    // 특정 주차의 평균 학습 시간 조회
    @Query("SELECT COALESCE(AVG(A.workHours), 0) FROM WorkLog A WHERE A.weeklySessionId = :weeklySessionId")
    java.math.BigDecimal avgWorkHoursByWeeklySessionId(@Param("weeklySessionId") Long weeklySessionId);

    // 특정 주차의 최대 학습 시간 조회
    @Query("SELECT COALESCE(MAX(A.workHours), 0) FROM WorkLog A WHERE A.weeklySessionId = :weeklySessionId")
    java.math.BigDecimal maxWorkHoursByWeeklySessionId(@Param("weeklySessionId") Long weeklySessionId);

    // 특정 주차의 최소 학습 시간 조회
    @Query("SELECT COALESCE(MIN(A.workHours), 0) FROM WorkLog A WHERE A.weeklySessionId = :weeklySessionId")
    java.math.BigDecimal minWorkHoursByWeeklySessionId(@Param("weeklySessionId") Long weeklySessionId);

    // 학생의 전체 총 학습 시간 조회
    @Query("SELECT COALESCE(SUM(A.workHours), 0) FROM WorkLog A WHERE A.userId = :userId")
    java.math.BigDecimal sumTotalWorkHoursByUserId(@Param("userId") Long userId);

    // 학생의 최근 7일 학습 시간 조회
    @Query("SELECT COALESCE(SUM(A.workHours), 0) FROM WorkLog A WHERE A.userId = :userId AND A.logDate >= :startDate")
    java.math.BigDecimal sumRecentWorkHoursByUserId(@Param("userId") Long userId, @Param("startDate") java.time.LocalDate startDate);

    // 학습 패턴 분석용 - 시간대별 학습 시간 조회 (요일별)
    @Query("SELECT FUNCTION('DAYOFWEEK', A.logDate), COALESCE(SUM(A.workHours), 0) " +
           "FROM WorkLog A WHERE A.userId = :userId " +
           "GROUP BY FUNCTION('DAYOFWEEK', A.logDate) " +
           "ORDER BY FUNCTION('DAYOFWEEK', A.logDate)")
    List<Object[]> findWeeklyActivityPatternByUserId(@Param("userId") Long userId);

    // 일관성 점수 계산용 - 최근 30일간 일별 학습 데이터
    @Query("SELECT A.logDate, COALESCE(SUM(A.workHours), 0) " +
           "FROM WorkLog A WHERE A.userId = :userId AND A.logDate >= :startDate " +
           "GROUP BY A.logDate ORDER BY A.logDate")
    List<Object[]> findDailyWorkHoursByUserIdSince(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    // 평균 일일 학습 시간 (분 단위)
    @Query("SELECT COALESCE(AVG(A.workHours), 0) FROM WorkLog A WHERE A.userId = :userId")
    java.math.BigDecimal avgDailyWorkHoursByUserId(@Param("userId") Long userId);

    // ===== Cycle별 집계 쿼리 (간접 연결 방식) =====

    /**
     * Cycle별 WorkLog 통계 조회 (WEEKLY_SESSION을 통한 간접 연결)
     *
     * 연결 경로:
     * WorkLog -> WeeklySession -> ClassInvite -> Class -> Cycle (CUR_ID + WEEK_NO)
     */
    @Query(value = """
            SELECT
                c.CYCLE_ID as cycleId,
                c.WEEK_NO as weekNo,
                c.CYCLE_TITLE as cycleTitle,
                AVG(wl.PROFICIENCY_LEVEL) as proficiencyAvg,
                AVG(wl.DIFFICULTY_LEVEL) as difficultyAvg,
                COUNT(wl.WORK_LOG_ID) as workLogCount,
                SUM(wl.WORK_HOURS) as totalWorkHours
            FROM TBL_WORK_LOG wl
            INNER JOIN TBL_WEEKLY_SESSION ws ON wl.WEEKLY_SESSION_ID = ws.WEEKLY_SESSION_ID
            INNER JOIN TBL_CLASS_INVITE ci ON ws.INVITE_ID = ci.INVITE_ID
            INNER JOIN TBL_CLASS cls ON ci.CLASS_ID = cls.CLASS_ID
            INNER JOIN TBL_CYCLE c ON c.CUR_ID = cls.CUR_ID AND c.WEEK_NO = ws.WEEK_NO
            WHERE wl.USER_ID = :userId AND cls.CUR_ID = :curId
            GROUP BY c.CYCLE_ID, c.WEEK_NO, c.CYCLE_TITLE
            ORDER BY c.WEEK_NO
            """, nativeQuery = true)
    List<Object[]> findCycleStatisticsByUserIdAndCurId(@Param("userId") Long userId,
                                                        @Param("curId") Long curId);

    /**
     * Week별 피드백 조회
     */
    @Query(value = """
            SELECT
                wl.WORK_LOG_ID as workLogId,
                wl.LOG_DATE as logDate,
                wl.MEANINGFUL_CONTENT as meaningfulContent,
                wl.DIFFICULT_CONTENT as difficultContent,
                wl.QUESTION_CONTENT as questionContent
            FROM TBL_WORK_LOG wl
            INNER JOIN TBL_WEEKLY_SESSION ws ON wl.WEEKLY_SESSION_ID = ws.WEEKLY_SESSION_ID
            WHERE wl.USER_ID = :userId AND ws.WEEK_NO = :weekNo
            ORDER BY wl.LOG_DATE DESC
            """, nativeQuery = true)
    List<Object[]> findWeeklyFeedbackByUserIdAndWeekNo(@Param("userId") Long userId,
                                                         @Param("weekNo") Integer weekNo);

    /**
     * 특정 Class의 Cycle별 통계 조회 (전체 학생)
     */
    @Query(value = """
            SELECT
                c.CYCLE_ID as cycleId,
                c.WEEK_NO as weekNo,
                c.CYCLE_TITLE as cycleTitle,
                AVG(wl.PROFICIENCY_LEVEL) as proficiencyAvg,
                AVG(wl.DIFFICULTY_LEVEL) as difficultyAvg,
                COUNT(wl.WORK_LOG_ID) as workLogCount,
                SUM(wl.WORK_HOURS) as totalWorkHours
            FROM TBL_CYCLE c
            LEFT JOIN TBL_WEEKLY_SESSION ws ON c.WEEK_NO = ws.WEEK_NO
            LEFT JOIN TBL_CLASS_INVITE ci ON ws.INVITE_ID = ci.INVITE_ID
            LEFT JOIN TBL_WORK_LOG wl ON wl.WEEKLY_SESSION_ID = ws.WEEKLY_SESSION_ID
            WHERE ci.CLASS_ID = :classId AND c.CUR_ID = (SELECT CUR_ID FROM TBL_CLASS WHERE CLASS_ID = :classId)
            GROUP BY c.CYCLE_ID, c.WEEK_NO, c.CYCLE_TITLE
            ORDER BY c.WEEK_NO
            """, nativeQuery = true)
    List<Object[]> findCycleStatisticsByClassId(@Param("classId") Long classId);

    /**
     * inviteId로 특정 사용자의 모든 업무일지 조회
     */
    @Query("SELECT wl FROM WorkLog wl " +
           "JOIN FETCH wl.user " +
           "LEFT JOIN FETCH wl.weeklySession ws " +
           "WHERE wl.userId = :userId AND ws.inviteId = :inviteId " +
           "ORDER BY wl.logDate DESC")
    List<WorkLog> findByUserIdAndInviteId(@Param("userId") Long userId,
                                          @Param("inviteId") Long inviteId);
}
