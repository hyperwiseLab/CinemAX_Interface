package com.cinemax.domain.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 답변 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "답변 응답")
public class AnswerResponse {

    @Schema(description = "답변 ID", example = "1")
    private Long answerId;

    @Schema(description = "질문 ID", example = "1")
    private Long questionId;

    @Schema(description = "작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "작성자 이름", example = "김교수")
    private String userName;

    @Schema(description = "작성자 이메일", example = "professor@example.com")
    private String userEmail;

    @Schema(description = "답변 내용", example = "int 변수는 'int a = 10;' 이런 형식으로 선언합니다.")
    private String content;

    @Schema(description = "생성 일시")
    private LocalDateTime createDt;

    @Schema(description = "수정 일시")
    private LocalDateTime updateDt;
}
