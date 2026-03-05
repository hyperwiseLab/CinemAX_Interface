package com.cinemax.domain.code.dto;

import com.cinemax.domain.testcase.dto.TestCaseResultResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 제출 결과 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 제출 결과")
public class SubmitResultResponse {

    @Schema(description = "제출 ID", example = "1")
    private Long submitId;

    @Schema(description = "과제 ID", example = "1")
    private Long taskId;

    @Schema(description = "수업 ID", example = "1")
    private Long classId;

    @Schema(description = "사이클 ID", example = "1")
    private Long cycleId;

    @Schema(description = "전체 통과 여부", example = "true")
    private Boolean result;

    @Schema(description = "점수", example = "85.5")
    private BigDecimal score;

    @Schema(description = "통과한 테스트 케이스 수", example = "8")
    private Integer passedTests;

    @Schema(description = "전체 테스트 케이스 수", example = "10")
    private Integer totalTests;

    @Schema(description = "제출 횟수", example = "3")
    private Integer submitNum;

    @Schema(description = "첫 제출 여부", example = "false")
    private Boolean isFirstSubmit;

    @Schema(description = "제출 시각")
    private LocalDateTime submitAt;

    @Schema(description = "테스트 케이스별 결과")
    private List<TestCaseResultResponse> testCaseResults;

    @Schema(description = "메시지", example = "제출이 완료되었습니다.")
    private String message;

    @Schema(description = "해당 과제의 성공률 (%)", example = "66.67")
    private BigDecimal successRate;
}
