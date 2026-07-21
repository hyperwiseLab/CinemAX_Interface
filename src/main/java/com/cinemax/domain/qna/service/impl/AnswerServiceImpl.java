package com.cinemax.domain.qna.service.impl;

import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.qna.dto.AnswerRequest;
import com.cinemax.domain.qna.dto.AnswerResponse;
import com.cinemax.domain.qna.entity.Answer;
import com.cinemax.domain.qna.entity.Question;
import com.cinemax.domain.qna.mapper.AnswerMapper;
import com.cinemax.domain.qna.repository.AnswerRepository;
import com.cinemax.domain.qna.repository.QuestionRepository;
import com.cinemax.domain.qna.service.AnswerService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 답변 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerServiceImpl implements AnswerService {

    private static final String NOT_FOUND_ANSWER = "답변을 찾을 수 없습니다. ";
    private static final String NOT_FOUND_USER = "사용자를 찾을 수 없습니다. ID: ";
    private static final String UNAUTHORIZED = "권한이 없습니다.";

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerMapper answerMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public AnswerResponse createAnswer(AnswerRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_USER + userId));

        Answer answer = Answer.create(
                request.getQuestionId(),
                user,
                request.getContent()
        );

        Answer savedAnswer = answerRepository.save(answer);

        // 질문 상태를 ANSWERED로 변경 (첫 번째 답변이면)
        Long answerCount = answerRepository.countByQuestionId(request.getQuestionId());
        if (answerCount == 1) {
            // 첫 번째 답변이므로 질문을 찾아서 상태 변경
            // questionId로 질문을 찾아야 하는데, 복합키여서 모든 키가 필요함
            // 여기서는 간단하게 처리하기 위해 답변이 달린 후 별도로 상태 업데이트할 수 있도록 함
        }

        publishQnaEvent("ANSWER_CREATED", request.getQuestionId());
        return answerMapper.toDto(savedAnswer);
    }

    @Override
    public AnswerResponse getAnswer(Long answerId, Long questionId) {
        Answer answer = answerRepository.findByAnswerIdAndQuestionId(answerId, questionId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ANSWER + "answerId: " + answerId + ", questionId: " + questionId));

        return answerMapper.toDto(answer);
    }

    @Override
    public List<AnswerResponse> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        return answerMapper.toDto(answers);
    }

    @Override
    public List<AnswerResponse> getAnswersByUserId(Long userId) {
        List<Answer> answers = answerRepository.findByUserId(userId);
        return answerMapper.toDto(answers);
    }

    @Override
    @Transactional
    public AnswerResponse updateAnswer(Long answerId, Long questionId, AnswerRequest request, Long userId) {
        Answer answer = answerRepository.findByAnswerIdAndQuestionId(answerId, questionId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ANSWER + "answerId: " + answerId + ", questionId: " + questionId));

        // 작성자 본인만 수정 가능
        if (!answer.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException(UNAUTHORIZED);
        }

        answer.updateContent(request.getContent());

        publishQnaEvent("ANSWER_UPDATED", questionId);
        return answerMapper.toDto(answer);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long answerId, Long questionId, Long userId) {
        Answer answer = answerRepository.findByAnswerIdAndQuestionId(answerId, questionId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_ANSWER + "answerId: " + answerId + ", questionId: " + questionId));

        // 작성자 본인만 삭제 가능
        if (!answer.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException(UNAUTHORIZED);
        }

        answerRepository.delete(answer);
        publishQnaEvent("ANSWER_DELETED", questionId);
    }

    @Override
    public Long countAnswersByQuestionId(Long questionId) {
        return answerRepository.countByQuestionId(questionId);
    }
    // QnA 답변 변경 웹소켓 이벤트 발행 (질문의 세션으로 라우팅, 세션 없으면 생략)
    private void publishQnaEvent(String type, Long questionId) {
        try {
            questionRepository.findById(questionId).ifPresent(question -> {
                if (question.getWeeklySessionId() == null) {
                    return;
                }
                messagingTemplate.convertAndSend("/topic/qna/" + question.getWeeklySessionId(),
                        com.cinemax.domain.qna.dto.QnaEventMessage.builder()
                                .type(com.cinemax.domain.qna.dto.QnaEventMessage.Type.valueOf(type))
                                .questionId(questionId)
                                .weeklySessionId(question.getWeeklySessionId())
                                .build());
            });
        } catch (Exception e) {
            log.error("QnA 답변 웹소켓 이벤트 발행 실패 - type: {}, questionId: {}", type, questionId, e);
        }
    }
}