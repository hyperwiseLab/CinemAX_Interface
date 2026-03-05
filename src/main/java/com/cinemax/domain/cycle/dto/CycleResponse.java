package com.cinemax.domain.cycle.dto;

import com.cinemax.domain.cycle.entity.Cycle;
import com.cinemax.domain.syntax.dto.SyntaxDetailDto;
import com.cinemax.domain.syntax.mapper.SyntaxDetailMapper;
import com.cinemax.domain.task.dto.TaskResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "학습 사이클 응답 DTO")
public class CycleResponse {

    // SyntaxDetailMapper 인스턴스 (MapStruct가 생성한 구현체 사용)
    private static final SyntaxDetailMapper syntaxDetailMapper = Mappers.getMapper(SyntaxDetailMapper.class);

    @Schema(description = "사이클 ID")
    private Long cycleId;

    @Schema(description = "커리큘럼 주차 ID")
    private Long curWeekId;

    @Schema(description = "문법 식별코드")
    private Long syntaxId;

    @JsonProperty("title")
    @Schema(description = "사이클 제목", example = "첫 파이썬 프로그램 작성 (print)")
    private String cycleTitle;

    @JsonProperty("filename")
    @Schema(description = "파일명", example = "hello.py")
    private String fileNm;

    @JsonProperty("syntaxDetails")
    @Schema(description = "문법 상세 정보 목록")
    private List<SyntaxDetailDto> syntaxDetails;

    @JsonProperty("task")
    @Schema(description = "포함된 Task 목록 (모드별)")
    private List<TaskResponse> tasks;

    @Schema(description = "생성일시")
    private LocalDateTime createDt;

    @Schema(description = "수정일시")
    private LocalDateTime updateDt;

    /**
     * Cycle 엔티티를 CycleResponse로 변환
     * SyntaxDetailMapper를 사용하여 SyntaxDetail 변환
     */
    public static CycleResponse from(Cycle cycle) {
        // Cycle에 속한 첫 번째 Syntax의 SyntaxDetail 목록을 가져옴
        List<SyntaxDetailDto> syntaxDetailDtos = cycle.getSyntaxes().isEmpty()
            ? Collections.emptyList()
            : syntaxDetailMapper.toDto(cycle.getSyntaxes().get(0).getSyntaxDetails());

        return CycleResponse.builder()
                .cycleId(cycle.getCycleId())
                .curWeekId(cycle.getCurWeekId())
                .syntaxId(cycle.getSyntaxId())
                .cycleTitle(cycle.getCycleTitle())
                .fileNm(cycle.getFileNm())
                .syntaxDetails(syntaxDetailDtos)
                .createDt(cycle.getCreateDt())
                .updateDt(cycle.getUpdateDt())
                .build();
    }

    /**
     * Cycle 엔티티를 Task 목록과 함께 CycleResponse로 변환
     */
    public static CycleResponse of(Cycle cycle, List<TaskResponse> tasks) {
        CycleResponse response = from(cycle);
        response.setTasks(tasks);
        return response;
    }
}
