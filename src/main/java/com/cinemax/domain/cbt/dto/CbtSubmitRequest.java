package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(description = "CBT 답안 제출 요청")
public class CbtSubmitRequest {

    @NotEmpty
    @Valid
    @Schema(description = "문항별 답안")
    private List<Answer> answers;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "문항 답안")
    public static class Answer {

        @NotNull
        @Schema(description = "문제 ID")
        private Long questionId;

        @Schema(description = "선택한 보기 orderNo (미선택이면 null)")
        private Integer selectedOrderNo;
    }
}
