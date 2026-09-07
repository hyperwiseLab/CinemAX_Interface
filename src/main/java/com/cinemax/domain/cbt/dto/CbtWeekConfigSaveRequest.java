package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주차별 CBT 설정 저장 요청")
public class CbtWeekConfigSaveRequest {

    @NotEmpty
    @Valid
    @Schema(description = "주차별 설정 목록")
    private List<Item> weeks;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "주차 설정 항목")
    public static class Item {

        @NotNull
        @Min(1)
        @Schema(description = "주차 번호")
        private Integer weekNo;

        @NotNull
        @Min(1)
        @Max(200)
        @Schema(description = "출제 문항 수")
        private Integer questionCount;

        @NotNull
        @Min(0)
        @Max(100)
        @Schema(description = "합격 기준 정답률(%)")
        private Double passScore;

        @NotNull
        @Schema(description = "사용 여부")
        private Boolean useYn;
    }
}
