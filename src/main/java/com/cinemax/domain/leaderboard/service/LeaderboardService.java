package com.cinemax.domain.leaderboard.service;

import com.cinemax.domain.leaderboard.dto.LeaderboardEntry;

import java.util.List;

/**
 * Leaderboard 서비스 인터페이스
 */
public interface LeaderboardService {

    //수업별 리더보드 조회
    List<LeaderboardEntry> getLeaderboardByClassId(Long classId);


    //특정 사용자의 랭킹 조회
    LeaderboardEntry getUserRank(Long classId, Long userId);
}
