package com.cinemax.domain.worklog.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 업무일지 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "주차별 수업 ID는 필수입니다.")
    private Long weeklySessionId;

    @NotNull(message = "작성 날짜는 필수입니다.")
    private LocalDate logDate;

    @NotBlank(message = "업무 내용은 필수입니다.")
    @Size(max = 5000, message = "업무 내용은 5000자 이내로 작성해주세요.")
    private String content;

    @NotNull(message = "작업 시간은 필수입니다.")
    @DecimalMin(value = "0.0", message = "작업 시간은 0 이상이어야 합니다.")
    @DecimalMax(value = "24.0", message = "작업 시간은 24시간을 초과할 수 없습니다.")
    private BigDecimal workHours;

    @Size(max = 5000, message = "성과는 5000자 이내로 작성해주세요.")
    private String achievements;

    @Min(value = 1, message = "난이도는 1 이상이어야 합니다.")
    @Max(value = 5, message = "난이도는 5 이하여야 합니다.")
    private Integer difficultyLevel;

    @Min(value = 1, message = "코드 활용 점수는 1 이상이어야 합니다.")
    @Max(value = 5, message = "코드 활용 점수는 5 이하여야 합니다.")
    private Integer proficiencyLevel;

    @Size(max = 2000, message = "메모는 2000자 이내로 작성해주세요.")
    private String notes;

    @Size(max = 1000, message = "의미있었던 내용은 1000자 이내로 작성해주세요.")
    private String meaningfulContent;

    @Size(max = 1000, message = "어려웠던 내용은 1000자 이내로 작성해주세요.")
    private String difficultContent;

    @Size(max = 1000, message = "궁금한 점은 1000자 이내로 작성해주세요.")
    private String questionContent;
}
