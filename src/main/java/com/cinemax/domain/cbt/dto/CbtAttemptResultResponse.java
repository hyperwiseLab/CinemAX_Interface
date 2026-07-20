package com.cinemax.domain.cbt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 회차 결과 (제출 직후 즉시 반환 + 과거 회차 복기 공용).
 * SubjectScore / Item 은 attempt 의 JSON 스냅샷 직렬화 형식과 동일하다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CBT 회차 결과")
public class CbtAttemptResultResponse {

    @Schema(description = "응시 ID")
    private Long attemptId;

    @Schema(description = "회차")
    private Integer roundNo;

    @Schema(description = "총점(100점 만점)")
    private Double totalScore;

    @Schema(description = "합격 기준 점수")
    private Double passScore;

    @Schema(description = "합격 여부")
    private Boolean passYn;

    @Schema(description = "탈락 사유 (과락 과목명 나열 또는 총점 미달)")
    private String failReason;

    @Schema(description = "제출 시간")
    private LocalDateTime submittedAt;

    @Schema(description = "과목별 점수")
    private List<SubjectScore> subjectScores;

    @Schema(description = "문항별 결과(복기)")
    private List<Item> items;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "과목별 점수")
    public static class SubjectScore {

        @Schema(description = "과목 ID")
        private Long subjectId;

        @Schema(description = "과목명")
        private String subjectNm;

        @Schema(description = "획득 점수")
        private Double score;

        @Schema(description = "과목 배점")
        private Double maxScore;

        @Schema(description = "과락 기준점")
        private Double cutScore;

        @Schema(description = "과락 적용 여부")
        private Boolean cutYn;

        @Schema(description = "과락 여부")
        private Boolean failed;

        @Schema(description = "정답 수")
        private Integer correctCount;

        @Schema(description = "출제 문항 수")
        private Integer questionCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "문항별 결과")
    public static class Item {

        @Schema(description = "문제 ID")
        private Long questionId;

        @Schema(description = "과목 ID")
        private Long subjectId;

        @Schema(description = "과목명")
        private String subjectNm;

        @Schema(description = "지문")
        private String content;

        @Schema(description = "해설")
        private String explanation;

        @Schema(description = "보기 목록")
        private List<CbtPracticeResponse.Option> options;

        @Schema(description = "선택한 보기 orderNo")
        private Integer selectedOrderNo;

        @Schema(description = "정답 보기 orderNo")
        private Integer correctOrderNo;

        @Schema(description = "정답 여부")
        private Boolean correct;
    }
}
