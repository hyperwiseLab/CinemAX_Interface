package com.cinemax.domain.assign.service;

import com.cinemax.domain.assign.dto.AssignRequest;
import com.cinemax.domain.assign.dto.AssignResponse;

import java.util.List;

/**
 * Assign 서비스 인터페이스
 */
public interface AssignService {

    // Assign 생성
    AssignResponse createAssign(AssignRequest request);

    // Assign 조회 (단일)
    AssignResponse getAssign(Long assignId, Long taskId);

    // 모든 Assign 조회
    List<AssignResponse> getAllAssigns();

    // Task ID로 Assign 조회
    List<AssignResponse> getAssignsByTaskId(Long taskId);

    // 제목으로 검색
    List<AssignResponse> searchAssignsByTitle(String keyword);

    // Assign 수정
    AssignResponse updateAssign(Long assignId, Long taskId, AssignRequest request);

    // Assign 삭제
    void deleteAssign(Long assignId, Long taskId);
}
