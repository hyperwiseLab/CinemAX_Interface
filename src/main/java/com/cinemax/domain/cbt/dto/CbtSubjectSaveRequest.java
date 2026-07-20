package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 과목 설정 일괄 저장 요청. 활성 과목의 배점 합계(문제당 점수 x 출제수)는 정확히 100점이어야 한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 과목 설정 저장 요청")
public class CbtSubjectSaveRequest {

    @NotNull
    @DecimalMin(value = "1")
    @DecimalMax(value = "100")
    @Schema(description = "최종 합격 점수(100점 기준)", example = "60")
    private Double passScore;

    @NotEmpty
    @Valid
    @Schema(description = "과목 목록(전체 교체 저장)")
    private List<Item> subjects;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "CBT 과목 항목")
    public static class Item {

        @Schema(description = "과목 ID (신규면 null)")
        private Long subjectId;

        @NotBlank
        @Size(max = 100)
        @Schema(description = "과목명", example = "파이썬 기초")
        private String subjectNm;

        @NotNull
        @Schema(description = "표시 순서", example = "1")
        private Integer orderNo;

        @NotNull
        @DecimalMin(value = "0.01")
        @Schema(description = "문제당 점수", example = "2.0")
        private Double pointPerQuestion;

        @NotNull
        @Min(1)
        @Schema(description = "회차당 출제 문항 수", example = "20")
        private Integer questionCount;

        @NotNull
        @DecimalMin(value = "0")
        @Schema(description = "과락 기준점(배점 내 절대점수)", example = "16")
        private Double cutScore;

        @NotNull
        @Schema(description = "과락 적용 여부", example = "true")
        private Boolean cutYn;

        @NotNull
        @Schema(description = "사용 여부", example = "true")
        private Boolean useYn;
    }
}
