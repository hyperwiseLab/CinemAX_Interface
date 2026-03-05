package com.cinemax.domain.classes.repository;

import com.cinemax.domain.classes.entity.ClassEnroll;
import com.cinemax.global.enums.EnrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassEnrollRepository extends JpaRepository<ClassEnroll, Long> {

    // 수업 ID로 수강 신청 조회
    List<ClassEnroll> findByClassEntityClassId(Long classId);

    // 사용자 ID로 수강 신청 조회
    @Query("SELECT A FROM ClassEnroll A WHERE A.user.userId = :userId")
    List<ClassEnroll> findByUserId(@Param("userId") Long userId);

    // 수업 ID와 사용자 ID로 수강 신청 조회
    Optional<ClassEnroll> findByClassEntityClassIdAndUserUserId(Long classId, Long userId);

    // 상태별 수강 신청 조회
    List<ClassEnroll> findByStatus(EnrollStatus status);

    // 수업 ID와 상태로 수강 신청 조회
    List<ClassEnroll> findByClassEntityClassIdAndStatus(Long classId, EnrollStatus status);

    // 사용자 ID와 상태로 수강 신청 조회
    List<ClassEnroll> findByUserUserIdAndStatus(Long userId, EnrollStatus status);

    // 활성화된 수강 신청 조회 (ACTIVE 상태)
    @Query("SELECT A FROM ClassEnroll A WHERE A.status = 'ACTIVE'")
    List<ClassEnroll> findActiveEnrollments();

    // 수업 ID로 활성화된 수강 신청 조회
    @Query("SELECT A FROM ClassEnroll A WHERE A.classEntity.classId = :classId AND A.status = 'ACTIVE'")
    List<ClassEnroll> findActiveEnrollmentsByClassId(@Param("classId") Long classId);

    // 사용자 ID로 활성화된 수강 신청 조회 (삭제되지 않은 수업만)
    @Query("SELECT A FROM ClassEnroll A JOIN FETCH A.classEntity c WHERE A.user.userId = :userId AND A.status = 'ACTIVE' AND c.useYn = true")
    List<ClassEnroll> findActiveEnrollmentsByUserId(@Param("userId") Long userId);

    // 사용자 ID로 삭제된 수업 조회 (읽기 전용)
    @Query("SELECT A FROM ClassEnroll A JOIN FETCH A.classEntity c WHERE A.user.userId = :userId AND c.useYn = false ORDER BY c.updateDt DESC")
    List<ClassEnroll> findDeletedClassesByUserId(@Param("userId") Long userId);

    // 수업 ID로 수강생 수 조회
    @Query("SELECT COUNT(A) FROM ClassEnroll A WHERE A.classEntity.classId = :classId AND A.status = 'ACTIVE'")
    Long countActiveEnrollmentsByClassId(@Param("classId") Long classId);

    // 사용자 ID로 수강 중인 수업 수 조회
    @Query("SELECT COUNT(A) FROM ClassEnroll A WHERE A.user.userId = :userId AND A.status = 'ACTIVE'")
    Long countActiveEnrollmentsByUserId(@Param("userId") Long userId);

    // 특정 수업에 사용자가 이미 수강 신청했는지 확인
    @Query("SELECT CASE WHEN COUNT(A) > 0 THEN true ELSE false END FROM ClassEnroll A WHERE A.classEntity.classId = :classId AND A.user.userId = :userId")
    boolean existsByClassIdAndUserId(@Param("classId") Long classId, @Param("userId") Long userId);

    // 특정 수업에 사용자가 활성화된 수강 신청이 있는지 확인
    @Query("SELECT CASE WHEN COUNT(A) > 0 THEN true ELSE false END FROM ClassEnroll A WHERE A.classEntity.classId = :classId AND A.user.userId = :userId AND A.status = 'ACTIVE'")
    boolean existsActiveByClassIdAndUserId(@Param("classId") Long classId, @Param("userId") Long userId);

    // 수업 ID로 수강 신청을 상태별로 그룹화하여 조회
    @Query("SELECT A.status, COUNT(A) FROM ClassEnroll A WHERE A.classEntity.classId = :classId GROUP BY A.status")
    List<Object[]> countEnrollmentsByStatusAndClassId(@Param("classId") Long classId);

    // 사용자 ID로 최근 수강 신청 조회 (최대 10개)
    @Query("SELECT A FROM ClassEnroll A WHERE A.user.userId = :userId ORDER BY A.createDt DESC")
    List<ClassEnroll> findTop10ByUserUserIdOrderByCreateDtDesc(@Param("userId") Long userId);
}
