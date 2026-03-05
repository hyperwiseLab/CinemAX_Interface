package com.cinemax.domain.testcase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 테스트 케이스 결과
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "테스트 케이스 실행 결과")
public class TestCaseResultResponse {

    @Schema(description = "테스트 케이스 ID", example = "1")
    private Long testCaseId;

    @Schema(description = "통과 여부", example = "true")
    private Boolean passed;

    @Schema(description = "입력", example = "5")
    private String input;

    @Schema(description = "예상 출력", example = "120")
    private String expectedOutput;

    @Schema(description = "실제 출력", example = "120")
    private String actualOutput;

    @Schema(description = "에러 메시지")
    private String errorMessage;

    @Schema(description = "실행 시간 (밀리초)", example = "123")
    private Long executionTime;

    @Schema(description = "가중치", example = "10")
    private Integer weight;
}