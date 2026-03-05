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

}