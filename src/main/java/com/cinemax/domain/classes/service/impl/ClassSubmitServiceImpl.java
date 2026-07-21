package com.cinemax.domain.classes.service.impl;

import com.cinemax.domain.classes.dto.ClassSubmitResponse;
import com.cinemax.domain.classes.entity.ClassSubmit;
import com.cinemax.domain.classes.repository.ClassSubmitRepository;
import com.cinemax.domain.classes.service.ClassSubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 제출 이력 조회 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassSubmitServiceImpl implements ClassSubmitService {

    private final ClassSubmitRepository classSubmitRepository;

    // 특정 학생의 특정 과제 제출 이력 조회
    @Override
    public List<ClassSubmitResponse> getStudentSubmissions(Long taskId, Long classId, Long userId) {
        List<ClassSubmit> submissions = classSubmitRepository.findByTaskAndClass(taskId, classId);

        return submissions.stream()
                .map(ClassSubmitResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 학생의 최신 제출 조회
    @Override
    public ClassSubmitResponse getLatestSubmission(Long taskId, Long classId, Long userId) {
        List<ClassSubmit> submissions = classSubmitRepository.findByTaskAndClass(taskId, classId);

        if (submissions.isEmpty()) {
            throw new IllegalArgumentException("제출 이력이 없습니다.");
        }

        // 이미 submitAt DESC로 정렬되어 있으므로 첫 번째 항목이 최신
        return ClassSubmitResponse.from(submissions.get(0));
    }

    // 특정 과제의 모든 제출 조회 (교수용)
    @Override
    public List<ClassSubmitResponse> getAllTaskSubmissions(Long taskId, Long classId) {
        List<ClassSubmit> submissions = classSubmitRepository.findAllByTask(taskId, classId);

        return submissions.stream()
                .map(ClassSubmitResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 주차 수업의 모든 제출 조회 (권한별 필터링)
    @Override
    public List<ClassSubmitResponse> getAllWeeklySessionSubmissions(Long weeklySessionId, Long classId, Long userId, String role) {
        List<ClassSubmit> submissions = classSubmitRepository.findAllByWeeklySession(weeklySessionId, classId);

        // 학생인 경우 자기 제출만 필터링
        if ("STUDENT".equals(role)) {
            submissions = submissions.stream()
                    .filter(submit -> userId.equals(submit.getUserId()))
                    .collect(Collectors.toList());
        }
        // 교수/관리자는 모든 제출 조회

        return submissions.stream()
                .map(ClassSubmitResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 수업의 모든 제출 조회 (권한별 필터링)
    @Override
    public List<ClassSubmitResponse> getAllClassSubmissions(Long classId, Long userId, String role) {
        List<ClassSubmit> submissions = classSubmitRepository.findAllByClass(classId);

        // 학생인 경우 자기 제출만 필터링
        if ("STUDENT".equals(role)) {
            submissions = submissions.stream()
                    .filter(submit -> userId.equals(submit.getUserId()))
                    .collect(Collectors.toList());
        }
        // 교수/관리자는 모든 제출 조회

        return submissions.stream()
                .map(ClassSubmitResponse::from)
                .collect(Collectors.toList());
    }

    // 제출 횟수 조회
    @Override
    public Long getSubmissionCount(Long taskId, Long classId, Long userId) {

        return classSubmitRepository.countSubmissions(taskId, classId);
    }

    // 합격한 학생 수 조회
    @Override
    public Long getPassedStudentCount(Long taskId, Long classId) {

        return classSubmitRepository.countPassedStudents(taskId, classId);
    }

    // 제출 여부 확인
    @Override
    public boolean hasSubmission(Long taskId, Long classId, Long userId) {

        return classSubmitRepository.countSubmissions(taskId, classId) > 0;
    }

    // 학생별 세션+사이클 기준 제출 여부 (중복 제출 판정)
    @Override
    public boolean hasCycleSubmission(Long userId, Long weeklySessionId, Long cycleId) {

        return classSubmitRepository.countUserCycleSubmissions(userId, weeklySessionId, cycleId) > 0;
    }
}
