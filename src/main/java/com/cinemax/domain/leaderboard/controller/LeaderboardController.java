package com.cinemax.domain.leaderboard.controller;

import com.cinemax.core.controller.BaseController;
import com.cinemax.core.dto.response.ApiResponse;
import com.cinemax.domain.leaderboard.dto.LeaderboardEntry;
import com.cinemax.domain.leaderboard.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Leaderboard 관리 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "리더보드 관리 API")
public class LeaderboardController extends BaseController {

    private final LeaderboardService leaderboardService;

 
    //수업별 리더보드 조회
    @GetMapping("/class/{classId}")
    @Operation(summary = "수업별 리더보드 조회", description = "특정 수업의 학생들 랭킹을 조회합니다.")
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getLeaderboardByClass(@Parameter(description = "수업 ID") @PathVariable Long classId) {

        List<LeaderboardEntry> leaderboard = leaderboardService.getLeaderboardByClassId(classId);

        return success(leaderboard, "리더보드 조회 성공");
    }

 
    //특정 사용자의 랭킹 조회
    @GetMapping("/class/{classId}/user/{userId}")
    @Operation(summary = "사용자 랭킹 조회", description = "특정 수업에서 사용자의 순위를 조회합니다.")
    public ResponseEntity<ApiResponse<LeaderboardEntry>> getUserRank(@Parameter(description = "수업 ID") @PathVariable Long classId,
                                                                     @Parameter(description = "사용자 ID") @PathVariable Long userId) {

        LeaderboardEntry entry = leaderboardService.getUserRank(classId, userId);

        return success(entry, "사용자 랭킹 조회 성공");
    }
}
