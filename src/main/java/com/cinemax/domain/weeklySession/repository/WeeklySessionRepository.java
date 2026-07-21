package com.cinemax.domain.weeklySession.repository;

import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.global.enums.WeeklySessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklySessionRepository extends JpaRepository<WeeklySession, Long> {

    // 초대 ID로 주차별 수업 조회
    List<WeeklySession> findByInviteId(Long inviteId);

    // 초대 ID와 주차 번호로 조회
    Optional<WeeklySession> findByInviteIdAndWeekNo(Long inviteId, Integer weekNo);

    // 초대 ID로 주차별 수업을 주차 순으로 정렬하여 조회
    @Query("SELECT A FROM WeeklySession A WHERE A.inviteId = :inviteId ORDER BY A.weekNo")
    List<WeeklySession> findByInviteIdOrderByWeekNo(@Param("inviteId") Long inviteId);

    // 상태별 주차별 수업 조회 (반 정보까지 fetch join - 응답에 classId/classNm 포함용)
    @Query("SELECT A FROM WeeklySession A " +
            "LEFT JOIN FETCH A.classInvite I LEFT JOIN FETCH I.classEntity " +
            "WHERE A.status = :status")
    List<WeeklySession> findByStatus(@Param("status") WeeklySessionStatus status);

    // 초대 ID와 상태로 주차별 수업 조회
    List<WeeklySession> findByInviteIdAndStatus(Long inviteId, WeeklySessionStatus status);

    // 진행 중인 주차별 수업 조회 (한 수업에서 동시에 하나만 가능)
    @Query("SELECT A FROM WeeklySession A WHERE A.inviteId = :inviteId AND A.status = 'IN_PROGRESS'")
    Optional<WeeklySession> findInProgressSessionByInviteId(@Param("inviteId") Long inviteId);

    // 특정 시간 이전에 시작된 진행 중인 수업 조회 (24시간 자동 종료용)
    @Query("SELECT A FROM WeeklySession A WHERE A.status = 'IN_PROGRESS' AND A.startDt < :cutoffTime")
    List<WeeklySession> findInProgressSessionsBeforeTime(@Param("cutoffTime") LocalDateTime cutoffTime);

    // 초대 ID로 특정 주차 범위의 수업 조회
    @Query("SELECT A FROM WeeklySession A WHERE A.inviteId = :inviteId AND A.weekNo BETWEEN :startWeek AND :endWeek ORDER BY A.weekNo")
    List<WeeklySession> findByInviteIdAndWeekNoBetween(@Param("inviteId") Long inviteId,
                                                        @Param("startWeek") Integer startWeek,
                                                        @Param("endWeek") Integer endWeek);

    // 초대 ID로 주차 수 조회
    @Query("SELECT COUNT(A) FROM WeeklySession A WHERE A.inviteId = :inviteId")
    Long countByInviteId(@Param("inviteId") Long inviteId);

    // 초대 ID로 특정 상태의 주차 수 조회
    @Query("SELECT COUNT(A) FROM WeeklySession A WHERE A.inviteId = :inviteId AND A.status = :status")
    Long countByInviteIdAndStatus(@Param("inviteId") Long inviteId, @Param("status") WeeklySessionStatus status);

    // 특정 초대의 최대 주차 번호 조회
    @Query("SELECT MAX(A.weekNo) FROM WeeklySession A WHERE A.inviteId = :inviteId")
    Optional<Integer> findMaxWeekNoByInviteId(@Param("inviteId") Long inviteId);

    // 특정 초대의 완료된 주차 수 조회
    @Query("SELECT COUNT(A) FROM WeeklySession A WHERE A.inviteId = :inviteId AND A.status = 'COMPLETED'")
    Long countCompletedSessionsByInviteId(@Param("inviteId") Long inviteId);

    // 오늘 진행 중인 주차별 수업 조회 (startDt가 오늘이고 IN_PROGRESS 상태)
    @Query("SELECT A FROM WeeklySession A WHERE A.status = 'IN_PROGRESS' " +
           "AND FUNCTION('DATE', A.startDt) = FUNCTION('DATE', :today)")
    List<WeeklySession> findTodayInProgressSessions(@Param("today") LocalDateTime today);

    // 교수의 수업들 중 오늘 진행 중인 세션 조회
    @Query("SELECT A FROM WeeklySession A JOIN A.classInvite B JOIN B.classEntity C " +
           "WHERE C.user.userId = :professorId AND A.status = 'IN_PROGRESS' AND FUNCTION('DATE', A.startDt) = FUNCTION('DATE', :today)")
    List<WeeklySession> findTodaySessionsByProfessorId(@Param("professorId") Long professorId,
                                                        @Param("today") LocalDateTime today);

    // 상태별 주차별 세션 수 조회 (관리자 대시보드용)
    Long countByStatus(WeeklySessionStatus status);

    // 특정 수업(ClassId)의 모든 주차 세션 조회 (주차 순서대로)
    @Query("SELECT A FROM WeeklySession A JOIN A.classInvite B " +
           "WHERE B.classEntity.classId = :classId ORDER BY A.weekNo")
    List<WeeklySession> findAllByClassId(@Param("classId") Long classId);
}
