package com.cinemax.domain.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * QnA 변경 웹소켓 이벤트. /topic/qna/{weeklySessionId} 로 발행되며,
 * 구독 중인 모니터링 화면은 이 이벤트를 받으면 질문 목록을 1회 재조회한다 (5초 폴링 대체).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "QnA 변경 이벤트 메시지")
public class QnaEventMessage {

    public enum Type {
        QUESTION_CREATED,
        QUESTION_UPDATED,
        QUESTION_STATUS_CHANGED,
        QUESTION_DELETED,
        ANSWER_CREATED,
        ANSWER_UPDATED,
        ANSWER_DELETED,
    }

    @Schema(description = "이벤트 종류")
    private Type type;

    @Schema(description = "질문 ID")
    private Long questionId;

    @Schema(description = "주차 수업 ID")
    private Long weeklySessionId;
}
