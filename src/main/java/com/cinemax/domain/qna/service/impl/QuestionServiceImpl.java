package com.cinemax.domain.qna.service.impl;

import com.cinemax.core.error.enums.ErrorCode;
import com.cinemax.core.exception.BusinessException;
import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.classes.repository.ClassEntityRepository;
import com.cinemax.domain.qna.dto.QnaEventMessage;
import com.cinemax.domain.qna.dto.QuestionRequest;
import com.cinemax.domain.qna.dto.QuestionResponse;
import com.cinemax.domain.qna.entity.Question;
import com.cinemax.domain.qna.mapper.QuestionMapper;
import com.cinemax.domain.qna.repository.QuestionRepository;
import com.cinemax.domain.qna.service.QuestionService;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.domain.weeklySession.repository.WeeklySessionRepository;
import com.cinemax.global.enums.QuestionStatus;
import com.cinemax.global.enums.QuestionUrgency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 질문 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private static final String NOT_FOUND_QUESTION = "질문을 찾을 수 없습니다. ";
    private static final String NOT_FOUND_USER = "사용자를 찾을 수 없습니다. ID: ";
    private static final String UNAUTHORIZED = "권한이 없습니다.";

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuestionMapper questionMapper;
    private final ClassEntityRepository classEntityRepository;
    private final WeeklySessionRepository weeklySessionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_USER + request.getUserId()));

        // 삭제된 수업인지 확인
        if (request.getClassId() != null) {
            ClassEntity classEntity = classEntityRepository.findById(request.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("수업을 찾을 수 없습니다. ID: " + request.getClassId()));

            if (!classEntity.getUseYn()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "삭제된 수업입니다. 질문을 작성할 수 없습니다.");
            }
        }

        // WeeklySession에서 weekNo 조회
        Integer weekNo = null;
        if (request.getWeeklySessionId() != null) {
            WeeklySession weeklySession = weeklySessionRepository.findById(request.getWeeklySessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("주차별 세션을 찾을 수 없습니다. ID: " + request.getWeeklySessionId()));
            weekNo = weeklySession.getWeekNo();
        }

        Question question = Question.create(
                request.getClassId(),
                request.getWeeklySessionId(),
                weekNo,
                user,
                request.getTitle(),
                request.getContent(),
                request.getUrgency(),
                request.getTags()
        );

        Question savedQuestion = questionRepository.save(question);
        publishQnaEvent(QnaEventMessage.Type.QUESTION_CREATED, savedQuestion.getQuestionId(),
                savedQuestion.getWeeklySessionId());
        return questionMapper.toDto(savedQuestion);
    }

    @Override
    public QuestionResponse getQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_QUESTION + questionId));

        return questionMapper.toDto(question);
    }

    @Override
    public List<QuestionResponse> getQuestionsByClassId(Long classId) {
        List<Question> questions = questionRepository.findByClassId(classId);
        return questions.stream()
                .map(questionMapper::toDtoWithoutAnswers)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getQuestionsByWeeklySession(Long weeklySessionId) {
        List<Question> questions = questionRepository.findByWeeklySessionWithAnswers(weeklySessionId);
        return questions.stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getQuestionsByUserId(Long userId) {
        // 학생 QnA 화면에서 교수 답변까지 보여야 하므로 답변 포함 조회
        List<Question> questions = questionRepository.findByUserIdWithAnswers(userId);
        return questions.stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getQuestionsByStatus(Long weeklySessionId, QuestionStatus status) {
        List<Question> questions = questionRepository.findByWeeklySessionAndStatus(weeklySessionId, status);
        return questions.stream()
                .map(questionMapper::toDtoWithoutAnswers)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getQuestionsByUrgency(Long weeklySessionId, QuestionUrgency urgency) {
        List<Question> questions = questionRepository.findByWeeklySessionAndUrgency(weeklySessionId, urgency);
        return questions.stream()
                .map(questionMapper::toDtoWithoutAnswers)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getUnansweredQuestions(Long weeklySessionId) {
        List<Question> questions = questionRepository.findUnansweredQuestions(weeklySessionId);
        return questions.stream()
                .map(questionMapper::toDtoWithoutAnswers)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getHighUrgencyQuestions(Long weeklySessionId) {
        List<Question> questions = questionRepository.findHighUrgencyQuestions(weeklySessionId);
        return questions.stream()
                .map(questionMapper::toDtoWithoutAnswers)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> searchQuestions(Long classId, String keyword) {
        List<Question> questionsByTitle = questionRepository.searchByTitle(classId, keyword);
        List<Question> questionsByContent = questionRepository.searchByContent(classId, keyword);

        // 중복 제거 및 합치기
        questionsByTitle.addAll(questionsByContent);
        return questionsByTitle.stream()
                .distinct()
                .map(questionMapper::toDtoWithoutAnswers)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> searchQuestionsAdvanced(Long classId, String keyword, String tag,
                                                         java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        List<Question> questions = questionRepository.searchQuestions(classId, keyword, tag, startDate, endDate);
        return questions.stream()
                .map(questionMapper::toDtoWithoutAnswers)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_QUESTION + questionId));

        // 작성자 본인만 수정 가능
        if (!question.getUser().getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException(UNAUTHORIZED);
        }

        question.updateInfo(request.getTitle(), request.getContent(), request.getUrgency(), request.getTags());

        publishQnaEvent(QnaEventMessage.Type.QUESTION_UPDATED, question.getQuestionId(), question.getWeeklySessionId());
        return questionMapper.toDto(question);
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestionStatus(Long questionId, QuestionStatus status) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_QUESTION + questionId));

        question.updateStatus(status);

        publishQnaEvent(QnaEventMessage.Type.QUESTION_STATUS_CHANGED, question.getQuestionId(), question.getWeeklySessionId());
        return questionMapper.toDto(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_QUESTION + questionId));

        // 작성자 본인만 삭제 가능
        if (!question.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException(UNAUTHORIZED);
        }

        Long weeklySessionId = question.getWeeklySessionId();
        questionRepository.delete(question);
        publishQnaEvent(QnaEventMessage.Type.QUESTION_DELETED, questionId, weeklySessionId);
    }

    @Override
    public Long countQuestionsByWeeklySession(Long weeklySessionId) {
        return questionRepository.countByWeeklySession(weeklySessionId);
    }

    @Override
    public Long countUnansweredQuestions(Long weeklySessionId) {
        return questionRepository.countUnansweredQuestions(weeklySessionId);
    }
    // QnA 변경 웹소켓 이벤트 발행 (세션에 속하지 않은 질문은 발행 생략)
    private void publishQnaEvent(QnaEventMessage.Type type, Long questionId, Long weeklySessionId) {
        if (weeklySessionId == null) {
            return;
        }
        try {
            messagingTemplate.convertAndSend("/topic/qna/" + weeklySessionId,
                    QnaEventMessage.builder()
                            .type(type)
                            .questionId(questionId)
                            .weeklySessionId(weeklySessionId)
                            .build());
        } catch (Exception e) {
            log.error("QnA 웹소켓 이벤트 발행 실패 - type: {}, questionId: {}", type, questionId, e);
        }
    }
}