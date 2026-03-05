package com.cinemax.domain.cycle.dto;

import com.cinemax.domain.syntax.dto.SyntaxDetailRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "학습 사이클 생성/수정 요청 DTO")
public class CycleRequest {

    @NotNull(message = "커리큘럼 주차 ID는 필수입니다")
    @Schema(description = "커리큘럼 주차 ID", example = "1")
    private Long curWeekId;

    @Schema(description = "문법 식별코드", example = "1")
    private Long syntaxId;

    @NotBlank(message = "사이클 제목은 필수입니다")
    @JsonProperty("title")
    @Schema(description = "사이클 제목", example = "첫 파이썬 프로그램 작성 (print)")
    private String cycleTitle;

    @JsonProperty("filename")
    @Schema(description = "파일명", example = "hello.py")
    private String fileNm;

    @JsonProperty("syntaxDetails")
    @Schema(description = "문법 상세 정보 목록")
    private List<SyntaxDetailRequest> syntaxDetails;
}
