package com.cinemax.domain.brief.service;

import com.cinemax.domain.brief.dto.BriefRequest;
import com.cinemax.domain.brief.dto.BriefResponse;

import java.util.List;

/**
 * Brief 서비스 인터페이스
 */
public interface BriefService {

    // Brief 생성
    BriefResponse createBrief(BriefRequest request);

    // Brief 조회 (단일)
    BriefResponse getBrief(Long briefId, Long taskId);

    // 모든 Brief 조회
    List<BriefResponse> getAllBriefs();

    // Task ID로 Brief 조회
    List<BriefResponse> getBriefsByTaskId(Long taskId);

    // 제목으로 검색
    List<BriefResponse> searchBriefsByTitle(String keyword);

    // Brief 수정
    BriefResponse updateBrief(Long briefId, Long taskId, BriefRequest request);

    // Brief 삭제
    void deleteBrief(Long briefId, Long taskId);
}
