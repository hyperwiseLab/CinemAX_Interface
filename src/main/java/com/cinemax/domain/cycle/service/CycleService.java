package com.cinemax.domain.cycle.service;

import com.cinemax.domain.cycle.dto.CycleRequest;
import com.cinemax.domain.cycle.dto.CycleResponse;

import java.util.List;

/**
 * Cycle Service Interface
 */
public interface CycleService {

    // Cycle 생성
    CycleResponse createCycle(CycleRequest request);

    // Cycle 수정
    CycleResponse updateCycle(Long cycleId, CycleRequest request);

    // Cycle 삭제
    void deleteCycle(Long cycleId);

    // Cycle 단건 조회 (Task 목록 포함)
    CycleResponse getCycleById(Long cycleId);

    // 커리큘럼 주차별 Cycle 목록 조회 (Task 목록 포함)
    List<CycleResponse> getCyclesByWeek(Long curWeekId);
}
