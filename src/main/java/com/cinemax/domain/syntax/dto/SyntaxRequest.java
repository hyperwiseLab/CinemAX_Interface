package com.cinemax.domain.syntax.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Syntax 생성/수정 요청 DTO - JSON의 syntaxId 객체에 해당
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문법 그룹 요청")
public class SyntaxRequest {

    @JsonProperty("syntax_detail")
    @Schema(description = "문법 상세 정보 목록")
    private List<SyntaxDetailRequest> syntaxDetail;
}
