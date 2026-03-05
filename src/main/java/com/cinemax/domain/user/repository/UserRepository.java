package com.cinemax.domain.user.repository;

import com.cinemax.domain.user.entity.User;
import com.cinemax.global.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * User JPA Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

    // 학번으로 사용자 조회
    Optional<User> findByStudentNum(String studentNum);

    // 학번 중복 체크
    boolean existsByStudentNum(String studentNum);

    // 역할별 사용자 수 조회 (관리자 대시보드용)
    Long countByRole(RoleType role);

    // 상태별 사용자 수 조회 (활성 사용자)
    Long countByStatus(Integer status);

    // 특정 날짜 이후 생성된 사용자 수 조회 (신규 가입자)
    @Query("SELECT COUNT(u) FROM User u WHERE u.createDt >= :createDt")
    Long countByCreateDtAfter(@Param("createDt") LocalDateTime createDt);

    // 역할과 활성화 상태로 사용자 조회 (관리자용)
    java.util.List<User> findByRoleAndStatus(RoleType role, Byte status);

    // 역할로 사용자 조회 (관리자용)
    java.util.List<User> findByRole(RoleType role);

    // 활성화 상태로 사용자 조회 (관리자용)
    java.util.List<User> findByStatus(Byte status);
}
