package com.cinemax.domain.code.service;

import com.cinemax.domain.activity.service.ActivityMonitorService;
import com.cinemax.domain.classes.entity.ClassSubmit;
import com.cinemax.domain.classes.repository.ClassSubmitRepository;
import com.cinemax.domain.code.dto.*;
import com.cinemax.domain.progress.entity.Progress;
import com.cinemax.domain.progress.repository.ProgressRepository;
import com.cinemax.domain.task.repository.TaskRepository;
import com.cinemax.domain.testcase.dto.TestCaseResultRequest;
import com.cinemax.domain.testcase.dto.TestCaseResultResponse;
import com.cinemax.domain.testcase.entity.TestCase;
import com.cinemax.domain.testcase.repository.TestCaseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 코드 제출 및 자동 채점 서비스
 * 프론트엔드에서 실행한 테스트 결과를 받아 DB에 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeSubmitService {

    private final ClassSubmitRepository classSubmitRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProgressRepository progressRepository;
    private final TaskRepository taskRepository;
    private final ActivityMonitorService activityMonitorService;
    private final ObjectMapper objectMapper;

    // 코드 제출 및 자동 채점
    @Transactional
    public SubmitResultResponse submitCode(CodeSubmitRequest request, Long userId) {

        // 테스트 케이스 조회 (검증 목적)
        List<TestCase> testCases = testCaseRepository.findAllByTaskId(request.getTaskId(), request.getCycleId());
        if (testCases.isEmpty()) {
            log.error("테스트 케이스가 존재하지 않습니다 - taskId: {}, cycleId: {}", request.getTaskId(), request.getCycleId());
            throw new IllegalArgumentException(
                String.format("테스트 케이스가 존재하지 않습니다. (taskId: %d, cycleId: %d)",
                    request.getTaskId(), request.getCycleId())
            );
        }

        Map<Long, TestCase> testCaseMap = testCases.stream()
                .collect(Collectors.toMap(
                        TestCase::getTestCaseId,
                        tc -> tc
                ));

        // 프론트엔드에서 받은 테스트 결과 처리
        List<TestCaseResultResponse> testResults = new ArrayList<>();
        int passedCount = 0;
        int totalWeight = 0;
        int earnedWeight = 0;

        for (TestCaseResultRequest resultRequest : request.getTestCaseResults()) {
            Long testCaseId = resultRequest.getTestCaseId();

            TestCase testCase = testCaseMap.get(testCaseId);
            if (testCase == null) {
                log.warn("존재하지 않는 테스트 케이스 ID: {}", testCaseId);
                continue;
            }

            boolean passed = resultRequest.getPassed();
            Integer weight = resultRequest.getWeight() != null ?
                    resultRequest.getWeight() : testCase.getWeight();

            if (passed) {
                passedCount++;
                earnedWeight += weight;
            }

            totalWeight += weight;

            // 테스트 결과 저장
            TestCaseResultResponse testResult = TestCaseResultResponse.builder()
                    .testCaseId(testCaseId)
                    .passed(passed)
                    .input(resultRequest.getInput() != null ?
                            resultRequest.getInput() : testCase.getInputText())
                    .expectedOutput(resultRequest.getExpectedOutput() != null ?
                            resultRequest.getExpectedOutput() : testCase.getExpectedOutput())
                    .actualOutput(resultRequest.getActualOutput())
                    .errorMessage(resultRequest.getErrorMessage())
                    .executionTime(resultRequest.getExecutionTime())
                    .weight(weight)
                    .build();

            testResults.add(testResult);
        }

        // 점수 계산
        BigDecimal score = calculateScore(earnedWeight, totalWeight);
        boolean allPassed = (passedCount == testResults.size()) && (testResults.size() == testCases.size());

        // 제출 이력 저장
        boolean isFirstSubmit = classSubmitRepository.isFirstSubmission(
                request.getTaskId(), request.getClassId());

        Long submitCount = classSubmitRepository.countSubmissions(
                request.getTaskId(), request.getClassId());

        Integer submitNum = submitCount.intValue() + 1;

        // 상세 결과 JSON 생성
        String detailJson = createDetailJson(testResults);

        // 재제출 덮어쓰기: 같은 (본인+세션+사이클)의 이전 유효 제출을 무효화
        // (난이도 전환으로 taskId가 달라져도 같은 사이클이면 최신 제출만 유효 - 통계 왜곡 방지)
        if (request.getWeeklySessionId() != null && request.getCycleId() != null) {
            classSubmitRepository.findActiveUserCycleSubmissions(
                            userId, request.getWeeklySessionId(), request.getCycleId())
                    .forEach(ClassSubmit::invalidate);
        }

        ClassSubmit classSubmit = ClassSubmit.create(
                userId,
                request.getTaskId(),
                request.getClassId(),
                request.getCycleId(),
                request.getWeeklySessionId(),
                allPassed,
                score,
                detailJson,
                isFirstSubmit,
                submitNum
        );

        ClassSubmit savedSubmit = classSubmitRepository.save(classSubmit);

        // 해당 과제에 대한 성공률 계산 (이번 제출 포함)
        Double successRateDouble = classSubmitRepository.calculateTaskSuccessRate(
                request.getTaskId(), request.getClassId());
        BigDecimal successRate = BigDecimal.valueOf(successRateDouble != null ? successRateDouble : 0.0)
                .setScale(2, RoundingMode.HALF_UP);

        return SubmitResultResponse.builder()
                .submitId(savedSubmit.getSubmitId())
                .taskId(request.getTaskId())
                .classId(request.getClassId())
                .cycleId(request.getCycleId())
                .result(allPassed)
                .score(score)
                .passedTests(passedCount)
                .totalTests(testResults.size())
                .submitNum(submitNum)
                .isFirstSubmit(isFirstSubmit)
                .submitAt(LocalDateTime.now())
                .testCaseResults(testResults)
                .message(allPassed ? "모든 테스트를 통과했습니다!" : passedCount + "개의 테스트를 통과했습니다.")
                .successRate(successRate)
                .build();
    }
    // 출력 결과 비교
    private boolean compareOutput(String expected, String actual, Boolean success) {
        if (!success || actual == null) {
            return false;
        }

        // 공백 및 개행 정규화
        String normalizedExpected = expected.trim().replaceAll("\\s+", " ");
        String normalizedActual = actual.trim().replaceAll("\\s+", " ");

        return normalizedExpected.equals(normalizedActual);
    }

    // 점수 계산 (가중치 기반)
    private BigDecimal calculateScore(int earnedWeight, int totalWeight) {
        if (totalWeight == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(earnedWeight)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalWeight), 2, RoundingMode.HALF_UP);
    }

    // 상세 결과 JSON 생성
    private String createDetailJson(List<TestCaseResultResponse> testResults) {
        try {
            return objectMapper.writeValueAsString(testResults);
        } catch (JsonProcessingException e) {
            log.error("JSON 생성 실패", e);
            return "[]";
        }
    }
}
