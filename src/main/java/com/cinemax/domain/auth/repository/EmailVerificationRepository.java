package com.cinemax.domain.auth.repository;

import com.cinemax.domain.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // 이메일로 활성 인증 조회 (만료되지 않은)
    @Query("SELECT A FROM EmailVerification A WHERE A.email = :email AND A.expiresAt > :now AND A.verifiedAt IS NULL ORDER BY A.createDt DESC")
    Optional<EmailVerification> findActiveByEmail(@Param("email") String email, @Param("now") LocalDateTime now);

    // 이메일과 코드로 조회
    @Query("SELECT A FROM EmailVerification A WHERE A.email = :email AND A.code = :code")
    Optional<EmailVerification> findByEmailAndCode(@Param("email") String email, @Param("code") String code);

    // 사용자별 이메일 인증 목록 조회
    List<EmailVerification> findByUserId(Long userId);

    // 사용자의 최신 이메일 인증 조회
    @Query("SELECT A FROM EmailVerification A WHERE A.userId = :userId ORDER BY A.createDt DESC")
    Optional<EmailVerification> findLatestByUserId(@Param("userId") Long userId);

    // 사용자의 미인증 이메일 인증 조회
    @Query("SELECT A FROM EmailVerification A WHERE A.userId = :userId AND A.verifiedAt IS NULL ORDER BY A.createDt DESC")
    Optional<EmailVerification> findUnverifiedByUserId(@Param("userId") Long userId);

    // 사용자의 활성 이메일 인증 조회 (만료되지 않은)
    @Query("SELECT A FROM EmailVerification A WHERE A.userId = :userId AND A.expiresAt > :now AND A.verifiedAt IS NULL ORDER BY A.createDt DESC")
    Optional<EmailVerification> findActiveByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // 특정 인증 코드로 조회
    @Query("SELECT A FROM EmailVerification A WHERE A.userId = :userId AND A.code = :code")
    Optional<EmailVerification> findByUserIdAndCode(@Param("userId") Long userId, @Param("code") String code);

    // 만료된 인증 코드 삭제
    @Modifying
    @Query("DELETE FROM EmailVerification A WHERE A.expiresAt < :now")
    void deleteExpiredVerifications(@Param("now") LocalDateTime now);

    // 사용자의 모든 인증 코드 삭제
    @Modifying
    @Query("DELETE FROM EmailVerification A WHERE A.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // 이메일로 모든 인증 코드 삭제
    @Modifying
    @Query("DELETE FROM EmailVerification A WHERE A.email = :email")
    void deleteByEmail(@Param("email") String email);

    // 사용자의 미인증 인증 코드 삭제
    @Modifying
    @Query("DELETE FROM EmailVerification A WHERE A.userId = :userId AND A.verifiedAt IS NULL")
    void deleteUnverifiedByUserId(@Param("userId") Long userId);

    // 특정 기간 이전의 인증 코드 삭제
    @Modifying
    @Query("DELETE FROM EmailVerification A WHERE A.createDt < :cutoffDate")
    void deleteByCreateDtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

    // 사용자의 인증 시도 횟수 조회
    @Query("SELECT A.attemptCount FROM EmailVerification A WHERE A.userId = :userId ORDER BY A.createDt DESC")
    Optional<Byte> findLatestAttemptCountByUserId(@Param("userId") Long userId);

    // 사용자의 인증 완료 여부 조회
    @Query("SELECT COUNT(A) > 0 FROM EmailVerification A WHERE A.userId = :userId AND A.verifiedAt IS NOT NULL")
    boolean isUserEmailVerified(@Param("userId") Long userId);

    // 이메일의 인증 완료 여부 조회
    @Query("SELECT COUNT(A) > 0 FROM EmailVerification A WHERE A.email = :email AND A.verifiedAt IS NOT NULL")
    boolean isEmailVerified(@Param("email") String email);
}
