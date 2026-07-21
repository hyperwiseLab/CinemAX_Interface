package com.cinemax.domain.classes.repository;

import com.cinemax.domain.classes.entity.ClassSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface ClassSubmitRepository extends JpaRepository<ClassSubmit, Long> {

    // 특정 학생의 특정 과제 제출 이력 조회
    @Query("SELECT A FROM ClassSubmit A WHERE A.taskId = :taskId " +
            "AND A.classId = :classId ORDER BY A.submitAt DESC")
    List<ClassSubmit> findByTaskAndClass(@Param("taskId") Long taskId,
                                         @Param("classId") Long classId);

    // 특정 과제의 모든 제출 조회 (교수용)
    @Query("SELECT A FROM ClassSubmit A WHERE A.taskId = :taskId " +
            "AND A.classId = :classId AND A.submitYn = true ORDER BY A.submitAt DESC")
    List<ClassSubmit> findAllByTask(@Param("taskId") Long taskId,
                                    @Param("classId") Long classId);

    // 특정 수업의 특정 주차 모든 제출 조회
    @Query("SELECT A FROM ClassSubmit A WHERE A.weeklySessionId = :weeklySessionId " +
            "AND A.classId = :classId AND A.submitYn = true ORDER BY A.submitAt DESC")
    List<ClassSubmit> findAllByWeeklySession(@Param("weeklySessionId") Long weeklySessionId,
                                             @Param("classId") Long classId);

    // 특정 수업의 모든 제출 조회 (교수 대시보드용)
    @Query("SELECT A FROM ClassSubmit A WHERE A.classId = :classId " +
            "AND A.submitYn = true ORDER BY A.submitAt DESC")
    List<ClassSubmit> findAllByClass(@Param("classId") Long classId);

    // 제출 횟수 조회
    @Query("SELECT COUNT(A) FROM ClassSubmit A WHERE A.taskId = :taskId " +
            "AND A.classId = :classId AND A.submitYn = true")
    Long countSubmissions(@Param("taskId") Long taskId,
                          @Param("classId") Long classId);

    // 학생별 "이번 세션의 이 사이클" 제출 수 (중복 제출 판정용 - 난이도 전환으로 taskId가 바뀌어도 잡힘)
    @Query("SELECT COUNT(A) FROM ClassSubmit A WHERE A.userId = :userId " +
            "AND A.weeklySessionId = :weeklySessionId AND A.cycleId = :cycleId AND A.submitYn = true")
    Long countUserCycleSubmissions(@Param("userId") Long userId,
                                   @Param("weeklySessionId") Long weeklySessionId,
                                   @Param("cycleId") Long cycleId);

    // 같은 (본인+세션+사이클)의 유효 제출 목록 (재제출 덮어쓰기 시 무효화 대상)
    @Query("SELECT A FROM ClassSubmit A WHERE A.userId = :userId " +
            "AND A.weeklySessionId = :weeklySessionId AND A.cycleId = :cycleId AND A.submitYn = true")
    List<ClassSubmit> findActiveUserCycleSubmissions(@Param("userId") Long userId,
                                                     @Param("weeklySessionId") Long weeklySessionId,
                                                     @Param("cycleId") Long cycleId);

    // 합격한 학생 수 조회
    @Query("SELECT COUNT(A) FROM ClassSubmit A WHERE A.taskId = :taskId " +
            "AND A.classId = :classId AND A.result = true AND A.submitYn = true")
    Long countPassedStudents(@Param("taskId") Long taskId,
                             @Param("classId") Long classId);

    // 첫 제출 여부 확인
    @Query("SELECT CASE WHEN COUNT(A) > 0 THEN false ELSE true END FROM ClassSubmit A " +
            "WHERE A.taskId = :taskId AND A.classId = :classId AND A.submitYn = true")
    boolean isFirstSubmission(@Param("taskId") Long taskId,
                              @Param("classId") Long classId);

    // 주차별 세션의 전체 제출 수 조회
    @Query("SELECT COUNT(A) FROM ClassSubmit A WHERE A.weeklySessionId = :weeklySessionId AND A.submitYn = true")
    Long countByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);

    // 주차별 세션의 합격 제출 수 조회
    @Query("SELECT COUNT(A) FROM ClassSubmit A WHERE A.weeklySessionId = :weeklySessionId " +
            "AND A.result = true AND A.submitYn = true")
    Long countPassedByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);

    // 주차별 세션의 불합격 제출 수 조회
    @Query("SELECT COUNT(A) FROM ClassSubmit A WHERE A.weeklySessionId = :weeklySessionId " +
            "AND A.result = false AND A.submitYn = true")
    Long countFailedByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);

    // 주차별 세션의 제출한 학생 수 조회
    @Query("SELECT COUNT(A) FROM ClassSubmit A WHERE A.weeklySessionId = :weeklySessionId AND A.submitYn = true")
    Long countDistinctStudentsByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);

    // 주차별 세션의 합격한 학생 수 조회
    @Query("SELECT COUNT(A) FROM ClassSubmit A WHERE A.weeklySessionId = :weeklySessionId " +
            "AND A.result = true AND A.submitYn = true")
    Long countDistinctPassedStudentsByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);

    // 주차별 세션 제출 목록 (기간 필터링용)
    @Query("SELECT A FROM ClassSubmit A WHERE A.weeklySessionId = :weeklySessionId AND A.submitYn = true")
    List<ClassSubmit> findByWeeklySessionId(@Param("weeklySessionId") Long weeklySessionId);

    // 학생이 제출한 총 과제 수 조회 (중복 제거 - 과제별 최신 제출만)
    @Query("SELECT COUNT(DISTINCT A.taskId) FROM ClassSubmit A WHERE A.submitYn = true")
    Long countSubmittedAssignments();

    // 학생이 합격한 총 과제 수 조회 (중복 제거)
    @Query("SELECT COUNT(DISTINCT A.taskId) FROM ClassSubmit A WHERE A.result = true AND A.submitYn = true")
    Long countPassedAssignments();

    // 학생이 불합격한 총 과제 수 조회 (중복 제거)
    @Query("SELECT COUNT(DISTINCT A.taskId) FROM ClassSubmit A WHERE A.result = false AND A.submitYn = true")
    Long countFailedAssignments();

    // 특정 시간 이후 제출된 코드 수 조회 (관리자 대시보드용 - 오늘 제출 수)
    @Query("SELECT COUNT(A) FROM ClassSubmit A WHERE A.submitAt >= :submitAt AND A.submitYn = true")
    Long countBySubmitAtAfter(@Param("submitAt") LocalDateTime submitAt);

    // 전체 합격한 제출 수 조회 (관리자 대시보드용 - 평균 합격률 계산)
    @Query("SELECT COUNT(A) FROM ClassSubmit A WHERE A.result = true AND A.submitYn = true")
    Long countPassed();

    // "진행 중" 수업들의 학생별 과제 성공률 계산 (성공한 과제 수 / 전체 과제 수)
    // 학생별로 제출한 과제 중에서 성공한 과제의 비율을 계산
    @Query("SELECT e.user.userId, " +
            "COUNT(DISTINCT CASE WHEN cs.result = true THEN cs.taskId END) * 100.0 / COUNT(DISTINCT cs.taskId) " +
            "FROM ClassSubmit cs, ClassEnroll e, WeeklySession ws " +
            "WHERE cs.classEntity = e.classEntity " +
            "AND cs.weeklySessionId = ws.weeklySessionId " +
            "AND ws.status = 'IN_PROGRESS' AND cs.submitYn = true " +
            "GROUP BY e.user.userId")
    List<Object[]> calculateSuccessRateByStudentInProgressClasses();

    // "진행 중" 수업들의 모든 제출 조회
    @Query("SELECT cs FROM ClassSubmit cs, WeeklySession ws " +
            "WHERE cs.weeklySessionId = ws.weeklySessionId " +
            "AND ws.status = 'IN_PROGRESS' AND cs.submitYn = true")
    List<ClassSubmit> findAllByInProgressSessions();

    // 특정 학생의 특정 수업에서의 과제 성공률 (성공한 유니크 과제 수 / 전체 유니크 과제 수)
    @Query("SELECT " +
            "COUNT(DISTINCT CASE WHEN cs.result = true THEN cs.taskId END) * 100.0 / " +
            "COUNT(DISTINCT cs.taskId) " +
            "FROM ClassSubmit cs " +
            "JOIN cs.classEntity.enrollments e " +
            "WHERE e.user.userId = :userId AND cs.classId = :classId AND cs.submitYn = true")
    Double calculateStudentSuccessRateInClass(@Param("userId") Long userId, @Param("classId") Long classId);

    // 진행 중인 수업에 등록된 모든 학생의 ID 조회
    @Query("SELECT DISTINCT e.user.userId " +
            "FROM ClassEnroll e, WeeklySession ws " +
            "WHERE ws.classInvite.classEntity.classId = e.classEntity.classId " +
            "AND ws.status = 'IN_PROGRESS' AND e.status = 'ACTIVE'")
    List<Long> findAllStudentIdsInProgressClasses();

    // 진행 중인 수업 ID 목록 조회
    @Query("SELECT DISTINCT c.classId " +
            "FROM ClassEntity c, WeeklySession ws " +
            "WHERE ws.classInvite.classEntity.classId = c.classId " +
            "AND ws.status = 'IN_PROGRESS'")
    List<Long> findAllInProgressClassIds();

    // 특정 주차 세션의 과제별 통계 조회 (과제 ID, 총 제출 수, 성공 수, 평균 시도 횟수)
    @Query("SELECT cs.taskId, " +
            "COUNT(DISTINCT cs.submitId), " +
            "COUNT(DISTINCT CASE WHEN cs.result = true THEN cs.submitId END), " +
            "AVG(cs.submitNum) " +
            "FROM ClassSubmit cs " +
            "WHERE cs.weeklySessionId = :weeklySessionId AND cs.submitYn = true " +
            "GROUP BY cs.taskId")
    List<Object[]> calculateTaskStatisticsByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);

    // 특정 수업의 과제별 전체 통계 조회 (난이도 분석용)
    @Query("SELECT cs.taskId, " +
            "COUNT(DISTINCT CASE WHEN cs.result = true THEN cs.submitId END) * 100.0 / COUNT(DISTINCT cs.submitId), " +
            "AVG(cs.submitNum), " +
            "AVG(TIMESTAMPDIFF(MINUTE, cs.createDt, cs.submitAt)) " +
            "FROM ClassSubmit cs " +
            "WHERE cs.classId = :classId AND cs.submitYn = true " +
            "GROUP BY cs.taskId")
    List<Object[]> calculateTaskDifficultyStatistics(@Param("classId") Long classId);

    // 특정 주차의 평균 제출 시간 (분 단위)
    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, cs.createDt, cs.submitAt)) " +
            "FROM ClassSubmit cs " +
            "WHERE cs.weeklySessionId = :weeklySessionId AND cs.submitYn = true AND cs.result = true")
    Double calculateAverageSubmissionTimeByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);

    // 특정 주차의 평균 재도전 횟수
    @Query("SELECT AVG(cs.submitNum) " +
            "FROM ClassSubmit cs " +
            "WHERE cs.weeklySessionId = :weeklySessionId AND cs.submitYn = true")
    Double calculateAverageRetryCountByWeeklySession(@Param("weeklySessionId") Long weeklySessionId);

    // 학생의 최근 N개 과제 제출 시간 추세 (문제 해결 속도 추세 분석용)
    @Query("SELECT TIMESTAMPDIFF(MINUTE, cs.createDt, cs.submitAt) " +
            "FROM ClassSubmit cs, ClassEnroll e " +
            "WHERE cs.classEntity = e.classEntity " +
            "AND e.user.userId = :userId " +
            "AND cs.submitYn = true AND cs.result = true " +
            "ORDER BY cs.submitAt DESC")
    List<Double> findRecentSubmissionTimesByUserId(@Param("userId") Long userId);

    // 학생의 Syntax별 성공률 조회 (강점/약점 분석용)
    // Cycle의 syntaxId를 이용하여 문법별로 그룹화하고 성공률 계산
    @Query("SELECT c.syntaxId, " +
            "COUNT(DISTINCT CASE WHEN cs.result = true THEN cs.submitId END) * 100.0 / COUNT(DISTINCT cs.submitId), " +
            "COUNT(DISTINCT cs.submitId) " +
            "FROM ClassSubmit cs, Task t, Cycle c, ClassEnroll e " +
            "WHERE cs.taskId = t.taskId " +
            "AND t.cycleId = c.cycleId " +
            "AND cs.classEntity = e.classEntity " +
            "AND e.user.userId = :userId AND cs.submitYn = true AND c.syntaxId IS NOT NULL " +
            "GROUP BY c.syntaxId " +
            "ORDER BY COUNT(DISTINCT CASE WHEN cs.result = true THEN cs.submitId END) * 100.0 / COUNT(DISTINCT cs.submitId)")
    List<Object[]> calculateSyntaxSuccessRateByUserId(@Param("userId") Long userId);

    // 특정 학생의 특정 과제에 대한 성공률 계산 (성공한 제출 수 / 전체 제출 수 * 100)
    @Query("SELECT " +
            "CASE WHEN COUNT(cs.submitId) = 0 THEN 0.0 " +
            "ELSE COUNT(CASE WHEN cs.result = true THEN 1 END) * 100.0 / COUNT(cs.submitId) END " +
            "FROM ClassSubmit cs " +
            "WHERE cs.taskId = :taskId AND cs.classId = :classId AND cs.submitYn = true")
    Double calculateTaskSuccessRate(@Param("taskId") Long taskId, @Param("classId") Long classId);
}
