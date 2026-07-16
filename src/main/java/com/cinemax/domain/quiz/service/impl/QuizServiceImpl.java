package com.cinemax.domain.quiz.service.impl;

import com.cinemax.core.error.enums.ErrorCode;
import com.cinemax.core.exception.BusinessException;
import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.classes.entity.ClassEntity;
import com.cinemax.domain.classes.entity.ClassEnroll;
import com.cinemax.domain.classes.repository.ClassEntityRepository;
import com.cinemax.domain.classes.repository.ClassEnrollRepository;
import com.cinemax.domain.quiz.dto.GeneratedQuestion;
import com.cinemax.domain.quiz.dto.QuizClassResultResponse;
import com.cinemax.domain.quiz.dto.QuizGenerateRequest;
import com.cinemax.domain.quiz.dto.QuizPlayResponse;
import com.cinemax.domain.quiz.dto.QuizQuestionRequest;
import com.cinemax.domain.quiz.dto.QuizResponse;
import com.cinemax.domain.quiz.dto.QuizResultResponse;
import com.cinemax.domain.quiz.dto.QuizSubmitRequest;
import com.cinemax.domain.quiz.dto.QuizUpdateRequest;
import com.cinemax.domain.quiz.entity.Quiz;
import com.cinemax.domain.quiz.entity.QuizOption;
import com.cinemax.domain.quiz.entity.QuizQuestion;
import com.cinemax.domain.quiz.entity.QuizSubmission;
import com.cinemax.domain.quiz.mapper.QuizMapper;
import com.cinemax.domain.quiz.repository.QuizRepository;
import com.cinemax.domain.quiz.repository.QuizSubmissionRepository;
import com.cinemax.domain.quiz.service.QuizAiGenerator;
import com.cinemax.domain.quiz.service.QuizService;
import com.cinemax.domain.quiz.service.ScenarioAggregator;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.cinemax.domain.weeklySession.entity.WeeklySession;
import com.cinemax.domain.weeklySession.repository.WeeklySessionRepository;
import com.cinemax.global.enums.QuizQuestionType;
import com.cinemax.global.enums.QuizStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizServiceImpl implements QuizService {

    private static final String NOT_FOUND_QUIZ = "퀴즈를 찾을 수 없습니다. ID: ";
    // 문항당 1점 (만점 = 문항 수). 점수를 정답 개수로 읽히게 한다.
    private static final int DEFAULT_SCORE = 1;

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizMapper quizMapper;
    private final ClassEntityRepository classEntityRepository;
    private final ClassEnrollRepository classEnrollRepository;
    private final WeeklySessionRepository weeklySessionRepository;
    private final UserRepository userRepository;
    private final ScenarioAggregator scenarioAggregator;
    private final QuizAiGenerator quizAiGenerator;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public List<QuizResponse> generateQuizzes(QuizGenerateRequest request, Long createdBy) {
        Long classId = request.getClassId();

        // 반 → 선택한 커리큘럼(curId)
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("반을 찾을 수 없습니다. ID: " + classId));
        if (classEntity.getCurriculum() == null) {
            throw new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION,
                    "반에 연결된 커리큘럼이 없습니다.");
        }
        Long curId = classEntity.getCurriculum().getCurId();

        // 주차 번호 → weeklySessionId 매핑 (한 번에 조회)
        Map<Integer, Long> sessionByWeek = weeklySessionRepository.findAllByClassId(classId).stream()
                .collect(Collectors.toMap(
                        WeeklySession::getWeekNo,
                        WeeklySession::getWeeklySessionId,
                        (a, b) -> a));

        // 1) 주차 시나리오 aggregate (DB 조회 → 메인 스레드에서 순차, 빠름)
        Map<Integer, String> scenarioByWeek = new LinkedHashMap<>();
        for (QuizGenerateRequest.WeekSpec spec : request.getWeeks()) {
            scenarioByWeek.put(spec.getWeekNo(), scenarioAggregator.aggregate(curId, spec.getWeekNo()));
        }

        // 2) AI 생성은 주차별 병렬 호출 (느린 부분만 병렬화)
        ExecutorService executor = Executors.newFixedThreadPool(
                Math.min(request.getWeeks().size(), 12));
        Map<Integer, List<GeneratedQuestion>> generatedByWeek = new LinkedHashMap<>();
        try {
            Map<Integer, CompletableFuture<List<GeneratedQuestion>>> futures = new LinkedHashMap<>();
            for (QuizGenerateRequest.WeekSpec spec : request.getWeeks()) {
                Integer weekNo = spec.getWeekNo();
                String scenario = scenarioByWeek.get(weekNo);
                futures.put(weekNo, CompletableFuture.supplyAsync(
                        () -> quizAiGenerator.generate(scenario, spec.getMultipleCount(), spec.getOxCount()),
                        executor));
            }
            for (Map.Entry<Integer, CompletableFuture<List<GeneratedQuestion>>> e : futures.entrySet()) {
                try {
                    generatedByWeek.put(e.getKey(), e.getValue().join());
                } catch (Exception ex) {
                    log.error("[Quiz] {}주차 AI 생성 실패 - 건너뜀", e.getKey(), ex);
                    generatedByWeek.put(e.getKey(), List.of());
                }
            }
        } finally {
            executor.shutdown();
        }

        // 3) 결과를 엔티티로 조립 후 저장 (메인 스레드, 트랜잭션 안)
        List<Quiz> savedQuizzes = new ArrayList<>();
        for (QuizGenerateRequest.WeekSpec spec : request.getWeeks()) {
            Integer weekNo = spec.getWeekNo();
            List<GeneratedQuestion> generated = generatedByWeek.getOrDefault(weekNo, List.of());
            if (generated.isEmpty()) {
                continue;
            }

            Quiz quiz = Quiz.create(classId, weekNo, sessionByWeek.get(weekNo),
                    weekNo + "주차 퀴즈", createdBy);

            int order = 1;
            for (GeneratedQuestion g : generated) {
                QuizQuestion question = toQuestion(g, order++);
                if (question != null) {
                    quiz.addQuestion(question);
                }
            }

            if (!quiz.getQuestions().isEmpty()) {
                savedQuizzes.add(quizRepository.save(quiz));
            }
        }

        return savedQuizzes.stream().map(quizMapper::toDto).toList();
    }

    // AI가 생성한 문항 -> 엔티티 변환
    private QuizQuestion toQuestion(GeneratedQuestion g, int orderNo) {
        if (g.getType() == null) {
            return null;
        }
        if ("OX".equalsIgnoreCase(g.getType())) {
            String answer = normalizeOx(g.getAnswer());
            if (answer == null || g.getContent() == null) {
                return null;
            }
            return QuizQuestion.createOx(orderNo, g.getContent(), answer,
                    g.getExplanation(), DEFAULT_SCORE);
        }

        // MULTIPLE
        List<String> options = g.getOptions();
        Integer answerIndex = g.getAnswerIndex();
        if (options == null || options.isEmpty() || answerIndex == null
                || answerIndex < 0 || answerIndex >= options.size() || g.getContent() == null) {
            return null;
        }
        // 정답은 보기 순번(1-based) 문자열로 저장
        String answer = String.valueOf(answerIndex + 1);
        QuizQuestion question = QuizQuestion.createMultiple(orderNo, g.getContent(), answer,
                g.getExplanation(), DEFAULT_SCORE);
        int optOrder = 1;
        for (String optContent : options) {
            boolean correct = (optOrder - 1) == answerIndex;
            question.addOption(QuizOption.create(optOrder, optContent, correct));
            optOrder++;
        }
        return question;
    }

    private String normalizeOx(String raw) {
        if (raw == null) {
            return null;
        }
        String v = raw.trim().toUpperCase();
        if (v.startsWith("O") || v.equals("TRUE") || v.equals("참")) {
            return "O";
        }
        if (v.startsWith("X") || v.equals("FALSE") || v.equals("거짓")) {
            return "X";
        }
        return null;
    }

    @Override
    public List<QuizResponse> getQuizzesByClass(Long classId) {
        List<Quiz> quizzes = quizRepository.findByClassId(classId);
        quizzes.forEach(this::initQuizGraph);
        return quizzes.stream().map(quizMapper::toDto).toList();
    }

    @Override
    public QuizResponse getQuiz(Long quizId) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_QUIZ + quizId));
        initQuizGraph(quiz);
        return quizMapper.toDto(quiz);
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(Long quizId, QuizUpdateRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_QUIZ + quizId));

        // 확정(PUBLISHED)된 퀴즈는 수정 불가
        if (quiz.isPublished()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "이미 공개된 퀴즈는 수정할 수 없습니다.");
        }

        quiz.updateTitle(request.getTitle());

        // 문항/보기 통째 교체 (orphanRemoval 로 기존 문항·보기 삭제됨)
        List<QuizQuestion> newQuestions = new ArrayList<>();
        for (QuizQuestionRequest qReq : request.getQuestions()) {
            QuizQuestion question = buildQuestion(qReq);
            newQuestions.add(question);
        }
        quiz.replaceQuestions(newQuestions);

        return quizMapper.toDto(quiz);
    }

    @Override
    @Transactional
    public QuizResponse publishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_QUIZ + quizId));
        quiz.publish();
        return quizMapper.toDto(quiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_QUIZ + quizId));
        quizRepository.delete(quiz);
    }

    @Override
    public List<QuizPlayResponse> getPlayableQuizzes(Long classId, Integer weekNo) {
        List<Quiz> quizzes = quizRepository.findByClassIdAndWeekNoAndStatus(classId, weekNo, QuizStatus.PUBLISHED);
        quizzes.forEach(this::initQuizGraph);
        return quizzes.stream().map(this::toPlayDto).toList();
    }

    // lazy 컬렉션(문항/보기)을 트랜잭션 안에서 명시적으로 초기화한다.
    private void initQuizGraph(Quiz quiz) {
        quiz.getQuestions().forEach(q -> q.getOptions().size());
    }

    @Override
    @Transactional
    public QuizResultResponse submit(Long quizId, QuizSubmitRequest request, Long userId) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_QUIZ + quizId));

        if (!quiz.isPublished()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "아직 공개되지 않은 퀴즈입니다.");
        }

        // 제출 답안: questionId -> selected
        Map<Long, String> selectedByQuestion = new HashMap<>();
        for (QuizSubmitRequest.Answer a : request.getAnswers()) {
            selectedByQuestion.put(a.getQuestionId(), a.getSelected());
        }

        // 채점
        int totalScore = 0;
        int maxScore = 0;
        int correctCount = 0;
        List<QuizResultResponse.Item> items = new ArrayList<>();

        for (QuizQuestion question : quiz.getQuestions()) {
            maxScore += question.getScore();
            String selected = selectedByQuestion.get(question.getQuestionId());
            boolean correct = isCorrect(question, selected);
            if (correct) {
                totalScore += question.getScore();
                correctCount++;
            }
            items.add(QuizResultResponse.Item.builder()
                    .questionId(question.getQuestionId())
                    .selected(selected)
                    .answer(question.getAnswer())
                    .correct(correct)
                    .explanation(question.getExplanation())
                    .build());
        }

        // 최초 제출만 기록 (이후는 연습 응시, 성적 미반영)
        boolean firstAttempt = !quizSubmissionRepository.existsByQuizIdAndUserId(quizId, userId);
        if (firstAttempt) {
            String answersJson = writeAnswersJson(items);
            quizSubmissionRepository.save(QuizSubmission.create(
                    quizId, userId, quiz.getClassId(), totalScore, maxScore, answersJson));
        }

        return QuizResultResponse.builder()
                .quizId(quizId)
                .totalScore(totalScore)
                .maxScore(maxScore)
                .correctCount(correctCount)
                .questionCount(quiz.getQuestions().size())
                .firstAttempt(firstAttempt)
                .items(items)
                .build();
    }

    @Override
    public QuizResultResponse getMyResult(Long quizId, Long userId) {
        QuizSubmission submission = quizSubmissionRepository.findByQuizIdAndUserId(quizId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("제출 기록이 없습니다. quizId: " + quizId));

        List<QuizResultResponse.Item> items = readAnswersJson(submission.getAnswersJson());
        int correctCount = (int) items.stream().filter(i -> Boolean.TRUE.equals(i.getCorrect())).count();

        return QuizResultResponse.builder()
                .quizId(quizId)
                .totalScore(submission.getTotalScore())
                .maxScore(submission.getMaxScore())
                .correctCount(correctCount)
                .questionCount(items.size())
                .firstAttempt(true)
                .items(items)
                .build();
    }

    @Override
    public QuizClassResultResponse getClassResults(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_QUIZ + quizId));

        List<QuizSubmission> submissions = quizSubmissionRepository.findAllByQuizId(quizId);

        // 반 수강생 (분모)
        int totalStudents = classEnrollRepository.findActiveEnrollmentsByClassId(quiz.getClassId()).size();

        int maxScore = submissions.isEmpty() ? 0 : submissions.get(0).getMaxScore();
        double average = submissions.isEmpty() ? 0.0
                : submissions.stream().mapToInt(QuizSubmission::getTotalScore).average().orElse(0.0);

        // 이름 매핑
        List<Long> userIds = submissions.stream().map(QuizSubmission::getUserId).toList();
        Map<Long, String> nameByUser = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, User::getName, (a, b) -> a));

        // 순위 (findAllByQuizId 가 이미 점수 내림차순 정렬)
        List<QuizClassResultResponse.Ranking> rankings = new ArrayList<>();
        int rank = 1;
        for (QuizSubmission s : submissions) {
            rankings.add(QuizClassResultResponse.Ranking.builder()
                    .rank(rank++)
                    .userId(s.getUserId())
                    .userName(nameByUser.get(s.getUserId()))
                    .totalScore(s.getTotalScore())
                    .build());
        }

        return QuizClassResultResponse.builder()
                .quizId(quizId)
                .maxScore(maxScore)
                .submittedCount(submissions.size())
                .totalStudents(totalStudents)
                .averageScore(Math.round(average * 100) / 100.0)
                .rankings(rankings)
                .build();
    }

    // 문항 채점: OX는 O/X 비교, MULTIPLE은 정답 보기 순번(answer) 비교
    private boolean isCorrect(QuizQuestion question, String selected) {
        if (selected == null) {
            return false;
        }
        return question.getAnswer() != null
                && question.getAnswer().trim().equalsIgnoreCase(selected.trim());
    }

    // 학생 응시용 뷰 (정답/해설 제외)
    private QuizPlayResponse toPlayDto(Quiz quiz) {
        List<QuizPlayResponse.Question> questions = quiz.getQuestions().stream()
                .sorted((a, b) -> Integer.compare(a.getOrderNo(), b.getOrderNo()))
                .map(q -> QuizPlayResponse.Question.builder()
                        .questionId(q.getQuestionId())
                        .type(q.getType())
                        .orderNo(q.getOrderNo())
                        .content(q.getContent())
                        .score(q.getScore())
                        .options(q.getOptions().stream()
                                .sorted((a, b) -> Integer.compare(a.getOrderNo(), b.getOrderNo()))
                                .map(o -> QuizPlayResponse.Option.builder()
                                        .orderNo(o.getOrderNo())
                                        .content(o.getContent())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return QuizPlayResponse.builder()
                .quizId(quiz.getQuizId())
                .classId(quiz.getClassId())
                .weekNo(quiz.getWeekNo())
                .title(quiz.getTitle())
                .questions(questions)
                .build();
    }

    private String writeAnswersJson(List<QuizResultResponse.Item> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (Exception e) {
            log.warn("퀴즈 제출 답안 JSON 직렬화 실패", e);
            return "[]";
        }
    }

    private List<QuizResultResponse.Item> readAnswersJson(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, QuizResultResponse.Item.class));
        } catch (Exception e) {
            log.warn("퀴즈 제출 답안 JSON 역직렬화 실패", e);
            return new ArrayList<>();
        }
    }

    // 요청 DTO -> 문항 엔티티 (보기 포함) 조립
    private QuizQuestion buildQuestion(QuizQuestionRequest req) {
        QuizQuestion question = QuizQuestion.builder()
                .type(req.getType())
                .orderNo(req.getOrderNo())
                .content(req.getContent())
                .answer(req.getAnswer())
                .explanation(req.getExplanation())
                .score(req.getScore())
                .build();

        // 객관식만 보기 추가
        if (req.getType() == QuizQuestionType.MULTIPLE && req.getOptions() != null) {
            req.getOptions().forEach(optReq ->
                    question.addOption(QuizOption.create(
                            optReq.getOrderNo(), optReq.getContent(), optReq.getIsCorrect())));
        }
        return question;
    }
}
