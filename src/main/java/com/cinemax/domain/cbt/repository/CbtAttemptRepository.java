package com.cinemax.domain.cbt.repository;

import com.cinemax.domain.cbt.entity.CbtAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CbtAttemptRepository extends JpaRepository<CbtAttempt, Long> {

    List<CbtAttempt> findByClassIdAndUserIdOrderByRoundNoDesc(Long classId, Long userId);

    List<CbtAttempt> findByClassIdOrderByUserIdAscRoundNoAsc(Long classId);

    // 전체 모의고사 기록만 (weekNo IS NULL) - 주차별 응시가 섞이지 않도록 분리
    @Query("SELECT a FROM CbtAttempt a WHERE a.classId = :classId AND a.userId = :userId " +
            "AND a.weekNo IS NULL ORDER BY a.roundNo DESC")
    List<CbtAttempt> findFullExamAttempts(@Param("classId") Long classId, @Param("userId") Long userId);

    // 특정 주차 기록만
    @Query("SELECT a FROM CbtAttempt a WHERE a.classId = :classId AND a.userId = :userId " +
            "AND a.weekNo = :weekNo ORDER BY a.roundNo DESC")
    List<CbtAttempt> findWeekAttempts(@Param("classId") Long classId, @Param("userId") Long userId,
                                      @Param("weekNo") Integer weekNo);

    // 내 주차별 응시 기록 전체 (주차별 화면 목록용)
    @Query("SELECT a FROM CbtAttempt a WHERE a.classId = :classId AND a.userId = :userId " +
            "AND a.weekNo IS NOT NULL ORDER BY a.weekNo ASC, a.roundNo DESC")
    List<CbtAttempt> findAllWeekAttempts(@Param("classId") Long classId, @Param("userId") Long userId);

    // 회차 채번: 전체 모의고사(weekNo IS NULL)와 주차별을 독립적으로 센다
    @Query("SELECT COALESCE(MAX(a.roundNo), 0) FROM CbtAttempt a WHERE a.classId = :classId " +
            "AND a.userId = :userId AND a.weekNo IS NULL")
    int findMaxRoundNo(@Param("classId") Long classId, @Param("userId") Long userId);

    @Query("SELECT COALESCE(MAX(a.roundNo), 0) FROM CbtAttempt a WHERE a.classId = :classId " +
            "AND a.userId = :userId AND a.weekNo = :weekNo")
    int findMaxWeekRoundNo(@Param("classId") Long classId, @Param("userId") Long userId,
                           @Param("weekNo") Integer weekNo);

    void deleteByClassId(Long classId);
}
