package com.cinemax.domain.classes.service;

import com.cinemax.domain.classes.dto.ClassSubmitResponse;

import java.util.List;

public interface ClassSubmitService {

    List<ClassSubmitResponse> getStudentSubmissions(Long taskId, Long classId, Long userId);

    ClassSubmitResponse getLatestSubmission(Long taskId, Long classId, Long userId);

    List<ClassSubmitResponse> getAllTaskSubmissions(Long taskId, Long classId);

    List<ClassSubmitResponse> getAllWeeklySessionSubmissions(Long weeklySessionId, Long classId, Long userId, String role);

    List<ClassSubmitResponse> getAllClassSubmissions(Long classId, Long userId, String role);

    Long getSubmissionCount(Long taskId, Long classId, Long userId);

    Long getPassedStudentCount(Long taskId, Long classId);

    boolean hasSubmission(Long taskId, Long classId, Long userId);

    // 학생별 세션+사이클 기준 제출 여부 (중복 제출 판정)
    boolean hasCycleSubmission(Long userId, Long weeklySessionId, Long cycleId);

}