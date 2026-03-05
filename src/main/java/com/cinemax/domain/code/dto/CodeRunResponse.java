package com.cinemax.domain.code.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코드 실행 결과 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 실행 결과")
public class CodeRunResponse {

    @Schema(description = "실행 성공 여부", example = "true")
    private Boolean success;

    @Schema(description = "표준 출력 (stdout)", example = "Hello, World!")
    private String output;

    @Schema(description = "표준 에러 (stderr)", example = "")
    private String error;

    @Schema(description = "실행 시간 (밀리초)", example = "123")
    private Long executionTime;

    @Schema(description = "종료 코드", example = "0")
    private Integer exitCode;

    @Schema(description = "실행 언어", example = "java")
    private String language;

    @Schema(description = "실행 버전", example = "15.0.2")
    private String version;

    @Schema(description = "에러 메시지 (실패 시)", example = "")
    private String message;

    // 성공 응답 생성
    public static CodeRunResponse success(String output, Long executionTime, String language, String version) {
        return CodeRunResponse.builder()
                .success(true)
                .output(output)
                .error("")
                .executionTime(executionTime)
                .exitCode(0)
                .language(language)
                .version(version)
                .build();
    }

    // 실패 응답 생성
    public static CodeRunResponse failure(String error, String message, String language) {
        return CodeRunResponse.builder()
                .success(false)
                .output("")
                .error(error)
                .message(message)
                .language(language)
                .build();
    }
}
