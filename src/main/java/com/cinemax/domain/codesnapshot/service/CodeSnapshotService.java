package com.cinemax.domain.codesnapshot.service;

import com.cinemax.domain.codesnapshot.dto.CodeSaveRequest;
import com.cinemax.domain.codesnapshot.dto.CodeSnapshotResponse;

import java.util.List;

/**
 * 코드 스냅샷 서비스 인터페이스
 * 코드 자동저장 및 불러오기 기능 제공
 */
public interface CodeSnapshotService {

    // 코드 저장 (자동저장) -> 기존 코드가 있으면 업데이트, 없으면 신규 생성
    CodeSnapshotResponse saveCode(CodeSaveRequest request, Long userId);

    // 최신 코드 불러오기
    CodeSnapshotResponse getLatestCode(Long weeklySessionId, Long userId);

    // 코드 스냅샷 이력 조회
    List<CodeSnapshotResponse> getCodeHistory(Long taskId, Long userId, Long weeklySessionId, Long cycleId);

    // 코드 삭제
    void deleteCode(Long taskId, Long userId, Long weeklySessionId, Long cycleId);

    // 실시간 모니터링용: 주차별 수업의 모든 학생 코드 조회
    List<CodeSnapshotResponse> getAllStudentCodes(Long weeklySessionId, Long taskId);

    // 실시간 모니터링용: 특정 학생 코드 조회 (userId 필터링)
    List<CodeSnapshotResponse> getStudentCodesByUserId(Long weeklySessionId, Long taskId, Long userId);

    // 저장된 코드 존재 여부 확인
    boolean hasCode(Long taskId, Long userId, Long weeklySessionId, Long cycleId);
}
