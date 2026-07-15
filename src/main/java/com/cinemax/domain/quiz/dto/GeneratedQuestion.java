package com.cinemax.domain.quiz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * AI가 생성해 반환하는 문항 JSON 파싱용 DTO (내부용).
 * 프롬프트에서 지정한 스키마와 일치.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneratedQuestion {

    // "MULTIPLE" | "OX"
    private String type;

    // 문제 지문
    private String content;

    // 객관식 보기 (OX는 null/빈 목록)
    private List<String> options;

    // 객관식 정답 보기의 인덱스 (0-based)
    private Integer answerIndex;

    // OX 정답 ("O" | "X")
    private String answer;

    // 해설
    private String explanation;
}
