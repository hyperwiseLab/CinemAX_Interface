package com.cinemax.domain.qna.service;

import com.cinemax.domain.qna.dto.AnswerRequest;
import com.cinemax.domain.qna.dto.AnswerResponse;

import java.util.List;

/**
 * 답변 서비스 인터페이스
 */
public interface AnswerService {

    /**
     * 답변 생성
     */
    AnswerResponse createAnswer(AnswerRequest request, Long userId);

    /**
     * 답변 조회 (단일)
     */
    AnswerResponse getAnswer(Long answerId, Long questionId);

    /**
     * 질문별 답변 목록 조회
     */
    List<AnswerResponse> getAnswersByQuestionId(Long questionId);

    /**
     * 사용자별 답변 목록 조회
     */
    List<AnswerResponse> getAnswersByUserId(Long userId);

    /**
     * 답변 수정
     */
    AnswerResponse updateAnswer(Long answerId, Long questionId, AnswerRequest request, Long userId);

    /**
     * 답변 삭제
     */
    void deleteAnswer(Long answerId, Long questionId, Long userId);

    /**
     * 질문별 답변 개수
     */
    Long countAnswersByQuestionId(Long questionId);
}
