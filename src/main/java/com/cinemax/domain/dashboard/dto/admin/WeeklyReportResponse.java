package com.cinemax.domain.dashboard.dto.admin;


import com.cinemax.domain.dashboard.dto.common.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 주차별 종합 리포트 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyReportResponse {

    // 주차 정보
    private Long weeklySessionId;
    private Long inviteId;
    private Integer weekNo;
    private String className;

    // 학생 통계
    private StudentStatistics studentStatistics;

    // 과제 제출 통계
    private SubmissionStatistics submissionStatistics;

    // 학습 시간 통계
    private WorkHourStatistics workHourStatistics;

    // 질문 통계
    private QuestionStatistics questionStatistics;

    // 진도 통계
    private ProgressStatistics progressStatistics;

    // 학생별 요약 (상위 5명, 도움 필요 학생)
    private List<StudentSummary> topStudents;
    private List<StudentSummary> studentsNeedingHelp;

    // 통계 데이터로부터 응답 생성
    public static WeeklyReportResponse of(
            Long weeklySessionId, Long inviteId, Integer weekNo, String className,
            StudentStatistics studentStatistics,
            SubmissionStatistics submissionStatistics,
            WorkHourStatistics workHourStatistics,
            QuestionStatistics questionStatistics,
            ProgressStatistics progressStatistics,
            List<StudentSummary> topStudents,
            List<StudentSummary> studentsNeedingHelp) {

        return WeeklyReportResponse.builder()
                .weeklySessionId(weeklySessionId)
                .inviteId(inviteId)
                .weekNo(weekNo)
                .className(className)
                .studentStatistics(studentStatistics)
                .submissionStatistics(submissionStatistics)
                .workHourStatistics(workHourStatistics)
                .questionStatistics(questionStatistics)
                .progressStatistics(progressStatistics)
                .topStudents(topStudents)
                .studentsNeedingHelp(studentsNeedingHelp)
                .build();
    }
}
