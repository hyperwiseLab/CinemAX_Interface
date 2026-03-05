package com.cinemax.domain.classes.repository;

import com.cinemax.domain.classes.entity.ClassInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassInviteRepository extends JpaRepository<ClassInvite, Long> {

    // 수업 ID로 초대 조회
    List<ClassInvite> findByClassEntityClassId(Long classId);

    //  수업 ID와 활성화 상태로 초대 조회
    List<ClassInvite> findByClassEntityClassIdAndActiveYn(Long classId, Boolean activeYn);

    // 초대 코드로 조회
    Optional<ClassInvite> findByInviteCd(String inviteCd);

    // 활성화된 초대 코드로 조회
    @Query("SELECT A FROM ClassInvite A WHERE A.inviteCd = :inviteCd AND A.activeYn = true")
    Optional<ClassInvite> findActiveByInviteCd(@Param("inviteCd") String inviteCd);

    // QR 코드로 조회
    Optional<ClassInvite> findByQrCd(String qrCd);

    // 활성화된 QR 코드로 조회
    @Query("SELECT A FROM ClassInvite A WHERE A.qrCd = :qrCd AND A.activeYn = true")
    Optional<ClassInvite> findActiveByQrCd(@Param("qrCd") String qrCd);

    // 수업 ID로 활성화된 초대 조회
    @Query("SELECT A FROM ClassInvite A WHERE A.classEntity.classId = :classId AND A.activeYn = true")
    List<ClassInvite> findActiveByClassId(@Param("classId") Long classId);

    // 수업 ID로 현재 활성화된 초대 조회 (하나만)
    @Query("SELECT A FROM ClassInvite A WHERE A.classEntity.classId = :classId AND A.activeYn = true ORDER BY A.createDt DESC")
    Optional<ClassInvite> findCurrentActiveByClassId(@Param("classId") Long classId);

    // 수업 ID로 초대 수 조회
    @Query("SELECT COUNT(A) FROM ClassInvite A WHERE A.classEntity.classId = :classId")
    Long countByClassId(@Param("classId") Long classId);

    // 수업 ID로 활성화된 초대 수 조회
    @Query("SELECT COUNT(A) FROM ClassInvite A WHERE A.classEntity.classId = :classId AND A.activeYn = true")
    Long countActiveByClassId(@Param("classId") Long classId);

    // 특정 수업의 모든 초대를 비활성화
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ClassInvite A SET A.activeYn = false WHERE A.classEntity.classId = :classId")
    void deactivateAllByClassId(@Param("classId") Long classId);

    // 특정 초대를 제외하고 나머지 초대들을 비활성화
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ClassInvite A SET A.activeYn = false WHERE A.classEntity.classId = :classId AND A.inviteId != :excludeInviteId")
    void deactivateOthersByClassId(@Param("classId") Long classId, @Param("excludeInviteId") Long excludeInviteId);

    // 여러 수업 ID로 활성화된 초대 조회
    @Query("SELECT A FROM ClassInvite A WHERE A.classEntity.classId IN :classIds AND A.activeYn = true")
    List<ClassInvite> findActiveByClassIds(@Param("classIds") List<Long> classIds);
}
