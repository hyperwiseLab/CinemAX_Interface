package com.cinemax.domain.codesnapshot.service;

import com.cinemax.domain.codesnapshot.dto.CodePeekPenaltyRequest;
import com.cinemax.domain.codesnapshot.dto.CodePeekPenaltyResponse;

import java.util.List;

/**
 * 코드 교수자 확인 및 제재 서비스 인터페이스
 */
public interface CodePeekPenaltyService {

    // 제재 기록 생성 또는 엿보기 횟수 증가
    CodePeekPenaltyResponse recordPeek(CodePeekPenaltyRequest request);

    // 제재 기록 조회
    CodePeekPenaltyResponse getPenalty(Long userId, Long weeklySessionId, Long inviteId);

    // 사용자 ID로 제재 목록 조회
    List<CodePeekPenaltyResponse> getPenaltiesByUserId(Long userId);

    // 주차 세션별 제재 목록 조회
    List<CodePeekPenaltyResponse> getPenaltiesByWeeklySession(Long weeklySessionId, Long inviteId);

    // 주차 세션별 제재 점수 상위 목록 조회
    List<CodePeekPenaltyResponse> getTopPenaltiesByWeeklySession(Long weeklySessionId, Long inviteId);

    // 제재 점수가 특정 값 이상인 사용자 조회
    List<CodePeekPenaltyResponse> getPenaltiesByMinPoints(Integer minPoints);

    // 제재 기록 초기화
    void resetPenalty(Long userId, Long weeklySessionId, Long inviteId);

    // 제재 기록 삭제
    void deletePenalty(Long userId, Long weeklySessionId, Long inviteId);
}
