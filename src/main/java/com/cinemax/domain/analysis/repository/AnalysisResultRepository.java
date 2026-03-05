package com.cinemax.domain.analysis.repository;

import com.cinemax.domain.analysis.entity.AnalysisResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AI 분석 결과 Repository
 */
@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    // 사용자별 분석 결과 조회 (페이징)
    Page<AnalysisResult> findByUserId(Long userId, Pageable pageable);

    // 사용자 + 과제별 분석 결과 조회
    Page<AnalysisResult> findByUserIdAndTaskId(Long userId, Long taskId, Pageable pageable);

    // 사용자 + 사이클별 분석 결과 조회
    Page<AnalysisResult> findByUserIdAndCycleId(Long userId, Long cycleId, Pageable pageable);

    // 제출 ID로 분석 결과 조회
    List<AnalysisResult> findBySubmitId(Long submitId);

    // 최근 분석 결과 조회
    Optional<AnalysisResult> findFirstByUserIdAndTaskIdOrderByRequestedAtDesc(Long userId, Long taskId);

    // 특정 기간 내 분석 결과 조회
    @Query("SELECT ar FROM AnalysisResult ar " +
            "WHERE ar.userId = :userId " +
            "AND ar.requestedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY ar.requestedAt DESC")
    List<AnalysisResult> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate
    );

    // 수업별 전체 분석 결과 조회 (교수용)
    @Query("SELECT ar FROM AnalysisResult ar " +
            "WHERE ar.classId = :classId " +
            "AND ar.cycleId = :cycleId " +
            "ORDER BY ar.requestedAt DESC")
    Page<AnalysisResult> findByClassIdAndCycleId(@Param("classId") Long classId, 
                                                 @Param("cycleId") Long cycleId, 
                                                 Pageable pageable
    );

    // 과제별 통계 - 요구사항 충족률
    @Query("SELECT COUNT(ar) FROM AnalysisResult ar " +
            "WHERE ar.taskId = :taskId " +
            "AND ar.requirementsMet = true")
    Long countRequirementsMetByTaskId(@Param("taskId") Long taskId);

    // 과제별 통계 - 코드 품질 통과율
    @Query("SELECT COUNT(ar) FROM AnalysisResult ar " +
            "WHERE ar.taskId = :taskId " +
            "AND ar.codeQualityPass = true")
    Long countCodeQualityPassByTaskId(@Param("taskId") Long taskId);

    // 과제별 전체 분석 건수
    Long countByTaskId(Long taskId);

    // 사용자별 전체 분석 건수
    Long countByUserId(Long userId);

 
    // 특정 과제에서 논리 오류가 있는 결과 조회
    @Query("SELECT ar FROM AnalysisResult ar " +
            "WHERE ar.taskId = :taskId " +
            "AND ar.hasLogicError = true " +
            "ORDER BY ar.requestedAt DESC")
    List<AnalysisResult> findLogicErrorsByTaskId(@Param("taskId") Long taskId);

 
    // 특정 과제에서 보안 이슈가 있는 결과 조회
    @Query("SELECT ar FROM AnalysisResult ar " +
            "WHERE ar.taskId = :taskId " +
            "AND ar.hasSecurityIssue = true " +
            "ORDER BY ar.requestedAt DESC")
    List<AnalysisResult> findSecurityIssuesByTaskId(@Param("taskId") Long taskId);

 
    // 평균 토큰 사용량 조회
    @Query("SELECT AVG(ar.totalTokens) FROM AnalysisResult ar " +
            "WHERE ar.taskId = :taskId")
    Double getAverageTokenUsageByTaskId(@Param("taskId") Long taskId);

 
    // 기간별 총 토큰 사용량 조회
    @Query("SELECT SUM(ar.totalTokens) FROM AnalysisResult ar " +
            "WHERE ar.requestedAt BETWEEN :startDate AND :endDate")
    Long getTotalTokenUsageByDateRange(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate
    );

 
    // 기간별 프롬프트 토큰 총합
    @Query("SELECT SUM(ar.promptTokens) FROM AnalysisResult ar " +
            "WHERE ar.requestedAt BETWEEN :startDate AND :endDate")
    Long getTotalPromptTokensByDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate
    );

 
    // 기간별 완성 토큰 총합
    @Query("SELECT SUM(ar.completionTokens) FROM AnalysisResult ar " +
            "WHERE ar.requestedAt BETWEEN :startDate AND :endDate")
    Long getTotalCompletionTokensByDateRange(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate
    );

 
    // 기간별 분석 건수
    @Query("SELECT COUNT(ar) FROM AnalysisResult ar " +
            "WHERE ar.requestedAt BETWEEN :startDate AND :endDate")
    Long countByDateRange(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate
    );

 
    // 전체 토큰 사용량
    @Query("SELECT SUM(ar.totalTokens) FROM AnalysisResult ar")
    Long getTotalTokenUsage();

 
    // 전체 프롬프트 토큰
    @Query("SELECT SUM(ar.promptTokens) FROM AnalysisResult ar")
    Long getTotalPromptTokens();

    //전체 완성 토큰
    @Query("SELECT SUM(ar.completionTokens) FROM AnalysisResult ar")
    Long getTotalCompletionTokens();

 
    //수업/사이클별 토큰 사용량
    @Query("SELECT SUM(ar.totalTokens) FROM AnalysisResult ar " +
            "WHERE ar.classId = :classId AND ar.cycleId = :cycleId")
    Long getTotalTokensByClassAndCycle(@Param("classId") Long classId,
                                       @Param("cycleId") Long cycleId
    );
}
