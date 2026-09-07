package com.cinemax.domain.cbt.service;

import com.cinemax.domain.cbt.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CbtService {

    // ===== 관리자 =====

    // 과목 설정 전체 저장 (배점 합계 100 검증)
    CbtSubjectsResponse saveSubjects(Long classId, CbtSubjectSaveRequest request);

    // 과목 설정 조회
    CbtSubjectsResponse getSubjects(Long classId);

    // 문제 JSON 대량 등록
    CbtBulkResultResponse bulkCreateQuestions(CbtQuestionBulkRequest request, Long createdBy);

    // 문제 목록 (페이징/검색/과목·주차 필터)
    Page<CbtQuestionResponse> getQuestions(Long classId, Long subjectId, Integer weekNo,
                                           String keyword, Pageable pageable);

    // 주차별 CBT 설정 조회
    CbtWeekConfigResponse getWeekConfigs(Long classId);

    // 주차별 CBT 설정 저장
    CbtWeekConfigResponse saveWeekConfigs(Long classId, CbtWeekConfigSaveRequest request);

    // 문제 상세
    CbtQuestionResponse getQuestion(Long questionId);

    // 문제 수정
    CbtQuestionResponse updateQuestion(Long questionId, CbtQuestionUpdateRequest request);

    // 문제 삭제
    void deleteQuestion(Long questionId);

    // 반 응시 현황 (학생별 회차별)
    CbtClassAttemptsResponse getClassAttempts(Long classId);

    // ===== 학생 =====

    // 시험 구성 + 내 회차별 기록
    CbtInfoResponse getInfo(Long classId, Long userId);

    // 응시 문제 세트 (과목별 랜덤, 정답 숨김)
    CbtPracticeResponse getPracticeSet(Long classId);

    // 제출 -> 채점 -> 과락/합불 판정 -> 회차 기록
    CbtAttemptResultResponse submit(Long classId, Long userId, CbtSubmitRequest request);

    // 회차 상세 복기 (본인 것만, 관리자는 전체)
    CbtAttemptResultResponse getAttempt(Long attemptId, Long userId, boolean admin);

    // ===== 주차별 CBT =====

    // 주차별 응시 문제 세트 (해당 주차 문항만 랜덤, 정답 숨김)
    CbtPracticeResponse getWeekPracticeSet(Long classId, Integer weekNo);

    // 주차별 제출 -> 정답률 채점 -> 기록
    CbtWeekResultResponse submitWeek(Long classId, Long userId, Integer weekNo, CbtSubmitRequest request);
}
