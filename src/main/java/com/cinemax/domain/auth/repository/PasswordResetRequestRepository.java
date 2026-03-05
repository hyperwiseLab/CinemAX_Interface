package com.cinemax.domain.auth.repository;

import com.cinemax.domain.auth.entity.PasswordResetRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 비밀번호 재설정 요청 Repository
 */
@Repository
public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequestEntity, Long> {

    // 이메일로 활성화된 요청 조회
    @Query("SELECT A FROM PasswordResetRequestEntity A WHERE A.email = :email AND A.status = 'PENDING' AND A.expiresAt > :now ORDER BY A.createDt DESC")
    Optional<PasswordResetRequestEntity> findActiveByEmail(@Param("email") String email, @Param("now") LocalDateTime now);

    // 이메일과 인증 코드로 요청 조회
    @Query("SELECT A FROM PasswordResetRequestEntity A WHERE A.email = :email AND A.verificationCode = :code AND A.status = 'PENDING' AND A.expiresAt > :now")
    Optional<PasswordResetRequestEntity> findByEmailAndCode(@Param("email") String email, @Param("code") String code, @Param("now") LocalDateTime now);

    // 사용자 ID로 요청 목록 조회
    List<PasswordResetRequestEntity> findByUserUserIdOrderByCreateDtDesc(Long userId);

    // 만료된 요청 조회
    @Query("SELECT A FROM PasswordResetRequestEntity A WHERE A.expiresAt < :now AND A.status = 'PENDING'")
    List<PasswordResetRequestEntity> findExpiredRequests(@Param("now") LocalDateTime now);

    // 특정 시간 이후 생성된 요청 수 조회
    @Query("SELECT COUNT(A) FROM PasswordResetRequestEntity A WHERE A.email = :email AND A.createDt > :since")
    Long countRecentRequestsByEmail(@Param("email") String email, @Param("since") LocalDateTime since);

    // 만료된 요청 삭제
    @Modifying
    @Query("DELETE FROM PasswordResetRequestEntity A WHERE A.expiresAt < :now AND A.status = 'PENDING'")
    int deleteExpiredRequests(@Param("now") LocalDateTime now);

    // 사용자별 모든 요청 삭제
    @Modifying
    @Query("DELETE FROM PasswordResetRequestEntity A WHERE A.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // 이메일별 모든 요청 삭제
    @Modifying
    @Query("DELETE FROM PasswordResetRequestEntity A WHERE A.email = :email")
    void deleteByEmail(@Param("email") String email);
}
