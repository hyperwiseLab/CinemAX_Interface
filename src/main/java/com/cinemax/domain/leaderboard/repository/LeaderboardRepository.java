package com.cinemax.domain.leaderboard.repository;

import com.cinemax.domain.leaderboard.dto.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 수업별 학생 랭킹 정보 조회
 */
@Repository
public interface LeaderboardRepository extends JpaRepository<com.cinemax.domain.user.entity.User, Long> {


    //특정 수업의 리더보드 조회 (점수 기준 내림차순)
    @Query(value = """
        SELECT
            u.USER_ID as userId,
            u.NAME as userName,
            u.STUDENT_NUM as studentNum,
            COALESCE(SUM(cs.SCORE), 0) as totalScore,
            COUNT(DISTINCT cs.SUBMIT_ID) as submissionCount,
            COALESCE(AVG(p.PROGRESS_PCT), 0) as averageProgress,
            COUNT(DISTINCT CASE WHEN cs.RESULT = true THEN cs.TASK_ID END) as completedTasks
        FROM TBL_USER u
            INNER JOIN TBL_CLASS_ENROLL ce ON u.USER_ID = ce.USER_ID
            LEFT JOIN TBL_CLASS_SUBMIT cs ON u.USER_ID = cs.SUBMIT_ID AND cs.CLASS_ID = :classId AND cs.SUBMIT_YN = true
            LEFT JOIN TBL_PROGRESS p ON u.USER_ID = p.USER_ID AND p.CLASS_ID = :classId
        WHERE ce.CLASS_ID = :classId
            AND ce.STATUS = 'ACTIVE'
            AND u.ROLE = 'STUDENT'
        GROUP BY u.USER_ID, u.NAME, u.STUDENT_NUM
        ORDER BY totalScore DESC, submissionCount DESC
        """, nativeQuery = true)
    List<Object[]> findLeaderboardByClassId(@Param("classId") Long classId);


    //특정 수업의 특정 사용자 랭킹 정보 조회
    @Query(value = """
        SELECT
            u.USER_ID as userId,
            u.NAME as userName,
            u.STUDENT_NUM as studentNum,
            COALESCE(SUM(cs.SCORE), 0) as totalScore,
            COUNT(DISTINCT cs.SUBMIT_ID) as submissionCount,
            COALESCE(AVG(p.PROGRESS_PCT), 0) as averageProgress,
            COUNT(DISTINCT CASE WHEN cs.RESULT = true THEN cs.TASK_ID END) as completedTasks
        FROM TBL_USER u
            LEFT JOIN TBL_CLASS_SUBMIT cs ON u.USER_ID = cs.SUBMIT_ID AND cs.CLASS_ID = :classId AND cs.SUBMIT_YN = true
            LEFT JOIN TBL_PROGRESS p ON u.USER_ID = p.USER_ID AND p.CLASS_ID = :classId
        WHERE u.USER_ID = :userId
        GROUP BY u.USER_ID, u.NAME, u.STUDENT_NUM
        """, nativeQuery = true)
    Optional<Object[]> findUserLeaderboardEntry(@Param("classId") Long classId, @Param("userId") Long userId);
}
