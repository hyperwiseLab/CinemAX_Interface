package com.cinemax.domain.testcase.dto;

import com.cinemax.domain.testcase.entity.TestCase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 테스트 케이스 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "테스트 케이스 응답")
public class TestCaseResponse {

    @Schema(description = "테스트 케이스 ID", example = "1")
    private Long testCaseId;

    @Schema(description = "과제 ID", example = "1")
    private Long taskId;

    @Schema(description = "사이클 ID", example = "1")
    private Long cycleId;

    @Schema(description = "시작 코드 (템플릿)")
    private String startCode;

    @Schema(description = "테스트 코드")
    private String testCode;

    @Schema(description = "입력 데이터", example = "5")
    private String inputText;

    @Schema(description = "예상 출력", example = "120")
    private String expectedOutput;

    @Schema(description = "가중치", example = "10")
    private Integer weight;

    @Schema(description = "생성 시각")
    private LocalDateTime createDt;

    @Schema(description = "수정 시각")
    private LocalDateTime updateDt;
}
