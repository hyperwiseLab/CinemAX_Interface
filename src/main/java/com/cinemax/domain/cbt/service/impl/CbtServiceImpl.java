package com.cinemax.domain.cbt.service.impl;

import com.cinemax.core.error.enums.ErrorCode;
import com.cinemax.core.exception.BusinessException;
import com.cinemax.core.exception.ResourceNotFoundException;
import com.cinemax.domain.cbt.dto.*;
import com.cinemax.domain.cbt.entity.*;
import com.cinemax.domain.cbt.repository.*;
import com.cinemax.domain.cbt.service.CbtService;
import com.cinemax.domain.classes.repository.ClassEntityRepository;
import com.cinemax.domain.curriculum.entity.CurriculumWeek;
import com.cinemax.domain.curriculum.repository.CurriculumWeekRepository;
import com.cinemax.domain.user.entity.User;
import com.cinemax.domain.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CbtServiceImpl implements CbtService {

    private static final double TOTAL_SCORE_REQUIRED = 100.0;
    private static final double SCORE_EPSILON = 0.001;

    private final CbtConfigRepository configRepository;
    private final CbtSubjectRepository subjectRepository;
    private final CbtQuestionRepository questionRepository;
    private final CbtAttemptRepository attemptRepository;
    private final CbtWeekConfigRepository weekConfigRepository;
    private final ClassEntityRepository classEntityRepository;
    private final CurriculumWeekRepository curriculumWeekRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // ===== 관리자: 과목 설정 =====

    @Override
    @Transactional
    public CbtSubjectsResponse saveSubjects(Long classId, CbtSubjectSaveRequest request) {
        validateSubjectRequest(request);

        Map<Long, CbtSubject> existing = subjectRepository.findByClassIdOrderByOrderNoAsc(classId).stream()
                .collect(Collectors.toMap(CbtSubject::getSubjectId, Function.identity()));

        Set<Long> keepIds = new HashSet<>();
        for (CbtSubjectSaveRequest.Item item : request.getSubjects()) {
            if (item.getSubjectId() != null && existing.containsKey(item.getSubjectId())) {
                CbtSubject subject = existing.get(item.getSubjectId());
                subject.updateSettings(item.getSubjectNm(), item.getOrderNo(), item.getPointPerQuestion(),
                        item.getQuestionCount(), item.getCutScore(), item.getCutYn(), item.getUseYn());
                keepIds.add(subject.getSubjectId());
            } else {
                CbtSubject saved = subjectRepository.save(CbtSubject.builder()
                        .classId(classId)
                        .subjectNm(item.getSubjectNm())
                        .orderNo(item.getOrderNo())
                        .pointPerQuestion(item.getPointPerQuestion())
                        .questionCount(item.getQuestionCount())
                        .cutScore(item.getCutScore())
                        .cutYn(item.getCutYn())
                        .useYn(item.getUseYn())
                        .build());
                keepIds.add(saved.getSubjectId());
            }
        }

        // 요청에서 빠진 기존 과목 삭제 (문제가 남아있으면 차단)
        for (CbtSubject subject : existing.values()) {
            if (!keepIds.contains(subject.getSubjectId())) {
                if (questionRepository.countBySubjectId(subject.getSubjectId()) > 0) {
                    throw new BusinessException(ErrorCode.CBT_SUBJECT_HAS_QUESTIONS,
                            "문제가 등록된 과목은 삭제할 수 없습니다: " + subject.getSubjectNm());
                }
                subjectRepository.delete(subject);
            }
        }

        // 합격점수 upsert
        configRepository.findByClassId(classId)
                .ifPresentOrElse(
                        config -> config.updatePassScore(request.getPassScore()),
                        () -> configRepository.save(CbtConfig.create(classId, request.getPassScore())));

        return getSubjects(classId);
    }

    // 배점 합계 100 / 과락점수 유효성 검증
    private void validateSubjectRequest(CbtSubjectSaveRequest request) {
        double total = 0;
        for (CbtSubjectSaveRequest.Item item : request.getSubjects()) {
            if (!Boolean.TRUE.equals(item.getUseYn())) {
                continue;
            }
            double maxScore = item.getPointPerQuestion() * item.getQuestionCount();
            if (item.getCutScore() > maxScore + SCORE_EPSILON) {
                throw new BusinessException(ErrorCode.CBT_CUT_SCORE_INVALID,
                        String.format("과락 점수(%s)가 과목 배점(%s)을 초과합니다: %s",
                                item.getCutScore(), round2(maxScore), item.getSubjectNm()));
            }
            total += maxScore;
        }
        if (Math.abs(total - TOTAL_SCORE_REQUIRED) > SCORE_EPSILON) {
            throw new BusinessException(ErrorCode.CBT_SUBJECT_TOTAL_INVALID,
                    String.format("활성 과목 배점 합계가 %s점입니다. 정확히 100점이어야 저장할 수 있습니다.", round2(total)));
        }
    }

    @Override
    public CbtSubjectsResponse getSubjects(Long classId) {
        List<CbtSubject> subjects = subjectRepository.findByClassIdOrderByOrderNoAsc(classId);
        Optional<CbtConfig> config = configRepository.findByClassId(classId);

        double total = subjects.stream()
                .filter(s -> Boolean.TRUE.equals(s.getUseYn()))
                .mapToDouble(CbtSubject::maxScore)
                .sum();

        return CbtSubjectsResponse.builder()
                .configured(config.isPresent() && !subjects.isEmpty())
                .passScore(config.map(CbtConfig::getPassScore).orElse(null))
                .totalMaxScore(round2(total))
                .subjects(subjects.stream().map(this::toSubjectDto).collect(Collectors.toList()))
                .build();
    }

    private CbtSubjectResponse toSubjectDto(CbtSubject subject) {
        return CbtSubjectResponse.builder()
                .subjectId(subject.getSubjectId())
                .subjectNm(subject.getSubjectNm())
                .orderNo(subject.getOrderNo())
                .pointPerQuestion(subject.getPointPerQuestion())
                .questionCount(subject.getQuestionCount())
                .maxScore(round2(subject.maxScore()))
                .cutScore(subject.getCutScore())
                .cutYn(subject.getCutYn())
                .useYn(subject.getUseYn())
                .bankCount(questionRepository.countByClassIdAndSubjectIdAndUseYnTrue(
                        subject.getClassId(), subject.getSubjectId()))
                .build();
    }

    // ===== 관리자: 문제 은행 =====

    @Override
    @Transactional
    public CbtBulkResultResponse bulkCreateQuestions(CbtQuestionBulkRequest request, Long createdBy) {
        Long classId = request.getClassId();

        // 과목명 -> 과목 (미등록이면 자동 생성)
        Map<String, CbtSubject> subjectByName = subjectRepository.findByClassIdOrderByOrderNoAsc(classId).stream()
                .collect(Collectors.toMap(CbtSubject::getSubjectNm, Function.identity(), (a, b) -> a));

        List<CbtBulkResultResponse.Failure> failures = new ArrayList<>();
        Map<String, Integer> subjectCounts = new LinkedHashMap<>();
        int success = 0;

        List<CbtQuestionBulkRequest.QuestionItem> items = request.getQuestions();
        for (int i = 0; i < items.size(); i++) {
            CbtQuestionBulkRequest.QuestionItem item = items.get(i);
            String error = validateQuestionOptions(item.getOptions());
            if (error != null) {
                failures.add(new CbtBulkResultResponse.Failure(i, error));
                continue;
            }

            String subjectNm = item.getSubject().trim();
            CbtSubject subject = subjectByName.computeIfAbsent(subjectNm,
                    name -> subjectRepository.save(
                            CbtSubject.createDefault(classId, name, subjectByName.size() + 1)));

            CbtQuestion question = CbtQuestion.create(classId, subject.getSubjectId(), item.getWeekNo(),
                    item.getContent(), item.getExplanation(), createdBy);
            for (CbtQuestionBulkRequest.OptionItem option : item.getOptions()) {
                question.addOption(CbtQuestionOption.create(option.getOrderNo(), option.getContent(), option.getCorrect()));
            }
            questionRepository.save(question);
            success++;
            subjectCounts.merge(subjectNm, 1, Integer::sum);
        }

        return CbtBulkResultResponse.builder()
                .totalCount(items.size())
                .successCount(success)
                .failCount(failures.size())
                .failures(failures)
                .subjectCounts(subjectCounts)
                .build();
    }

    // 보기 검증: 2개 이상 / 정답 정확히 1개 / orderNo 중복 없음. 통과 시 null 반환.
    private String validateQuestionOptions(List<CbtQuestionBulkRequest.OptionItem> options) {
        if (options == null || options.size() < 2) {
            return "보기는 2개 이상이어야 합니다";
        }
        long correctCount = options.stream().filter(o -> Boolean.TRUE.equals(o.getCorrect())).count();
        if (correctCount != 1) {
            return "정답은 정확히 1개여야 합니다 (현재 " + correctCount + "개)";
        }
        long distinctOrders = options.stream().map(CbtQuestionBulkRequest.OptionItem::getOrderNo).distinct().count();
        if (distinctOrders != options.size()) {
            return "보기 orderNo가 중복되었습니다";
        }
        return null;
    }

    @Override
    public Page<CbtQuestionResponse> getQuestions(Long classId, Long subjectId, Integer weekNo,
                                                 String keyword, Pageable pageable) {
        Map<Long, String> subjectNames = subjectNameMap(classId);
        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return questionRepository.search(classId, subjectId, weekNo, normalizedKeyword, pageable)
                .map(q -> toQuestionDto(q, subjectNames));
    }

    @Override
    public CbtQuestionResponse getQuestion(Long questionId) {
        CbtQuestion question = questionRepository.findByIdWithOptions(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "문제를 찾을 수 없습니다: " + questionId));
        return toQuestionDto(question, subjectNameMap(question.getClassId()));
    }

    @Override
    @Transactional
    public CbtQuestionResponse updateQuestion(Long questionId, CbtQuestionUpdateRequest request) {
        CbtQuestion question = questionRepository.findByIdWithOptions(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "문제를 찾을 수 없습니다: " + questionId));

        String error = validateQuestionOptions(request.getOptions());
        if (error != null) {
            throw new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION, error);
        }
        subjectRepository.findById(request.getSubjectId())
                .filter(s -> s.getClassId().equals(question.getClassId()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "과목을 찾을 수 없습니다: " + request.getSubjectId()));

        question.updateInfo(request.getSubjectId(), request.getWeekNo(), request.getContent(),
                request.getExplanation(), request.getUseYn());
        question.replaceOptions(request.getOptions().stream()
                .map(o -> CbtQuestionOption.create(o.getOrderNo(), o.getContent(), o.getCorrect()))
                .collect(Collectors.toList()));

        return toQuestionDto(question, subjectNameMap(question.getClassId()));
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        CbtQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "문제를 찾을 수 없습니다: " + questionId));
        questionRepository.delete(question);
    }

    private Map<Long, String> subjectNameMap(Long classId) {
        return subjectRepository.findByClassIdOrderByOrderNoAsc(classId).stream()
                .collect(Collectors.toMap(CbtSubject::getSubjectId, CbtSubject::getSubjectNm));
    }

    private CbtQuestionResponse toQuestionDto(CbtQuestion question, Map<Long, String> subjectNames) {
        return CbtQuestionResponse.builder()
                .weekNo(question.getWeekNo())
                .questionId(question.getQuestionId())
                .classId(question.getClassId())
                .subjectId(question.getSubjectId())
                .subjectNm(subjectNames.get(question.getSubjectId()))
                .content(question.getContent())
                .explanation(question.getExplanation())
                .useYn(question.getUseYn())
                .createDt(question.getCreateDt())
                .options(question.getOptions().stream()
                        .sorted(Comparator.comparing(CbtQuestionOption::getOrderNo))
                        .map(o -> CbtQuestionResponse.Option.builder()
                                .optionId(o.getOptionId())
                                .orderNo(o.getOrderNo())
                                .content(o.getContent())
                                .correctYn(o.getCorrectYn())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // ===== 관리자: 응시 현황 =====

    @Override
    public CbtClassAttemptsResponse getClassAttempts(Long classId) {
        List<CbtAttempt> attempts = attemptRepository.findByClassIdOrderByUserIdAscRoundNoAsc(classId);

        Map<Long, List<CbtAttempt>> byUser = attempts.stream()
                .collect(Collectors.groupingBy(CbtAttempt::getUserId, LinkedHashMap::new, Collectors.toList()));

        Map<Long, User> users = userRepository.findAllById(byUser.keySet()).stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        List<CbtClassAttemptsResponse.StudentRow> rows = byUser.entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    List<CbtAttempt> userAttempts = entry.getValue();
                    User user = users.get(userId);
                    return CbtClassAttemptsResponse.StudentRow.builder()
                            .userId(userId)
                            .userName(user != null ? user.getName() : "(탈퇴)")
                            .email(user != null ? user.getEmail() : null)
                            .attemptCount(userAttempts.size())
                            .bestScore(userAttempts.stream().mapToDouble(CbtAttempt::getTotalScore).max().orElse(0))
                            .everPassed(userAttempts.stream().anyMatch(CbtAttempt::getPassYn))
                            .lastAttemptAt(userAttempts.get(userAttempts.size() - 1).getSubmittedAt())
                            .attempts(userAttempts.stream().map(this::toSummaryDto).collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());

        return CbtClassAttemptsResponse.builder()
                .studentCount(rows.size())
                .attemptCount(attempts.size())
                .students(rows)
                .build();
    }

    // ===== 학생 =====

    @Override
    public CbtInfoResponse getInfo(Long classId, Long userId) {
        CbtSubjectsResponse setting = getSubjects(classId);
        List<CbtSubjectResponse> activeSubjects = setting.getSubjects().stream()
                .filter(s -> Boolean.TRUE.equals(s.getUseYn()))
                .collect(Collectors.toList());

        String unavailableReason = null;
        if (!Boolean.TRUE.equals(setting.getConfigured()) || activeSubjects.isEmpty()) {
            unavailableReason = "CBT 과목 설정이 아직 완료되지 않았습니다.";
        } else if (Math.abs(setting.getTotalMaxScore() - TOTAL_SCORE_REQUIRED) > SCORE_EPSILON) {
            unavailableReason = "과목 배점 합계가 100점이 아닙니다.";
        } else {
            List<String> insufficient = activeSubjects.stream()
                    .filter(s -> s.getBankCount() < s.getQuestionCount())
                    .map(CbtSubjectResponse::getSubjectNm)
                    .collect(Collectors.toList());
            if (!insufficient.isEmpty()) {
                unavailableReason = "문제 은행 문항이 부족한 과목: " + String.join(", ", insufficient);
            }
        }

        List<CbtAttemptSummaryResponse> myAttempts =
                attemptRepository.findFullExamAttempts(classId, userId).stream()
                        .map(this::toSummaryDto)
                        .collect(Collectors.toList());

        return CbtInfoResponse.builder()
                .available(unavailableReason == null)
                .unavailableReason(unavailableReason)
                .passScore(setting.getPassScore())
                .totalQuestionCount(activeSubjects.stream().mapToInt(CbtSubjectResponse::getQuestionCount).sum())
                .subjects(activeSubjects)
                .myAttempts(myAttempts)
                .build();
    }

    @Override
    public CbtPracticeResponse getPracticeSet(Long classId) {
        List<CbtSubject> subjects = requireConfiguredSubjects(classId);

        List<CbtPracticeResponse.Question> questions = new ArrayList<>();
        int orderNo = 1;
        for (CbtSubject subject : subjects) {
            List<CbtQuestion> pool = questionRepository.findPlayable(classId, subject.getSubjectId());
            if (pool.size() < subject.getQuestionCount()) {
                throw new BusinessException(ErrorCode.CBT_QUESTION_BANK_INSUFFICIENT,
                        String.format("%s 과목의 문항이 부족합니다 (보유 %d / 필요 %d)",
                                subject.getSubjectNm(), pool.size(), subject.getQuestionCount()));
            }
            Collections.shuffle(pool);
            for (CbtQuestion question : pool.subList(0, subject.getQuestionCount())) {
                questions.add(CbtPracticeResponse.Question.builder()
                        .questionId(question.getQuestionId())
                        .subjectId(subject.getSubjectId())
                        .subjectNm(subject.getSubjectNm())
                        .orderNo(orderNo++)
                        .content(question.getContent())
                        .options(question.getOptions().stream()
                                .sorted(Comparator.comparing(CbtQuestionOption::getOrderNo))
                                .map(o -> new CbtPracticeResponse.Option(o.getOrderNo(), o.getContent()))
                                .collect(Collectors.toList()))
                        .build());
            }
        }

        return CbtPracticeResponse.builder()
                .totalCount(questions.size())
                .questions(questions)
                .build();
    }

    @Override
    @Transactional
    public CbtAttemptResultResponse submit(Long classId, Long userId, CbtSubmitRequest request) {
        List<CbtSubject> subjects = requireConfiguredSubjects(classId);
        CbtConfig config = configRepository.findByClassId(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CBT_NOT_CONFIGURED));

        Map<Long, Integer> selectedByQuestion = new LinkedHashMap<>();
        for (CbtSubmitRequest.Answer answer : request.getAnswers()) {
            selectedByQuestion.put(answer.getQuestionId(), answer.getSelectedOrderNo());
        }

        List<CbtQuestion> questions = questionRepository
                .findAllWithOptionsByIds(new ArrayList<>(selectedByQuestion.keySet())).stream()
                .filter(q -> q.getClassId().equals(classId))
                .collect(Collectors.toList());

        Map<Long, CbtSubject> subjectById = subjects.stream()
                .collect(Collectors.toMap(CbtSubject::getSubjectId, Function.identity()));

        // 문항별 채점 + 스냅샷
        Map<Long, Integer> correctBySubject = new HashMap<>();
        List<CbtAttemptResultResponse.Item> items = new ArrayList<>();
        for (CbtQuestion question : questions) {
            CbtSubject subject = subjectById.get(question.getSubjectId());
            if (subject == null) {
                continue; // 비활성/삭제 과목 문항은 채점 제외
            }
            Integer selected = selectedByQuestion.get(question.getQuestionId());
            Integer correctOrderNo = question.correctOrderNo();
            boolean correct = selected != null && selected.equals(correctOrderNo);
            if (correct) {
                correctBySubject.merge(subject.getSubjectId(), 1, Integer::sum);
            }
            items.add(CbtAttemptResultResponse.Item.builder()
                    .questionId(question.getQuestionId())
                    .subjectId(subject.getSubjectId())
                    .subjectNm(subject.getSubjectNm())
                    .content(question.getContent())
                    .explanation(question.getExplanation())
                    .options(question.getOptions().stream()
                            .sorted(Comparator.comparing(CbtQuestionOption::getOrderNo))
                            .map(o -> new CbtPracticeResponse.Option(o.getOrderNo(), o.getContent()))
                            .collect(Collectors.toList()))
                    .selectedOrderNo(selected)
                    .correctOrderNo(correctOrderNo)
                    .correct(correct)
                    .build());
        }

        // 과목별 점수 / 과락 판정
        List<CbtAttemptResultResponse.SubjectScore> subjectScores = new ArrayList<>();
        List<String> failedSubjects = new ArrayList<>();
        double total = 0;
        for (CbtSubject subject : subjects) {
            int correctCount = Math.min(
                    correctBySubject.getOrDefault(subject.getSubjectId(), 0),
                    subject.getQuestionCount());
            double score = round2(correctCount * subject.getPointPerQuestion());
            boolean failed = Boolean.TRUE.equals(subject.getCutYn()) && score < subject.getCutScore() - SCORE_EPSILON;
            if (failed) {
                failedSubjects.add(subject.getSubjectNm());
            }
            total += score;
            subjectScores.add(CbtAttemptResultResponse.SubjectScore.builder()
                    .subjectId(subject.getSubjectId())
                    .subjectNm(subject.getSubjectNm())
                    .score(score)
                    .maxScore(round2(subject.maxScore()))
                    .cutScore(subject.getCutScore())
                    .cutYn(subject.getCutYn())
                    .failed(failed)
                    .correctCount(correctCount)
                    .questionCount(subject.getQuestionCount())
                    .build());
        }
        total = round2(total);

        boolean pass = failedSubjects.isEmpty() && total >= config.getPassScore() - SCORE_EPSILON;
        String failReason = null;
        if (!pass) {
            failReason = failedSubjects.isEmpty()
                    ? String.format("총점 미달 (%s점 / 합격 %s점)", total, config.getPassScore())
                    : "과락: " + String.join(", ", failedSubjects);
        }

        int roundNo = attemptRepository.findMaxRoundNo(classId, userId) + 1;
        CbtAttempt attempt = attemptRepository.save(CbtAttempt.create(
                classId, userId, roundNo, total, pass,
                toJson(subjectScores), toJson(items)));

        return CbtAttemptResultResponse.builder()
                .attemptId(attempt.getAttemptId())
                .roundNo(roundNo)
                .totalScore(total)
                .passScore(config.getPassScore())
                .passYn(pass)
                .failReason(failReason)
                .submittedAt(attempt.getSubmittedAt())
                .subjectScores(subjectScores)
                .items(items)
                .build();
    }

    @Override
    public CbtAttemptResultResponse getAttempt(Long attemptId, Long userId, boolean admin) {
        CbtAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "응시 기록을 찾을 수 없습니다: " + attemptId));
        if (!admin && !attempt.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_PERMISSIONS, "본인의 응시 기록만 조회할 수 있습니다.");
        }

        List<CbtAttemptResultResponse.SubjectScore> subjectScores =
                fromJson(attempt.getSubjectScoresJson(), CbtAttemptResultResponse.SubjectScore.class);
        List<String> failedSubjects = subjectScores.stream()
                .filter(s -> Boolean.TRUE.equals(s.getFailed()))
                .map(CbtAttemptResultResponse.SubjectScore::getSubjectNm)
                .collect(Collectors.toList());

        Double passScore = configRepository.findByClassId(attempt.getClassId())
                .map(CbtConfig::getPassScore).orElse(null);

        String failReason = null;
        if (!Boolean.TRUE.equals(attempt.getPassYn())) {
            failReason = failedSubjects.isEmpty() ? "총점 미달" : "과락: " + String.join(", ", failedSubjects);
        }

        return CbtAttemptResultResponse.builder()
                .attemptId(attempt.getAttemptId())
                .roundNo(attempt.getRoundNo())
                .totalScore(attempt.getTotalScore())
                .passScore(passScore)
                .passYn(attempt.getPassYn())
                .failReason(failReason)
                .submittedAt(attempt.getSubmittedAt())
                .subjectScores(subjectScores)
                .items(fromJson(attempt.getAnswersJson(), CbtAttemptResultResponse.Item.class))
                .build();
    }

    // ===== 공통 =====

    // 응시 가능 상태의 활성 과목 목록 (설정 미완료/배점 불일치 시 예외)
    private List<CbtSubject> requireConfiguredSubjects(Long classId) {
        List<CbtSubject> subjects = subjectRepository.findByClassIdAndUseYnTrueOrderByOrderNoAsc(classId);
        if (subjects.isEmpty() || configRepository.findByClassId(classId).isEmpty()) {
            throw new BusinessException(ErrorCode.CBT_NOT_CONFIGURED);
        }
        double total = subjects.stream().mapToDouble(CbtSubject::maxScore).sum();
        if (Math.abs(total - TOTAL_SCORE_REQUIRED) > SCORE_EPSILON) {
            throw new BusinessException(ErrorCode.CBT_SUBJECT_TOTAL_INVALID);
        }
        return subjects;
    }

    private CbtAttemptSummaryResponse toSummaryDto(CbtAttempt attempt) {
        return CbtAttemptSummaryResponse.builder()
                .attemptId(attempt.getAttemptId())
                .roundNo(attempt.getRoundNo())
                .totalScore(attempt.getTotalScore())
                .passYn(attempt.getPassYn())
                .submittedAt(attempt.getSubmittedAt())
                .subjectScores(fromJson(attempt.getSubjectScoresJson(), CbtAttemptResultResponse.SubjectScore.class))
                .build();
    }

    private static double round2(double value) {
        return Math.round(value * 100) / 100.0;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("CBT JSON 직렬화 실패", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "결과 저장 중 오류가 발생했습니다.");
        }
    }

    private <T> List<T> fromJson(String json, Class<T> elementType) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (JsonProcessingException e) {
            log.error("CBT JSON 역직렬화 실패", e);
            return Collections.emptyList();
        }
    }

    // ===== 주차별 CBT =====

    // 반 -> 커리큘럼 주차 목록 (설정 화면의 주차 뼈대)
    private List<CurriculumWeek> curriculumWeeks(Long classId) {
        Long curId = classEntityRepository.findById(classId)
                .map(c -> c.getCurriculum().getCurId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ENTITY_NOT_FOUND,
                        "반을 찾을 수 없습니다: " + classId));
        return curriculumWeekRepository.findByCurIdOrderByWeekNo(curId);
    }

    @Override
    public CbtWeekConfigResponse getWeekConfigs(Long classId) {
        List<CurriculumWeek> weeks = curriculumWeeks(classId);
        Map<Integer, CbtWeekConfig> configByWeek = weekConfigRepository
                .findByClassIdOrderByWeekNoAsc(classId).stream()
                .collect(Collectors.toMap(CbtWeekConfig::getWeekNo, Function.identity(), (a, b) -> a));

        List<CbtWeekConfigResponse.Item> items = new ArrayList<>();
        for (CurriculumWeek week : weeks) {
            CbtWeekConfig config = configByWeek.get(week.getWeekNo());
            int questionCount = config != null ? config.getQuestionCount() : CbtWeekConfig.DEFAULT_QUESTION_COUNT;
            long bankCount = questionRepository.countByClassIdAndWeekNoAndUseYnTrue(classId, week.getWeekNo());
            items.add(CbtWeekConfigResponse.Item.builder()
                    .weekNo(week.getWeekNo())
                    .title(week.getTitle() != null ? week.getTitle() : week.getSubtitle())
                    .questionCount(questionCount)
                    .passScore(config != null ? config.getPassScore() : CbtWeekConfig.DEFAULT_PASS_SCORE)
                    .useYn(config == null || Boolean.TRUE.equals(config.getUseYn()))
                    .bankCount(bankCount)
                    .insufficient(bankCount < questionCount)
                    .build());
        }
        return CbtWeekConfigResponse.builder()
                .totalWeeks(weeks.size())
                .weeks(items)
                .build();
    }

    @Override
    @Transactional
    public CbtWeekConfigResponse saveWeekConfigs(Long classId, CbtWeekConfigSaveRequest request) {
        Map<Integer, CbtWeekConfig> existing = weekConfigRepository
                .findByClassIdOrderByWeekNoAsc(classId).stream()
                .collect(Collectors.toMap(CbtWeekConfig::getWeekNo, Function.identity(), (a, b) -> a));

        for (CbtWeekConfigSaveRequest.Item item : request.getWeeks()) {
            CbtWeekConfig config = existing.get(item.getWeekNo());
            if (config != null) {
                config.updateSettings(item.getQuestionCount(), item.getPassScore(), item.getUseYn());
            } else {
                weekConfigRepository.save(CbtWeekConfig.builder()
                        .classId(classId)
                        .weekNo(item.getWeekNo())
                        .questionCount(item.getQuestionCount())
                        .passScore(item.getPassScore())
                        .useYn(item.getUseYn())
                        .build());
            }
        }
        return getWeekConfigs(classId);
    }

    // 주차 설정 조회 (없으면 기본값)
    private CbtWeekConfig weekConfigOrDefault(Long classId, Integer weekNo) {
        return weekConfigRepository.findByClassIdAndWeekNo(classId, weekNo)
                .orElseGet(() -> CbtWeekConfig.createDefault(classId, weekNo));
    }

    @Override
    public CbtPracticeResponse getWeekPracticeSet(Long classId, Integer weekNo) {
        CbtWeekConfig config = weekConfigOrDefault(classId, weekNo);
        if (!Boolean.TRUE.equals(config.getUseYn())) {
            throw new BusinessException(ErrorCode.CBT_WEEK_NOT_AVAILABLE,
                    weekNo + "주차 CBT 는 현재 사용하지 않도록 설정되어 있습니다.");
        }

        List<CbtQuestion> pool = questionRepository.findPlayableByWeek(classId, weekNo);
        if (pool.isEmpty()) {
            throw new BusinessException(ErrorCode.CBT_QUESTION_BANK_INSUFFICIENT,
                    weekNo + "주차에 등록된 문항이 없습니다.");
        }

        // 보유 문항이 설정 수보다 적으면 있는 만큼만 출제한다 (응시 자체를 막지 않음)
        int take = Math.min(config.getQuestionCount(), pool.size());
        boolean reduced = take < config.getQuestionCount();

        Collections.shuffle(pool);
        Map<Long, String> subjectNames = subjectNameMap(classId);
        List<CbtPracticeResponse.Question> questions = new ArrayList<>();
        int orderNo = 1;
        for (CbtQuestion question : pool.subList(0, take)) {
            questions.add(CbtPracticeResponse.Question.builder()
                    .questionId(question.getQuestionId())
                    .subjectId(question.getSubjectId())
                    .subjectNm(subjectNames.get(question.getSubjectId()))
                    .orderNo(orderNo++)
                    .content(question.getContent())
                    .options(question.getOptions().stream()
                            .sorted(Comparator.comparing(CbtQuestionOption::getOrderNo))
                            .map(o -> new CbtPracticeResponse.Option(o.getOrderNo(), o.getContent()))
                            .collect(Collectors.toList()))
                    .build());
        }

        return CbtPracticeResponse.builder()
                .totalCount(questions.size())
                .weekNo(weekNo)
                .reduced(reduced)
                .questions(questions)
                .build();
    }

    @Override
    @Transactional
    public CbtWeekResultResponse submitWeek(Long classId, Long userId, Integer weekNo,
                                            CbtSubmitRequest request) {
        CbtWeekConfig config = weekConfigOrDefault(classId, weekNo);

        Map<Long, Integer> selectedByQuestion = new LinkedHashMap<>();
        for (CbtSubmitRequest.Answer answer : request.getAnswers()) {
            selectedByQuestion.put(answer.getQuestionId(), answer.getSelectedOrderNo());
        }

        List<CbtQuestion> questions = questionRepository
                .findAllWithOptionsByIds(new ArrayList<>(selectedByQuestion.keySet())).stream()
                .filter(q -> q.getClassId().equals(classId))
                .collect(Collectors.toList());
        if (questions.isEmpty()) {
            throw new BusinessException(ErrorCode.CBT_QUESTION_BANK_INSUFFICIENT, "채점할 문항이 없습니다.");
        }

        Map<Long, String> subjectNames = subjectNameMap(classId);
        List<CbtAttemptResultResponse.Item> items = new ArrayList<>();
        int correctCount = 0;
        for (CbtQuestion question : questions) {
            Integer selected = selectedByQuestion.get(question.getQuestionId());
            Integer correctOrderNo = question.correctOrderNo();
            boolean correct = selected != null && selected.equals(correctOrderNo);
            if (correct) {
                correctCount++;
            }
            items.add(CbtAttemptResultResponse.Item.builder()
                    .questionId(question.getQuestionId())
                    .subjectId(question.getSubjectId())
                    .subjectNm(subjectNames.get(question.getSubjectId()))
                    .content(question.getContent())
                    .explanation(question.getExplanation())
                    .options(question.getOptions().stream()
                            .sorted(Comparator.comparing(CbtQuestionOption::getOrderNo))
                            .map(o -> new CbtPracticeResponse.Option(o.getOrderNo(), o.getContent()))
                            .collect(Collectors.toList()))
                    .selectedOrderNo(selected)
                    .correctOrderNo(correctOrderNo)
                    .correct(correct)
                    .build());
        }

        // 주차별은 과목 배점/과락이 아닌 정답률(%) 로 판정한다
        int totalCount = questions.size();
        double score = round2(correctCount * 100.0 / totalCount);
        boolean pass = score >= config.getPassScore() - SCORE_EPSILON;

        int roundNo = attemptRepository.findMaxWeekRoundNo(classId, userId, weekNo) + 1;
        CbtAttempt attempt = attemptRepository.save(CbtAttempt.create(
                classId, userId, roundNo, weekNo, score, pass, null, toJson(items)));

        return CbtWeekResultResponse.builder()
                .attemptId(attempt.getAttemptId())
                .weekNo(weekNo)
                .roundNo(roundNo)
                .correctCount(correctCount)
                .totalCount(totalCount)
                .score(score)
                .passScore(config.getPassScore())
                .passYn(pass)
                .submittedAt(attempt.getSubmittedAt())
                .items(items)
                .build();
    }

}
