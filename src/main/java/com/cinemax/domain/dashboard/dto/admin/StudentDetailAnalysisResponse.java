package com.cinemax.domain.dashboard.dto.admin;

import com.cinemax.domain.dashboard.dto.common.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 학생 상세 분석 리포트 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailAnalysisResponse {

    // 학생 정보
    private Long userId;
    private String userName;
    private String userEmail;

    // 주차별 세션 정보
    private Long weeklySessionId;
    private Integer weekNo;
    private String className;

    // 과제 제출 분석
    private AssignmentAnalysis assignmentAnalysis;

    // 학습 시간 분석
    private StudyTimeAnalysis studyTimeAnalysis;

    // 학습 진도 분석
    private ProgressAnalysis progressAnalysis;

    // 활동 분석
    private ActivityAnalysis activityAnalysis;

    // 질문/답변 분석
    private QnAAnalysis qnaAnalysis;

    // 분석 데이터로부터 응답 생성
    public static StudentDetailAnalysisResponse of(
            Long userId, String userName, String userEmail,
            Long weeklySessionId, Integer weekNo, String className,
            AssignmentAnalysis assignmentAnalysis,
            StudyTimeAnalysis studyTimeAnalysis,
            ProgressAnalysis progressAnalysis,
            ActivityAnalysis activityAnalysis,
            QnAAnalysis qnaAnalysis) {

        return StudentDetailAnalysisResponse.builder()
                .userId(userId)
                .userName(userName)
                .userEmail(userEmail)
                .weeklySessionId(weeklySessionId)
                .weekNo(weekNo)
                .className(className)
                .assignmentAnalysis(assignmentAnalysis)
                .studyTimeAnalysis(studyTimeAnalysis)
                .progressAnalysis(progressAnalysis)
                .activityAnalysis(activityAnalysis)
                .qnaAnalysis(qnaAnalysis)
                .build();
    }
}
