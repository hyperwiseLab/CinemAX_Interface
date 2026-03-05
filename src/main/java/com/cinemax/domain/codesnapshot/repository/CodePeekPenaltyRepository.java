package com.cinemax.domain.codesnapshot.repository;

import com.cinemax.domain.codesnapshot.entity.CodePeekPenalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 코드 중간 검사 및 제제 비즈니스 레이어
 */
@Repository
public interface CodePeekPenaltyRepository extends JpaRepository<CodePeekPenalty, Long> {

    /**
     * 사용자 ID로 제재 목록 조회
     */
    @Query("SELECT cpp FROM CodePeekPenalty cpp WHERE cpp.userId = :userId")
    List<CodePeekPenalty> findByUserId(@Param("userId") Long userId);

    /**
     * 주차 세션 ID와 초대 ID로 제재 목록 조회
     */
    @Query("SELECT cpp FROM CodePeekPenalty cpp WHERE cpp.weeklySessionId = :weeklySessionId AND cpp.inviteId = :inviteId")
    List<CodePeekPenalty> findByWeeklySessionIdAndInviteId(@Param("weeklySessionId") Long weeklySessionId,
                                                             @Param("inviteId") Long inviteId);

    /**
     * 사용자와 세션, 초대 ID로 단건 조회
     */
    @Query("SELECT cpp FROM CodePeekPenalty cpp WHERE cpp.userId = :userId AND cpp.weeklySessionId = :weeklySessionId AND cpp.inviteId = :inviteId")
    java.util.Optional<CodePeekPenalty> findByUserIdAndWeeklySessionIdAndInviteId(@Param("userId") Long userId,
                                                                                  @Param("weeklySessionId") Long weeklySessionId,
                                                                                  @Param("inviteId") Long inviteId);

    /**
     * 주차 세션 ID와 초대 ID로 제재 점수가 높은 순으로 조회
     */
    @Query("SELECT cpp FROM CodePeekPenalty cpp WHERE cpp.weeklySessionId = :weeklySessionId AND cpp.inviteId = :inviteId ORDER BY cpp.penaltyPoints DESC, cpp.peekCount DESC")
    List<CodePeekPenalty> findByWeeklySessionIdAndInviteIdOrderByPenaltyPointsDesc(@Param("weeklySessionId") Long weeklySessionId,
                                                                                     @Param("inviteId") Long inviteId);

    /**
     * 제재 점수가 특정 값 이상인 사용자 조회
     */
    @Query("SELECT cpp FROM CodePeekPenalty cpp WHERE cpp.penaltyPoints >= :minPoints")
    List<CodePeekPenalty> findByPenaltyPointsGreaterThanEqual(@Param("minPoints") Integer minPoints);

    /**
     * 사용자 ID와 주차 세션으로 엿보기 횟수 조회
     */
    @Query("SELECT cpp.peekCount FROM CodePeekPenalty cpp WHERE cpp.userId = :userId AND cpp.weeklySessionId = :weeklySessionId AND cpp.inviteId = :inviteId")
    Integer findPeekCountByUserAndSession(@Param("userId") Long userId,
                                           @Param("weeklySessionId") Long weeklySessionId,
                                           @Param("inviteId") Long inviteId);
}
