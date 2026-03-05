package com.cinemax.domain.testcase.service;

import com.cinemax.domain.testcase.dto.TestCaseRequest;
import com.cinemax.domain.testcase.dto.TestCaseResponse;
import com.cinemax.domain.testcase.entity.TestCase;
import com.cinemax.domain.testcase.mapper.TestCaseMapper;
import com.cinemax.domain.testcase.repository.TestCaseRepository;
import com.cinemax.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 테스트 케이스 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final TestCaseMapper testCaseMapper;

    /**
     * 특정 과제의 모든 테스트 케이스 조회 (전체 정보 - 교수용)
     */
    public List<TestCaseResponse> getAllTestCases(Long taskId, Long cycleId) {

        List<TestCase> testCases = testCaseRepository.findAllByTaskId(taskId, cycleId);

        if (testCases.isEmpty()) {
            log.warn("테스트 케이스가 존재하지 않습니다 - taskId: {}", taskId);
        }

        return testCases.stream()
                .map(testCaseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 과제의 공개 테스트 케이스 조회 (학생용 - testCode 제외) - Mapper의 toPublicResponseDto()를 사용하여 공개 정보만 변환
     */
    public List<TestCaseResponse> getPublicTestCases(Long taskId, Long cycleId) {

        List<TestCase> testCases = testCaseRepository.findAllByTaskId(taskId, cycleId);

        return testCases.stream()
                .map(testCaseMapper::toPublicResponse)
                .collect(Collectors.toList());
    }

    // 시작 코드 가져오기 (첫 번째 테스트 케이스의 START_CODE)
    public String getStartCode(Long taskId, Long cycleId) {

        List<TestCase> testCases = testCaseRepository.findAllByTaskId(taskId, cycleId);

        if (testCases.isEmpty()) {
            throw new IllegalArgumentException("테스트 케이스가 존재하지 않습니다.");
        }

        return testCases.get(0).getStartCode();
    }

    // 테스트 케이스 개수 조회
    public Long getTestCaseCount(Long taskId, Long cycleId) {

        return testCaseRepository.countByTaskId(taskId, cycleId);
    }

    // 총 가중치 합계 조회
    public Integer getTotalWeight(Long taskId, Long cycleId) {

        Integer totalWeight = testCaseRepository.sumWeightByTaskId(taskId, cycleId);

        return totalWeight != null ? totalWeight : 0;
    }

    // 테스트 케이스 존재 여부 확인
    public boolean hasTestCases(Long taskId, Long cycleId) {

        return testCaseRepository.countByTaskId(taskId, cycleId) > 0;
    }

    /**
     * 테스트 케이스 생성
     */
    @Transactional
    public TestCaseResponse createTestCase(TestCaseRequest request) {

        TestCase testCase = TestCase.create(
                request.getTaskId(),
                request.getCycleId(),
                request.getStartCode(),
                request.getTestCode(),
                request.getInputText(),
                request.getExpectedOutput(),
                request.getWeight()
        );

        TestCase savedTestCase = testCaseRepository.save(testCase);

        log.info("테스트 케이스가 생성되었습니다 - testCaseId: {}, taskId: {}",
                savedTestCase.getTestCaseId(), savedTestCase.getTaskId());

        return testCaseMapper.toResponse(savedTestCase);
    }

    /**
     * 테스트 케이스 단일 조회
     */
    public TestCaseResponse getTestCase(Long testCaseId) {

        TestCase testCase = testCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new ResourceNotFoundException("테스트 케이스를 찾을 수 없습니다 - testCaseId: " + testCaseId));

        return testCaseMapper.toResponse(testCase);
    }

    /**
     * 테스트 케이스 수정
     */
    @Transactional
    public TestCaseResponse updateTestCase(Long testCaseId, TestCaseRequest request) {

        TestCase testCase = testCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new ResourceNotFoundException("테스트 케이스를 찾을 수 없습니다 - testCaseId: " + testCaseId));

        testCase.update(
                request.getStartCode(),
                request.getTestCode(),
                request.getInputText(),
                request.getExpectedOutput(),
                request.getWeight()
        );

        log.info("테스트 케이스가 수정되었습니다 - testCaseId: {}", testCaseId);

        return testCaseMapper.toResponse(testCase);
    }

    /**
     * 테스트 케이스 삭제
     */
    @Transactional
    public void deleteTestCase(Long testCaseId) {

        TestCase testCase = testCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new ResourceNotFoundException("테스트 케이스를 찾을 수 없습니다 - testCaseId: " + testCaseId));

        testCaseRepository.delete(testCase);

        log.info("테스트 케이스가 삭제되었습니다 - testCaseId: {}", testCaseId);
    }
}
