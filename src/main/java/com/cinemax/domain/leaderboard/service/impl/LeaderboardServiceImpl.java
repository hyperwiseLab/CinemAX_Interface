package com.cinemax.domain.leaderboard.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.leaderboard.dto.LeaderboardEntry;
import com.cinemax.domain.leaderboard.repository.LeaderboardRepository;
import com.cinemax.domain.leaderboard.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Leaderboard 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderboardServiceImpl implements LeaderboardService {

    private static final String NOT_FOUND_USER = "사용자를 찾을 수 없습니다.";

    private final LeaderboardRepository leaderboardRepository;

    // 리더보드 조회
    @Override
    public List<LeaderboardEntry> getLeaderboardByClassId(Long classId) {

        List<Object[]> results = leaderboardRepository.findLeaderboardByClassId(classId);
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        int rank = 1;
        for (Object[] row : results) {
            LeaderboardEntry entry = LeaderboardEntry.builder()
                    .rank(rank++)
                    .userId(((Number) row[0]).longValue())
                    .userName((String) row[1])
                    .studentNum((String) row[2])
                    .totalScore((BigDecimal) row[3])
                    .submissionCount(((Number) row[4]).longValue())
                    .averageProgress((BigDecimal) row[5])
                    .completedTasks(((Number) row[6]).longValue())
                    .build();
            leaderboard.add(entry);
        }

        return leaderboard;
    }

    // 사용자 랭킹 조회
    @Override
    public LeaderboardEntry getUserRank(Long classId, Long userId) {

        // 사용자의 점수 정보 조회
        Object[] userResult = leaderboardRepository.findUserLeaderboardEntry(classId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_USER + " userId: " + userId));

        // 전체 리더보드에서 사용자의 순위 계산
        List<Object[]> allResults = leaderboardRepository.findLeaderboardByClassId(classId);
        BigDecimal userScore = (BigDecimal) userResult[3];

        int rank = 1;
        for (Object[] row : allResults) {
            Long currentUserId = ((Number) row[0]).longValue();
            if (currentUserId.equals(userId)) {
                break;
            }
            rank++;
        }

        LeaderboardEntry entry = LeaderboardEntry.builder()
                .rank(rank)
                .userId(((Number) userResult[0]).longValue())
                .userName((String) userResult[1])
                .studentNum((String) userResult[2])
                .totalScore((BigDecimal) userResult[3])
                .submissionCount(((Number) userResult[4]).longValue())
                .averageProgress((BigDecimal) userResult[5])
                .completedTasks(((Number) userResult[6]).longValue())
                .build();

        return entry;
    }
}
