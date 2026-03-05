package com.cinemax.domain.auth.repository;

import com.cinemax.domain.auth.entity.AuthLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthLogRepository extends JpaRepository<AuthLog, Long> {

    // 사용자 ID로 인증 로그 조회
    @Query("SELECT A FROM AuthLog A WHERE A.user.userId = :userId")
    List<AuthLog> findByUserId(@Param("userId") Long userId);

    // 사용자 ID로 인증 로그 조회 (페이징)
    @Query("SELECT A FROM AuthLog A WHERE A.user.userId = :userId ORDER BY A.createDt DESC")
    Page<AuthLog> findByUserIdOrderByCreateDtDesc(@Param("userId") Long userId, Pageable pageable);

    // 이메일로 인증 로그 조회
    List<AuthLog> findByEmail(String email);

    // 이메일로 인증 로그 조회 (페이징)
    @Query("SELECT A FROM AuthLog A WHERE A.email = :email ORDER BY A.createDt DESC")
    Page<AuthLog> findByEmailOrderByCreateDtDesc(@Param("email") String email, Pageable pageable);

    // 이벤트 타입으로 인증 로그 조회
    @Query("SELECT A FROM AuthLog A WHERE A.event = :event ORDER BY A.createDt DESC")
    List<AuthLog> findByEventOrderByCreateDtDesc(@Param("event") Integer event);

    // 이벤트 타입으로 인증 로그 조회 (페이징)
    @Query("SELECT A FROM AuthLog A WHERE A.event = :event ORDER BY A.createDt DESC")
    Page<AuthLog> findByEventOrderByCreateDtDesc(@Param("event") Integer event, Pageable pageable);

    // 사용자 ID와 이벤트 타입으로 인증 로그 조회
    @Query("SELECT A FROM AuthLog A WHERE A.user.userId = :userId AND A.event = :event ORDER BY A.createDt DESC")
    List<AuthLog> findByUserIdAndEventOrderByCreateDtDesc(@Param("userId") Long userId, @Param("event") Integer event);

    // 특정 기간 동안의 인증 로그 조회
    @Query("SELECT A FROM AuthLog A WHERE A.createDt BETWEEN :startDate AND :endDate ORDER BY A.createDt DESC")
    List<AuthLog> findByCreateDtBetweenOrderByCreateDtDesc(@Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);

    // 특정 기간 동안의 인증 로그 조회 (페이징)
    @Query("SELECT A FROM AuthLog A WHERE A.createDt BETWEEN :startDate AND :endDate ORDER BY A.createDt DESC")
    Page<AuthLog> findByCreateDtBetweenOrderByCreateDtDesc(@Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate,
                                                          Pageable pageable);

    // 사용자 ID와 특정 기간 동안의 인증 로그 조회
    @Query("SELECT A FROM AuthLog A WHERE A.user.userId = :userId AND A.createDt BETWEEN :startDate AND :endDate ORDER BY A.createDt DESC")
    List<AuthLog> findByUserIdAndCreateDtBetweenOrderByCreateDtDesc(@Param("userId") Long userId,
                                                                   @Param("startDate") LocalDateTime startDate,
                                                                   @Param("endDate") LocalDateTime endDate);

    // 최근 로그인 성공 로그 조회
    @Query("SELECT A FROM AuthLog A WHERE A.user.userId = :userId AND A.event = :event ORDER BY A.createDt DESC")
    Optional<AuthLog> findLatestLoginSuccessByUserId(@Param("userId") Long userId, @Param("event") Integer event);

    // 최근 로그인 실패 로그 조회 (최대 5개)
    @Query("SELECT A FROM AuthLog A WHERE A.user.userId = :userId AND A.event = :event ORDER BY A.createDt DESC")
    List<AuthLog> findTop5LatestLoginFailuresByUserId(@Param("userId") Long userId, @Param("event") Integer event);

    // 특정 시간 내 로그인 실패 횟수 조회
    @Query("SELECT COUNT(A) FROM AuthLog A WHERE A.user.userId = :userId AND A.event = :event AND A.createDt >= :since")
    Long countLoginFailuresSince(@Param("userId") Long userId, @Param("event") Integer event, @Param("since") LocalDateTime since);

    // 이메일로 특정 시간 내 로그인 실패 횟수 조회
    @Query("SELECT COUNT(A) FROM AuthLog A WHERE A.email = :email AND A.event = :event AND A.createDt >= :since")
    Long countLoginFailuresByEmailSince(@Param("email") String email, @Param("event") Integer event, @Param("since") LocalDateTime since);

    // 사용자별 인증 로그 통계 조회
    @Query("SELECT A.event, COUNT(A) FROM AuthLog A WHERE A.user.userId = :userId GROUP BY A.event")
    List<Object[]> getAuthLogStatisticsByUserId(@Param("userId") Long userId);

    // 전체 인증 로그 통계 조회
    @Query("SELECT A.event, COUNT(A) FROM AuthLog A GROUP BY A.event")
    List<Object[]> getOverallAuthLogStatistics();

    // 특정 기간 동안의 인증 로그 통계 조회
    @Query("SELECT A.event, COUNT(A) FROM AuthLog A WHERE A.createDt BETWEEN :startDate AND :endDate GROUP BY A.event")
    List<Object[]> getAuthLogStatisticsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    // 의심스러운 활동 감지 (다중 로그인 실패)
    @Query("SELECT A FROM AuthLog A WHERE A.user.userId = :userId AND A.event = :event AND A.createDt >= :since ORDER BY A.createDt DESC")
    List<AuthLog> findSuspiciousLoginFailures(@Param("userId") Long userId, @Param("event") Integer event, @Param("since") LocalDateTime since);

    // 이메일로 의심스러운 활동 감지
    @Query("SELECT A FROM AuthLog A WHERE A.email = :email AND A.event = :event AND A.createDt >= :since ORDER BY A.createDt DESC")
    List<AuthLog> findSuspiciousLoginFailuresByEmail(@Param("email") String email, @Param("event") Integer event, @Param("since") LocalDateTime since);
}

