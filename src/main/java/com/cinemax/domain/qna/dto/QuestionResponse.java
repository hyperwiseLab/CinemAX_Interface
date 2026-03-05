package com.cinemax.domain.qna.dto;

import com.cinemax.global.enums.QuestionStatus;
import com.cinemax.global.enums.QuestionUrgency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 질문 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "질문 응답")
public class QuestionResponse {

    @Schema(description = "질문 ID", example = "1")
    private Long questionId;

    @Schema(description = "클래스 ID", example = "1")
    private Long classId;

    @Schema(description = "클래스명", example = "자바 프로그래밍")
    private String classNm;

    @Schema(description = "학년도", example = "2024")
    private Integer year;

    @Schema(description = "학기", example = "1학기")
    private String semester;

    @Schema(description = "주차별 세션 ID", example = "1")
    private Long weeklySessionId;

    @NotNull(message = "주차 번호는 필수입니다")
    @Positive(message = "주차 번호는 양수여야 합니다")
    @Schema(description = "주차 번호", example = "1")
    private Integer weekNo;

    @Schema(description = "초대 ID", example = "1")
    private Long inviteId;

    @Schema(description = "작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "작성자 이메일", example = "student@example.com")
    private String userEmail;

    @Schema(description = "질문 제목", example = "변수 선언 관련 질문입니다")
    private String title;

    @Schema(description = "질문 내용", example = "int형 변수를 어떻게 선언하나요?")
    private String content;

    @Schema(description = "태그 (쉼표로 구분)", example = "Java,변수,선언")
    private String tags;

    @Schema(description = "긴급도", example = "HIGH")
    private QuestionUrgency urgency;

    @Schema(description = "질문 상태", example = "OPEN")
    private QuestionStatus status;

    @Schema(description = "답변 목록")
    private List<AnswerResponse> answers;

    @Schema(description = "답변 개수", example = "2")
    private Integer answerCount;

    @Schema(description = "생성 일시")
    private LocalDateTime createDt;

    @Schema(description = "수정 일시")
    private LocalDateTime updateDt;
}
