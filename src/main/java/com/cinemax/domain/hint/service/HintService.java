package com.cinemax.domain.hint.service;

import com.cinemax.domain.hint.dto.HintRequest;
import com.cinemax.domain.hint.dto.HintResponse;

import java.util.List;

/**
 * Hint 서비스 인터페이스
 */
public interface HintService {

    /**
     * Hint 생성
     */
    HintResponse createHint(HintRequest request);

    /**
     * Hint 조회 (단일)
     */
    HintResponse getHint(Long hintId, Long taskId);

    /**
     * 모든 Hint 조회
     */
    List<HintResponse> getAllHints();

    /**
     * Task ID로 Hint 조회
     */
    List<HintResponse> getHintsByTaskId(Long taskId);

    /**
     * 제목으로 검색
     */
    List<HintResponse> searchHintsByTitle(String keyword);

    /**
     * Hint 수정
     */
    HintResponse updateHint(Long hintId, Long taskId, HintRequest request);

    /**
     * Hint 삭제
     */
    void deleteHint(Long hintId, Long taskId);
}
