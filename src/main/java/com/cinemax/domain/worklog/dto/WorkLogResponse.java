package com.cinemax.domain.worklog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 업무일지 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogResponse {

    private Long userId;
    private String userName;
    private String userEmail;
    private Long weeklySessionId;
    private Integer weekNo;
    private LocalDate logDate;
    private String content;
    private BigDecimal workHours;
    private String achievements;
    private Integer difficultyLevel;
    private Integer proficiencyLevel;
    private String notes;
    private String meaningfulContent;
    private String difficultContent;
    private String questionContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 교수 피드백
    private String professorFeedback;
    private LocalDateTime feedbackDate;
    private Double feedbackScore;
}
