package com.cinemax.domain.classes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 수업 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequest {

    @NotBlank(message = "수업명은 필수입니다")
    private String classNm;

    private String description;

    @NotNull(message = "년도는 필수입니다")
    @Positive(message = "년도는 양수여야 합니다")
    private Integer year;

    @NotBlank(message = "학기는 필수입니다")
    private String term;

    @NotNull(message = "커리큘럼 ID는 필수입니다")
    @Positive(message = "커리큘럼 ID는 양수여야 합니다")
    private Long curId;

    @Builder.Default
    private Boolean useYn = true;

    private String currentInviteId;
}
