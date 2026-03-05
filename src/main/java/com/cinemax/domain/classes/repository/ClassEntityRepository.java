package com.cinemax.domain.classes.repository;

import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {

    // 교수자(사용자) ID로 수업 조회
    @Query("SELECT A FROM ClassEntity A WHERE A.user.userId = :userId")
    List<ClassEntity> findByUserId(@Param("userId") Long userId);

    // 교수자(사용자) ID와 활성화 상태로 수업 조회
    List<ClassEntity> findByUserUserIdAndUseYn(Long userId, Boolean useYn);

    // 활성화된 수업 조회
    List<ClassEntity> findByUseYn(Boolean useYn);

    // 수업명으로 조회
    Optional<ClassEntity> findByClassNm(String classNm);

    // 교수자와 수업명으로 조회
    Optional<ClassEntity> findByUserAndClassNm(User user, String classNm);

    // 년도와 학기로 수업 조회
    List<ClassEntity> findByYearAndTerm(Integer year, String term);

    // 교수자, 년도, 학기로 수업 조회
    List<ClassEntity> findByUserAndYearAndTerm(User user, Integer year, String term);

    // 현재 초대 코드로 수업 조회
    @Query("SELECT A FROM ClassEntity A WHERE A.currentInviteId = :inviteId")
    Optional<ClassEntity> findByCurrentInviteId(@Param("inviteId") String inviteId);

    // 교수자 ID로 활성화된 수업 수 조회
    @Query("SELECT COUNT(A) FROM ClassEntity A WHERE A.user.userId = :userId AND A.useYn = true")
    Long countActiveClassesByUserId(@Param("userId") Long userId);

    // 특정 년도와 학기의 활성화된 수업 조회
    @Query("SELECT A FROM ClassEntity A WHERE A.year = :year AND A.term = :term AND A.useYn = true")
    List<ClassEntity> findActiveClassesByYearAndTerm(@Param("year") Integer year, @Param("term") String term);

    // 교수자 ID로 최근 생성된 수업 조회 (최대 5개)
    @Query("SELECT A FROM ClassEntity A WHERE A.user.userId = :userId ORDER BY A.createDt DESC")
    List<ClassEntity> findTop5ByUserIdOrderByCreateDtDesc(@Param("userId") Long userId);

    // 수업명에 특정 키워드가 포함된 수업 조회
    @Query("SELECT A FROM ClassEntity A WHERE A.classNm LIKE CONCAT('%', :keyword, '%') AND A.useYn = true")
    List<ClassEntity> findByClassNmContainingAndUseYnTrue(@Param("keyword") String keyword);

    // 활성화 여부별 수업 수 조회 (관리자 대시보드용)
    Long countByUseYn(Boolean useYn);

    // 교수자 ID와 활성화 상태로 수업 조회 (관리자용)
    @Query("SELECT c FROM ClassEntity c WHERE c.user.userId = :professorId AND c.useYn = :isActive")
    List<ClassEntity> findByProfessorUserIdAndIsActive(@Param("professorId") Long professorId, @Param("isActive") Boolean isActive);

    // 교수자 ID로 수업 조회 (관리자용)
    @Query("SELECT c FROM ClassEntity c WHERE c.user.userId = :professorId")
    List<ClassEntity> findByProfessorUserId(@Param("professorId") Long professorId);

    // 특정 커리큘럼으로 생성된 수업 개수 조회
    @Query("SELECT COUNT(c) FROM ClassEntity c WHERE c.curriculum.curId = :curId")
    Long countByCurriculumId(@Param("curId") Long curId);
}
