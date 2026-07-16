package com.cinemax.domain.quiz.service;

import com.cinemax.domain.quiz.dto.QuizClassResultResponse;
import com.cinemax.domain.quiz.dto.QuizGenerateRequest;
import com.cinemax.domain.quiz.dto.QuizPlayResponse;
import com.cinemax.domain.quiz.dto.QuizResponse;
import com.cinemax.domain.quiz.dto.QuizResultResponse;
import com.cinemax.domain.quiz.dto.QuizSubmitRequest;
import com.cinemax.domain.quiz.dto.QuizUpdateRequest;

import java.util.List;

public interface QuizService {

    // 반 생성 시: 선택한 커리큘럼 시나리오로 주차별 퀴즈를 AI 생성(DRAFT). 생성된 퀴즈 목록 반환.
    List<QuizResponse> generateQuizzes(QuizGenerateRequest request, Long createdBy);

    // 반의 전체 퀴즈 목록 (관리 화면)
    List<QuizResponse> getQuizzesByClass(Long classId);

    // 학생 응시 진입: 반+주차의 공개(PUBLISHED) 퀴즈 (정답 숨김)
    List<QuizPlayResponse> getPlayableQuizzes(Long classId, Integer weekNo);

    // 학생 답안 제출 → 자동채점 → 결과. 최초 제출만 기록.
    QuizResultResponse submit(Long quizId, QuizSubmitRequest request, Long userId);

    // 본인 결과 조회
    QuizResultResponse getMyResult(Long quizId, Long userId);

    // 특정 학생의 결과 상세 조회 (관리자/교수) — 문항별 답변까지
    QuizResultResponse getStudentResult(Long quizId, Long userId);

    // 반 전체 결과 집계 (평균·랭킹) — 관리자
    QuizClassResultResponse getClassResults(Long quizId);

    // 퀴즈 상세 (문항+보기 포함)
    QuizResponse getQuiz(Long quizId);

    // 퀴즈 전체 수정 (문항/보기/정답 통째 교체). PUBLISHED면 거부.
    QuizResponse updateQuiz(Long quizId, QuizUpdateRequest request);

    // 확정(공개). PUBLISHED로 전환.
    QuizResponse publishQuiz(Long quizId);

    // 삭제
    void deleteQuiz(Long quizId);
}
