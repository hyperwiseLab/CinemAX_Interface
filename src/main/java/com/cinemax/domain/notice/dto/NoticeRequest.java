package com.cinemax.domain.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 생성/수정 요청 DTO")
public class NoticeRequest {

    @Schema(description = "수업 ID (전체 공지사항인 경우 null)", example = "1")
    private Long classId;

    @NotBlank(message = "제목은 필수입니다")
    @Schema(description = "공지사항 제목", example = "1주차 과제 안내")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    @Schema(description = "공지사항 내용", example = "1주차 과제는 다음 주 월요일까지 제출해주세요.")
    private String content;

    @Schema(description = "중요 공지사항 여부", example = "false")
    @Builder.Default
    private Boolean isImportant = false;
}
