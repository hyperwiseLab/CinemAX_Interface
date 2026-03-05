package com.cinemax.domain.activity.repository;

import com.cinemax.domain.activity.entity.ActivityMonitor;
import com.cinemax.global.enums.ActivityAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ActivityMonitor Repository
 * 학생 활동 로그 조회
 */
@Repository
public interface ActivityMonitorRepository extends JpaRepository<ActivityMonitor, Long> {

    // 주차별 수업의 모든 활동 로그 조회
    @Query("SELECT a FROM ActivityMonitor a LEFT JOIN FETCH a.weeklySession WHERE a.weeklySessionId = :weeklySessionId ORDER BY a.lastSeenDt DESC")
    List<ActivityMonitor> findAllByWeeklySessionId(@Param("weeklySessionId") Long weeklySessionId);

    // 특정 학생(inviteId)의 주차별 활동 로그 조회
    @Query("SELECT a FROM ActivityMonitor a LEFT JOIN FETCH a.weeklySession WHERE a.weeklySessionId = :weeklySessionId AND a.inviteId = :inviteId " +
            "ORDER BY a.lastSeenDt DESC")
    List<ActivityMonitor> findByWeeklySessionIdAndInviteId(@Param("weeklySessionId") Long weeklySessionId,
                                                            @Param("inviteId") Long inviteId);

    // 특정 학생의 최근 활동 조회
    @Query(value = "SELECT a.* FROM tbl_activity_monitor a WHERE a.weekly_session_id = :weeklySessionId AND a.invite_id = :inviteId " +
            "ORDER BY a.last_seen_dt DESC LIMIT 1", nativeQuery = true)
    Optional<ActivityMonitor> findLatestByWeeklySessionIdAndInviteId(@Param("weeklySessionId") Long weeklySessionId,
                                                                      @Param("inviteId") Long inviteId);

    // 특정 액션 타입의 활동 로그 조회
    @Query("SELECT a FROM ActivityMonitor a LEFT JOIN FETCH a.weeklySession WHERE a.weeklySessionId = :weeklySessionId AND a.lastAction = :action " +
            "ORDER BY a.lastSeenDt DESC")
    List<ActivityMonitor> findByWeeklySessionIdAndAction(@Param("weeklySessionId") Long weeklySessionId,
                                                          @Param("action") ActivityAction action);

    // 특정 시간 이후의 활동 로그 조회
    @Query("SELECT a FROM ActivityMonitor a LEFT JOIN FETCH a.weeklySession WHERE a.weeklySessionId = :weeklySessionId AND a.lastSeenDt >= :fromTime " +
            "ORDER BY a.lastSeenDt DESC")
    List<ActivityMonitor> findRecentActivities(@Param("weeklySessionId") Long weeklySessionId,
                                               @Param("fromTime") LocalDateTime fromTime);

    // 특정 학생의 특정 시간 이후 활동 로그 조회
    @Query("SELECT a FROM ActivityMonitor a LEFT JOIN FETCH a.weeklySession WHERE a.weeklySessionId = :weeklySessionId " +
            "AND a.inviteId = :inviteId " +
            "AND a.lastSeenDt >= :fromTime " +
            "ORDER BY a.lastSeenDt DESC")
    List<ActivityMonitor> findRecentActivitiesByInviteId(@Param("weeklySessionId") Long weeklySessionId,
                                                          @Param("inviteId") Long inviteId,
                                                          @Param("fromTime") LocalDateTime fromTime);

    // 특정 학생의 특정 액션 타입 활동 로그 조회
    @Query("SELECT a FROM ActivityMonitor a LEFT JOIN FETCH a.weeklySession WHERE a.weeklySessionId = :weeklySessionId " +
            "AND a.inviteId = :inviteId " +
            "AND a.lastAction = :action " +
            "ORDER BY a.lastSeenDt DESC")
    List<ActivityMonitor> findByWeeklySessionIdAndInviteIdAndAction(@Param("weeklySessionId") Long weeklySessionId,
                                                                     @Param("inviteId") Long inviteId,
                                                                     @Param("action") ActivityAction action);

    // 특정 시간 범위의 활동 로그 조회
    @Query("SELECT a FROM ActivityMonitor a LEFT JOIN FETCH a.weeklySession WHERE a.weeklySessionId = :weeklySessionId " +
            "AND a.lastSeenDt BETWEEN :startTime AND :endTime " +
            "ORDER BY a.lastSeenDt DESC")
    List<ActivityMonitor> findActivitiesBetween(@Param("weeklySessionId") Long weeklySessionId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    // 특정 활동 로그 존재 여부 확인
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM ActivityMonitor a WHERE a.weeklySessionId = :weeklySessionId AND a.inviteId = :inviteId")
    boolean existsByWeeklySessionIdAndInviteId(@Param("weeklySessionId") Long weeklySessionId,
                                                @Param("inviteId") Long inviteId);

    // 활동 로그 개수 조회
    @Query("SELECT COUNT(a) FROM ActivityMonitor a WHERE a.weeklySessionId = :weeklySessionId AND a.inviteId = :inviteId")
    long countByWeeklySessionIdAndInviteId(@Param("weeklySessionId") Long weeklySessionId,
                                            @Param("inviteId") Long inviteId);

    // 특정 액션 타입의 활동 개수 조회
    @Query("SELECT COUNT(a) FROM ActivityMonitor a WHERE a.weeklySessionId = :weeklySessionId " +
            "AND a.inviteId = :inviteId " +
            "AND a.lastAction = :action")
    long countByWeeklySessionIdAndInviteIdAndAction(@Param("weeklySessionId") Long weeklySessionId,
                                                     @Param("inviteId") Long inviteId,
                                                     @Param("action") ActivityAction action);

    // 유저 + 과제 기준 에러(테스트 실패) 개수 조회
    @Query("SELECT COUNT(a) FROM ActivityMonitor a JOIN Progress p ON p.weeklySessionId = a.weeklySessionId " +
            "WHERE p.user.userId = :userId AND a.taskId = :taskId AND a.lastAction = :errorAction")
    long countErrorByUserIdAndTaskId(@Param("userId") Long userId,
                                     @Param("taskId") Long taskId,
                                     @Param("errorAction") ActivityAction errorAction);
}
