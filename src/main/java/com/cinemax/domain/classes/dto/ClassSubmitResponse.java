package com.cinemax.domain.classes.dto;

import com.cinemax.domain.classes.entity.ClassSubmit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 제출 이력 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "제출 이력 응답")
public class ClassSubmitResponse {

    @Schema(description = "제출 ID", example = "1")
    private Long submitId;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "과제 ID", example = "1")
    private Long taskId;

    @Schema(description = "수업 ID", example = "1")
    private Long classId;

    @Schema(description = "주차 수업 ID", example = "1")
    private Long weeklySessionId;

    @Schema(description = "사이클 ID", example = "1")
    private Long cycleId;

    @Schema(description = "제출 시각")
    private LocalDateTime submitAt;

    @Schema(description = "결과 (통과/실패)", example = "true")
    private Boolean result;

    @Schema(description = "점수", example = "85.5")
    private BigDecimal score;

    @Schema(description = "상세 결과 (JSON)")
    private String detailJson;

    @Schema(description = "첫 제출 여부", example = "true")
    private Boolean isFirstEval;

    @Schema(description = "제출 횟수", example = "3")
    private Integer submitNum;

    @Schema(description = "제출 여부", example = "true")
    private Boolean submitYn;

    @Schema(description = "생성 시각")
    private LocalDateTime createDt;

    @Schema(description = "수정 시각")
    private LocalDateTime updateDt;

    /**
     * Entity -> Response DTO 변환
     */
    public static ClassSubmitResponse from(ClassSubmit classSubmit) {
        return ClassSubmitResponse.builder()
                .submitId(classSubmit.getSubmitId())
                .userId(classSubmit.getUserId())
                .taskId(classSubmit.getTaskId())
                .classId(classSubmit.getClassId())
                .weeklySessionId(classSubmit.getWeeklySessionId())
                .cycleId(classSubmit.getCycleId())
                .submitAt(classSubmit.getSubmitAt())
                .result(classSubmit.getResult())
                .score(classSubmit.getScore())
                .detailJson(classSubmit.getDetailJson())
                .isFirstEval(classSubmit.getIsFirstEval())
                .submitNum(classSubmit.getSubmitNum())
                .submitYn(classSubmit.getSubmitYn())
                .createDt(classSubmit.getCreateDt())
                .updateDt(classSubmit.getUpdateDt())
                .build();
    }
}
