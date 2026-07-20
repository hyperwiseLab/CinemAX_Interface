package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 문제 대량 등록 결과")
public class CbtBulkResultResponse {

    @Schema(description = "요청 문항 수")
    private Integer totalCount;

    @Schema(description = "등록 성공 수")
    private Integer successCount;

    @Schema(description = "실패 수")
    private Integer failCount;

    @Schema(description = "실패 상세 (index는 요청 배열의 0-base 위치)")
    private List<Failure> failures;

    @Schema(description = "과목별 등록 성공 수")
    private Map<String, Integer> subjectCounts;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "실패 항목")
    public static class Failure {

        @Schema(description = "요청 배열 내 위치(0-base)")
        private Integer index;

        @Schema(description = "실패 사유")
        private String reason;
    }
}
