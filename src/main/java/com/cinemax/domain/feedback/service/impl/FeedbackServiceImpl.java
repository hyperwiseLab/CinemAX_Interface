package com.cinemax.domain.feedback.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.feedback.dto.FeedbackRequest;
import com.cinemax.domain.feedback.dto.FeedbackResponse;
import com.cinemax.domain.feedback.entity.Feedback;
import com.cinemax.domain.feedback.repository.FeedbackRepository;
import com.cinemax.domain.feedback.service.FeedbackService;
import com.cinemax.global.enums.FeedbackType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 피드백 서비스 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    // 피드백 생성
    @Override
    @Transactional
    public FeedbackResponse createFeedback(FeedbackRequest request) {

        // Feedback 엔티티 생성
        Feedback feedback = Feedback.create(
                request.getCycleId(),
                request.getTaskId(),
                request.getFeedbackType(),
                request.getCharacterImg(),
                request.getCharacterPath(),
                request.getTitle(),
                request.getSubTitle(),
                request.getFeedbackContent()
        );

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return FeedbackResponse.from(savedFeedback);
    }

    // 피드백 조회
    @Override
    public FeedbackResponse getFeedback(Long feedbackId, Long taskId) {

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", feedbackId));

        return FeedbackResponse.from(feedback);
    }

    // 전체 피드백 조회
    @Override
    public List<FeedbackResponse> getAllFeedbacks() {

        return feedbackRepository.findAll().stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    // Task별 피드백 조회
    @Override
    public List<FeedbackResponse> getFeedbacksByTaskId(Long taskId) {

        return feedbackRepository.findByTaskId(taskId).stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    // FeedbackType별 피드백 조회
    @Override
    public List<FeedbackResponse> getFeedbacksByType(FeedbackType feedbackType) {

        return feedbackRepository.findByFeedbackType(feedbackType).stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    // Curriculum별 피드백 조회
    @Override
    public List<FeedbackResponse> getFeedbacksByCurId(Long curId) {
        // curId field removed; return all for backward compatibility
        return feedbackRepository.findAll().stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    // Curriculum과 주차별 피드백 조회
    @Override
    public List<FeedbackResponse> getFeedbacksByCurIdAndWeekNo(Long curId, Integer weekNo) {
        // weekNo/curId fields removed; return all for backward compatibility
        return feedbackRepository.findAll().stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    // 대체 조회: Task + Type
    @Override
    public List<FeedbackResponse> getFeedbacksByTaskAndType(Long taskId, FeedbackType type) {
        return feedbackRepository.findByTaskIdAndType(taskId, type).stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    // 대체 조회: Task + 기간
    @Override
    public List<FeedbackResponse> getFeedbacksByTaskAndDateRange(Long taskId, LocalDateTime start, LocalDateTime end) {
        return feedbackRepository.findByTaskIdAndCreateDtBetween(taskId, start, end).stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    // 대체 조회: 전체 기간
    @Override
    public List<FeedbackResponse> getFeedbacksByDateRange(LocalDateTime start, LocalDateTime end) {
        return feedbackRepository.findByCreateDtBetween(start, end).stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    // 피드백 수정
    @Override
    @Transactional
    public FeedbackResponse updateFeedback(Long feedbackId, Long taskId, FeedbackRequest request) {

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", feedbackId));

        // 엔티티 업데이트
        feedback.update(
                request.getFeedbackType(),
                request.getCharacterImg(),
                request.getCharacterPath(),
                request.getTitle(),
                request.getSubTitle(),
                request.getFeedbackContent()
        );

        return FeedbackResponse.from(feedback);
    }

    // 피드백 삭제
    @Override
    @Transactional
    public void deleteFeedback(Long feedbackId, Long taskId) {

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", feedbackId));

        feedbackRepository.delete(feedback);
    }
}
