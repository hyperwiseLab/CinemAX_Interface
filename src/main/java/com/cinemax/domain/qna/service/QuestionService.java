package com.cinemax.domain.qna.service;

import com.cinemax.domain.qna.dto.QuestionRequest;
import com.cinemax.domain.qna.dto.QuestionResponse;
import com.cinemax.global.enums.QuestionStatus;
import com.cinemax.global.enums.QuestionUrgency;

import java.util.List;

/**
 * 질문 서비스 인터페이스
 */
public interface QuestionService {

    // 질문 생성
    QuestionResponse createQuestion(QuestionRequest request);

    // 질문 조회 (단일)
    QuestionResponse getQuestion(Long questionId);

    // 클래스별 질문 목록 조회
    List<QuestionResponse> getQuestionsByClassId(Long classId);

    // 주차별 세션의 질문 목록 조회
    List<QuestionResponse> getQuestionsByWeeklySession(Long weeklySessionId);

    // 사용자별 질문 목록 조회
    List<QuestionResponse> getQuestionsByUserId(Long userId);

    // 상태별 질문 목록 조회
    List<QuestionResponse> getQuestionsByStatus(Long weeklySessionId, QuestionStatus status);

    // 긴급도별 질문 목록 조회
    List<QuestionResponse> getQuestionsByUrgency(Long weeklySessionId, QuestionUrgency urgency);

    // 답변되지 않은 질문 목록 조회
    List<QuestionResponse> getUnansweredQuestions(Long weeklySessionId);

    // 높은 긴급도 질문 조회
    List<QuestionResponse> getHighUrgencyQuestions(Long weeklySessionId);

    // 질문 검색 (키워드만)
    List<QuestionResponse> searchQuestions(Long classId, String keyword);

    // 질문 통합 검색 (키워드, 태그, 기간)
    List<QuestionResponse> searchQuestionsAdvanced(Long classId, String keyword, String tag,
                                                   java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    // 질문 수정
    QuestionResponse updateQuestion(Long questionId, QuestionRequest request);

    // 질문 상태 변경
    QuestionResponse updateQuestionStatus(Long questionId, QuestionStatus status);

    // 질문 삭제
    void deleteQuestion(Long questionId, Long userId);

    // 주차별 세션의 질문 개수
    Long countQuestionsByWeeklySession(Long weeklySessionId);

    // 답변되지 않은 질문 개수
    Long countUnansweredQuestions(Long weeklySessionId);
}
