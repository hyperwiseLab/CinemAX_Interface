package com.cinemax.domain.codesnapshot.dto;

import com.cinemax.global.enums.LanguageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코드 저장 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 저장 요청")
public class CodeSaveRequest {

    @NotNull(message = "과제 ID는 필수입니다")
    @Schema(description = "과제 ID", example = "1")
    private Long taskId;

    @NotNull(message = "주차 수업 ID는 필수입니다")
    @Schema(description = "주차 수업 ID", example = "1")
    private Long weeklySessionId;

    @NotNull(message = "사이클 ID는 필수입니다")
    @Schema(description = "사이클 ID", example = "1")
    private Long cycleId;

    @NotNull(message = "언어는 필수입니다")
    @Schema(description = "프로그래밍 언어", example = "JAVA")
    private LanguageType lang;

    @NotBlank(message = "코드 내용은 필수입니다")
    @Schema(description = "코드 내용", example = "public class Main { ... }")
    private String content;
}
