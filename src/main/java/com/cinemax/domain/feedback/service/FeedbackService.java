package com.cinemax.domain.feedback.service;

import com.cinemax.domain.feedback.dto.FeedbackRequest;
import com.cinemax.domain.feedback.dto.FeedbackResponse;
import com.cinemax.global.enums.FeedbackType;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 피드백 서비스 인터페이스
 */
public interface FeedbackService {

    // 피드백 생성
    FeedbackResponse createFeedback(FeedbackRequest request);

    // 피드백 조회
    FeedbackResponse getFeedback(Long feedbackId, Long taskId);

    // 전체 피드백 조회
    List<FeedbackResponse> getAllFeedbacks();

    // Task별 피드백 조회
    List<FeedbackResponse> getFeedbacksByTaskId(Long taskId);

    // FeedbackType별 피드백 조회
    List<FeedbackResponse> getFeedbacksByType(FeedbackType feedbackType);

    // Curriculum별 피드백 조회
    List<FeedbackResponse> getFeedbacksByCurId(Long curId);

    // Curriculum과 주차별 피드백 조회
    List<FeedbackResponse> getFeedbacksByCurIdAndWeekNo(Long curId, Integer weekNo);

    // 대체 조회: Task + Type
    List<FeedbackResponse> getFeedbacksByTaskAndType(Long taskId, FeedbackType type);

    // 대체 조회: Task + 기간
    List<FeedbackResponse> getFeedbacksByTaskAndDateRange(Long taskId, LocalDateTime start, LocalDateTime end);

    // 대체 조회: 전체 기간
    List<FeedbackResponse> getFeedbacksByDateRange(LocalDateTime start, LocalDateTime end);

    // 피드백 수정
    FeedbackResponse updateFeedback(Long feedbackId, Long taskId, FeedbackRequest request);

    // 피드백 삭제
    void deleteFeedback(Long feedbackId, Long taskId);
}
