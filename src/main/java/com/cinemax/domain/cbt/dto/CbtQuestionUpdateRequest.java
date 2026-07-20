package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 문제 수정 요청 (보기 전체 교체)")
public class CbtQuestionUpdateRequest {

    @NotNull
    @Schema(description = "과목 ID")
    private Long subjectId;

    @NotBlank
    @Schema(description = "지문")
    private String content;

    @Size(max = 1000)
    @Schema(description = "해설")
    private String explanation;

    @NotNull
    @Schema(description = "사용 여부")
    private Boolean useYn;

    @NotEmpty
    @Valid
    @Schema(description = "보기 목록(전체 교체)")
    private List<CbtQuestionBulkRequest.OptionItem> options;
}
